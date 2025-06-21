package com.hust.ict.aims.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class RushOrderRequestDTO {

    @NotNull(message = "Delivery time must not be null")
    @FutureOrPresent(message = "Delivery time must be present or future")
    private LocalDateTime deliveryTime;

    @NotBlank(message = "Delivery instruction must not be blank")
    private String deliveryInstruction;

    public RushOrderRequestDTO() {
    }

    public RushOrderRequestDTO(LocalDateTime deliveryTime, String deliveryInstruction) {
        this.deliveryTime = deliveryTime;
        this.deliveryInstruction = deliveryInstruction;
    }

    public @NotNull(message = "Delivery time must not be null") @FutureOrPresent(message = "Delivery time must be present or future") LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(@NotNull(message = "Delivery time must not be null") @FutureOrPresent(message = "Delivery time must be present or future") LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public @NotBlank(message = "Delivery instruction must not be blank") String getDeliveryInstruction() {
        return deliveryInstruction;
    }

    public void setDeliveryInstruction(@NotBlank(message = "Delivery instruction must not be blank") String deliveryInstruction) {
        this.deliveryInstruction = deliveryInstruction;
    }
}