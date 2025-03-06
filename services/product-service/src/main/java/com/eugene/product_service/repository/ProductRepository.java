package com.eugene.product_service.repository;


import com.eugene.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Returns a list of products in order based on the list of productIds from the ProductPurchaseDTO
    List<Product> findAllByIdInOrderById(List<Integer> productIds);
}
