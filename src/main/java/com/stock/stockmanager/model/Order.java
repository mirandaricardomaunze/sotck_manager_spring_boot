package com.stock.stockmanager.model;

import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount; // Change to BigDecimal
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private String customerName;
    private String deliveryAddress;
    private String customerEmail;
    private String customerContact;
    private String customerNuit;
    private String paymentMethod;
    private String companyName;
    private String warehouseName;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    private String notes;
    @PrePersist
    public void prePersist() {
        if (this.orderNumber == null || this.orderNumber.isEmpty()) {
            this.orderNumber = "ORD-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
    }
}