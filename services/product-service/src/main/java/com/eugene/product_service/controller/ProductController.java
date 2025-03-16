package com.eugene.product_service.controller;

import com.eugene.product_service.dto.ProductDTO;
import com.eugene.product_service.dto.ProductPurchaseDTO;
import com.eugene.product_service.dto.ProductPurchaseResponseDTO;
import com.eugene.product_service.dto.ProductResponseDTO;
import com.eugene.product_service.entity.Product;
import com.eugene.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    @Autowired
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        return ResponseEntity.ok(productService.createProduct(productDTO));
    }

    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchaseResponseDTO>> purchaseProducts(@RequestBody List<ProductPurchaseDTO> productPurchaseDTO) {
        return ResponseEntity.ok(productService.purchaseProducts(productPurchaseDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProductById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> findAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }
}
