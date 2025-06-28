package com.hust.ict.aims.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// Functional Cohesion, no violate SRP because it has only single responsibility: representing information of RushOrder
@Setter
@Getter
@Entity
@Table(name = "rush_order")
@PrimaryKeyJoinColumn(name = "order_id")
public class RushOrder extends Orders{

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @Column(name = "delivery_instruction")
    private String deliveryInstruction;

    public RushOrder() {

    }

    public RushOrder(Transaction transaction, Invoice invoice, DeliveryInfo deliveryInfo, String status, LocalDateTime deliveryTime, String deliveryInstruction) {
        super(transaction, invoice, deliveryInfo, status);
        this.deliveryTime = deliveryTime;
        this.deliveryInstruction = deliveryInstruction;
    }

    @Override
    public String toString() {
        return "RushOrder{" +
                "deliveryTime=" + deliveryTime +
                ", deliveryInstruction='" + deliveryInstruction + '\'' +
                '}';
    }
}