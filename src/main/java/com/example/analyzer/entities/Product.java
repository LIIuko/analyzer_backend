package com.example.analyzer.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "product")
@Entity
@Getter
@Setter
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String shop;
    @Column(nullable = false)
    private String url;

    public Product(String shop, String url, int price) {
        this.shop = shop;
        this.url = url;
        this.price = price;
    }

    @Column(nullable = false)
    private int price;

    public Product() {

    }
}
