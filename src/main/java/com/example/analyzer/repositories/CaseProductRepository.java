package com.example.analyzer.repositories;

import com.example.analyzer.entities.CaseProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaseProductRepository extends JpaRepository<CaseProducts, Long> {
}
