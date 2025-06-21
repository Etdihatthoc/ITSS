package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.InvoiceRequest;
import com.hust.ict.aims.model.Cart;
import com.hust.ict.aims.model.CartItem;
import com.hust.ict.aims.model.DeliveryInfo;
import com.hust.ict.aims.repository.CartRepository;
import com.hust.ict.aims.repository.DeliveryInfoRepository;
import com.hust.ict.aims.repository.InvoiceRepository;
import com.hust.ict.aims.model.Invoice;
import com.hust.ict.aims.service.CartService;
import com.hust.ict.aims.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CartRepository cartRepository;
    private final DeliveryInfoRepository deliveryInfoRepository;
    private final CartService cartService;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              CartRepository cartRepository,
                              DeliveryInfoRepository deliveryInfoRepository, CartService cartService) {
        this.invoiceRepository = invoiceRepository;
        this.cartRepository = cartRepository;
        this.deliveryInfoRepository = deliveryInfoRepository;
        this.cartService = cartService;
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice findById(int id) {
        Optional<Invoice> result = invoiceRepository.findById(id);
        return result.orElseThrow(() -> new RuntimeException("Invoice not found - " + id));
    }

    @Override
    public Invoice createInvoice(InvoiceRequest invoiceData) {
        // Create cart from cart data
        Cart cart = cartService.createCartSnapshot(invoiceData.getCart());

        // Create invoice
        Invoice invoice = new Invoice();
        invoice.setCart(cart);
        invoice.setTotalProductPriceAfterVAT(invoiceData.getTotalProductPriceAfterVAT());
        invoice.setTotalAmount(invoiceData.getTotalAmount());
        invoice.setDeliveryFee(invoiceData.getDeliveryFee());

        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice save(Long cartId, Long deliveryId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));

        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found: " + deliveryId));

        float totalBeforeVAT = cart.getTotalProductPriceBeforeVAT();

        float VAT_RATE = 0.1f;
        float totalAfterVAT = totalBeforeVAT * (1 + VAT_RATE);


        float deliveryFee = calculateDeliveryFee(cartId, deliveryId);

        Invoice invoice = new Invoice();
        invoice.setCart(cart);
        invoice.setDeliveryFee(deliveryFee);
        invoice.setTotalProductPriceAfterVAT(totalAfterVAT);
        invoice.setTotalAmount(totalAfterVAT + deliveryFee);
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice update(int invoiceId, Long cartId, Long deliveryId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found - " + invoiceId));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found: " + cartId));

        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found: " + deliveryId));

        float totalBeforeVAT = cart.getTotalProductPriceBeforeVAT();

        float VAT_RATE = 0.1f;
        float totalAfterVAT = totalBeforeVAT * (1 + VAT_RATE);

        float deliveryFee = calculateDeliveryFee(cartId, deliveryId);

        invoice.setCart(cart);
        invoice.setDeliveryFee(deliveryFee);
        invoice.setTotalProductPriceAfterVAT(totalAfterVAT);
        invoice.setTotalAmount(totalAfterVAT + deliveryFee);

        return invoiceRepository.save(invoice);
    }

    @Override
    public void deleteById(int id) {
        invoiceRepository.deleteById(id);
    }

    private float calculateDeliveryFee(Long cartId, Long deliveryId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found - " + cartId));
        DeliveryInfo deliveryInfo = deliveryInfoRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery info not found: " + deliveryId));

        float itemTotal = (float) cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getCurrentPrice() * i.getQuantity())
                .sum();

        float maxWeight = (float) cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getWeight() * i.getQuantity())
                .max().orElse(0.0);

        boolean isInnerCity = deliveryInfo.getDeliveryAddress().toLowerCase().contains("hanoi") ||
                deliveryInfo.getDeliveryAddress().toLowerCase().contains("ho chi minh");

        float baseRate = isInnerCity ? 22000 : 30000;
        float baseWeight = isInnerCity ? 3.0f : 0.5f;
        float extraPerUnit = 2500;

        float deliveryFee = baseRate;
        if (maxWeight > baseWeight) {
            float extraWeight = maxWeight - baseWeight;
            int extraUnits = (int) Math.ceil(extraWeight / 0.5);
            deliveryFee += extraUnits * extraPerUnit;
        }

        if (itemTotal > 100000) {
            deliveryFee = Math.max(deliveryFee - 25000, 0);
        }

        return deliveryFee;
    }
}
