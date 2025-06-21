package com.hust.ict.aims.subsystem;
import com.hust.ict.aims.model.Transaction;

import java.io.UnsupportedEncodingException;

public interface IPayment {
    public void payOder(int amount, long orderId);
}
