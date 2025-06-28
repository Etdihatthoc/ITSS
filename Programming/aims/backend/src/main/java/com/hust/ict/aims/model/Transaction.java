package com.hust.ict.aims.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.hust.ict.aims.config.HashMapToJsonConverter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionid")
    private int transactionId;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "transactionno", nullable = false)
    private String transactionNo;

    @Column(name = "paydate", nullable = false)
    private LocalDateTime payDate;

    @Column(name = "transactionstatus")
    private String transactionStatus; // PENDDING //SUCCESSFUL // REJECTED //APPROVED

    @Column(name = "gateway")
    private String gateway;

//    @Convert(converter = HashMapToJsonConverter.class)
    @Convert(converter = HashMapToJsonConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_params", columnDefinition = "jsonb")
    private Map<String, String> additionalParams = new HashMap<>();

    public Transaction() {

    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionID) { this.transactionId = transactionID; }


    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }


    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }


    public LocalDateTime getPayDate() { return payDate; }
    public void setPayDate(LocalDateTime payDate) { this.payDate = payDate; }


    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Map<String, String> getAdditionalParams() {
        return additionalParams;
    }

    public void setAdditionalParams(Map<String, String> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public String getGateway() { return gateway; }
    public void setGateway(String gateway) { this.gateway = gateway; }

    public Transaction(float amount, String gateway, String transactionNo, String transactionStatus, String errorMessage, LocalDateTime payDate, Map<String , String> addtionalParams) {
        this.amount = amount;
        this.gateway = gateway;
        this.transactionNo = transactionNo;
        this.transactionStatus = transactionStatus;
        this.payDate = payDate;
        this.additionalParams = addtionalParams;
    }


}
