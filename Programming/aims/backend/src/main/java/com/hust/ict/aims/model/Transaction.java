package com.hust.ict.aims.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionid")
    private int transactionID;

    @Column(name = "bankcode", nullable = false)
    private String bankCode;

    @Column(name = "amount", nullable = false)
    private float amount;

    @Column(name = "banktransactionno", nullable = false)
    private String bankTransactionNo;

    @Column(name = "transactionno", nullable = false)
    private String transactionNo;

    @Column(name = "cardtype", nullable = false)
    private String cardType;

    @Column(name = "paydate", nullable = false)
    private LocalDateTime payDate;

    @Column(name = "errormessage")
    private String errorMessage;

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @Column(name = "transactionstatus")
    private String transactionStatus;

    public String getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(String transactionInfo) {
        this.transactionInfo = transactionInfo;
    }

    @Column(name = "transactioninfo")
    private String transactionInfo;

    public Transaction() {}

    public Transaction(String bankCode, float amount, String bankTransactionNo,
                       String transactionNo, String cardType, LocalDateTime payDate, String errorMessage) {
        this.bankCode = bankCode;
        this.amount = amount;
        this.bankTransactionNo = bankTransactionNo;
        this.transactionNo = transactionNo;
        this.cardType = cardType;
        this.payDate = payDate;
        this.errorMessage = errorMessage;
        this.transactionInfo = transactionInfo;
    }

    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID = transactionID; }

    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }

    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }

    public String getBankTransactionNo() { return bankTransactionNo; }
    public void setBankTransactionNo(String bankTransactionNo) { this.bankTransactionNo = bankTransactionNo; }

    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public LocalDateTime getPayDate() { return payDate; }
    public void setPayDate(LocalDateTime payDate) { this.payDate = payDate; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", bankCode='" + bankCode + '\'' +
                ", amount=" + amount +
                ", bankTransactionNo='" + bankTransactionNo + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", cardType='" + cardType + '\'' +
                ", payDate=" + payDate +
                ", errorMessage='" + errorMessage + '\'' +
                ", transactionInfo='" + transactionInfo + '\'' +
                '}';
    }

    public Integer getId() {
        return transactionID;
    }

    public void setId(int id) {
        this.transactionID = id;
    }
}
