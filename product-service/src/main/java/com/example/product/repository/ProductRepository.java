package com.example.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // search by name or category with pagination
    Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
        String name, String category, Pageable pageable
    );
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
}