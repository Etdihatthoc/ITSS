package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.DeliveryInfo;
import com.hust.ict.aims.repository.DeliveryInfoRepository;
import com.hust.ict.aims.service.DeliveryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


@Service
public class DeliveryInfoServiceImpl implements DeliveryInfoService {

    private final DeliveryInfoRepository deliveryInfoRepository;

    @Autowired
    public DeliveryInfoServiceImpl(DeliveryInfoRepository deliveryInfoRepository) {
        this.deliveryInfoRepository = deliveryInfoRepository;
    }

    @Override
    public List<DeliveryInfo> findAll() {
        return deliveryInfoRepository.findAll();
    }

    @Override
    public DeliveryInfo findById(Long id) {
        Optional<DeliveryInfo> result = deliveryInfoRepository.findById(id);
        return result.orElse(null);
    }

    @Override
    public DeliveryInfo save(DeliveryInfo deliveryInfo) {
        validateDeliveryInfo(deliveryInfo);
        deliveryInfoRepository.save(deliveryInfo);
        return deliveryInfo;
    }

    @Override
    public void deleteById(Long id) {
        deliveryInfoRepository.deleteById(id);
    }

    private void validateDeliveryInfo(DeliveryInfo info) {
        if (info == null) {
            throw new IllegalArgumentException("DeliveryInfo must not be null");
        }

        if (isBlank(info.getRecipientName())) {
            throw new IllegalArgumentException("Recipient name is required");
        }

        if (!isValidEmail(info.getEmail())) {
            throw new IllegalArgumentException("Email is not valid");
        }

        if (isBlank(info.getDeliveryAddress())) {
            throw new IllegalArgumentException("Address is required");
        }

        if (info.getDeliveryAddress().length() > 255) {
            throw new IllegalArgumentException("Address is too long");
        }
        if (isBlank(info.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (!isValidPhoneNumber(info.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

        private boolean isValidPhoneNumber(String phone) {
        return Pattern.matches("^\\d{9,15}$", phone);
    }

    private boolean isValidEmail(String email) {
        if (email == null) return false;
        // Basic regex for email validation
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }
}