package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RushOrderDTO {
    private LocalDateTime deliveryTime;
    private String deliveryInstruction;
}