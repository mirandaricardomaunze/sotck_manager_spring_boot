package com.stock.stockmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private  Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private String website;
    private String taxId;
    private String registrationNumber;
    private String logoUrl;
    private String description;
    private String country;
    private String city;
    private String postalCode;
    private String industry;
    private String contactEmail;
    private String contactPhone;

    @OneToMany (mappedBy = "company")
    private List<User> users;

    @OneToMany (mappedBy = "company")
    private List<Product> products;

    @OneToMany (mappedBy = "company")
    private List<Order> ordersList;

    @OneToMany (mappedBy = "company")
    private List<Supplier> suppliersList;

    @OneToMany (mappedBy = "company")
    private List<Warehouse> wharehouses;

    @OneToMany (mappedBy = "company")
    private List<Movement> movements;

}
