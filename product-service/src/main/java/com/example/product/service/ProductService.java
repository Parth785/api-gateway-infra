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

    public ProductResponse createProduct(CreateProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());

        Product saved = productRepository.save(product);

        return new ProductResponse(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                saved.getStock()
        );
    }

    public ProductResponse getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }

    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(p -> new ProductResponse(
                        p.getId(),
                        p.getName(),
                        p.getPrice(),
                        p.getStock()))
                .toList();
    }

}
