package com.example.product.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 10) {
            log.info("Database already seeded — skipping");
            return;
        }

        try {
            RestTemplate restTemplate = new RestTemplate();

            // fetch 100 products from dummyjson
            Map response = restTemplate.getForObject(
                "https://dummyjson.com/products?limit=100",
                Map.class
            );

            List<Map> dummyProducts = (List<Map>) response.get("products");
            List<Product> products = new ArrayList<>();

            for (Map p : dummyProducts) {
                Product product = new Product();
                product.setName((String) p.get("title"));
                product.setDescription((String) p.get("description"));
                product.setPrice(BigDecimal.valueOf(
                    ((Number) p.get("price")).doubleValue()
                ));
                product.setStock(((Number) p.get("stock")).intValue());
                product.setCategory(capitalize((String) p.get("category")));
                product.setImageUrl((String) p.get("thumbnail")); // real image URL
                product.setModelUrl(null);
                products.add(product);
            }

            productRepository.saveAll(products);
            log.info("✅ Seeded {} real products with images", products.size());

        } catch (Exception e) {
            log.error("Seeding failed: {}", e.getMessage());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}