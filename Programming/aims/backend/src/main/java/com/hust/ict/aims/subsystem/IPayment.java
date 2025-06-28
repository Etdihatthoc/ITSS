package com.hust.ict.aims.subsystem;
import com.hust.ict.aims.exception.PaymentException;
import com.hust.ict.aims.model.Transaction;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public interface IPayment {
    public void payOrder(Transaction trans);
}
