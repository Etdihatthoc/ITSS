package com.hust.ict.aims.subsystem.PaymentSubsystem.dto;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class PayRequest {
    public abstract String getPaymentUrl(Map<String, String> parameters) throws UnsupportedEncodingException;
}
