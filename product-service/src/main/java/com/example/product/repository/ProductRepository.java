package com.example.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // search by name or category with pagination
    Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
        String name, String category, Pageable pageable
    );
    Page<Product> findByCategoryIgnoreCase(String category, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock < 10")
    Long countLowStock();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock = 0")
    Long countOutOfStock();
}