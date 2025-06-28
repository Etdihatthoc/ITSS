package com.hust.ict.aims.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryInfoDTO {
    private String deliveryAddress;
    private String province;
    private String phoneNumber;
    private String recipientName;
    private String email;
    private String district;
}
