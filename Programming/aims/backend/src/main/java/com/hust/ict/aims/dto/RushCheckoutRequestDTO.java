package com.hust.ict.aims.dto;

import com.hust.ict.aims.model.DeliveryInfo;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RushCheckoutRequestDTO {
    private DeliveryInfo deliveryInfo;
    private InvoiceRequest invoiceRequest;
    private TransactionRequest transactionRequest;
    private String status;

    @NotNull(message = "Delivery time must not be null")
    @FutureOrPresent(message = "Delivery time must be present or future")
    private LocalDateTime deliveryTime;

    @NotBlank(message = "Delivery instruction must not be blank")
    private String deliveryInstruction;
}
