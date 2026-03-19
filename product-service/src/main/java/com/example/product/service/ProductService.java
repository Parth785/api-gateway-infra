package com.example.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.product.dto.CreateProductRequest;
import com.example.product.dto.ProductResponse;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getStock(),
            product.getCategory(),
            product.getDescription(),
            product.getImageUrl(),
            product.getModelUrl(),
            product.getCreatedAt()
        );
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setModelUrl(request.getModelUrl());
        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getDescription(),
                product.getImageUrl(),
                product.getModelUrl(),
                product.getCreatedAt()
        );
    
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

}
