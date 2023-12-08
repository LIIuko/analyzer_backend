package com.example.analyzer.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table(name = "case_product")
@Entity
@Getter
@Setter
public class CaseProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    public CaseProducts(String title, Set<Product> products) {
        this.title = title;
        this.products = products;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Set<Product> products = new HashSet<>();

    public CaseProducts() {

    }
}
