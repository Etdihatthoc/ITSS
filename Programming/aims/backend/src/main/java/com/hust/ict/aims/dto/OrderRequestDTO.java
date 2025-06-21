package com.hust.ict.aims.dto;

import jakarta.validation.constraints.NotNull;

public class OrderRequestDTO {

    @NotNull(message = "Transaction ID is required")
    private Integer transactionId;

    @NotNull(message = "Invoice ID is required")
    private Integer invoiceId;

    @NotNull(message = "Delivery ID is required")
    private Integer deliveryId;

    @NotNull(message = "Status is required")
    private String status;

    public OrderRequestDTO() {}

    public OrderRequestDTO(Integer transactionId, Integer invoiceId, Integer deliveryId, String status) {
        this.transactionId = transactionId;
        this.invoiceId = invoiceId;
        this.deliveryId = deliveryId;
        this.status = status;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}