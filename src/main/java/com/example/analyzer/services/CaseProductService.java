package com.example.analyzer.services;

import com.example.analyzer.entities.CaseProducts;
import com.example.analyzer.repositories.CaseProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseProductService {
    @Autowired
    private CaseProductRepository caseProductRepository;

    public List<CaseProducts> getAll(){
        return caseProductRepository.findAll();
    }

    public CaseProducts getById(Long id){
        return caseProductRepository.findById(id).orElseThrow();
    }

    public void addProduct(CaseProducts caseProducts){
        caseProductRepository.save(caseProducts);
    }

    public void deleteProduct(Long id){
        caseProductRepository.deleteById(id);
    }

    public void updateProduct(CaseProducts caseProducts){
        caseProductRepository.flush();
    }
}
