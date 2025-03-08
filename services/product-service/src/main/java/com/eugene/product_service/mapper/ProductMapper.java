package com.eugene.product_service.mapper;

import com.eugene.product_service.dto.ProductDTO;
import com.eugene.product_service.dto.ProductPurchaseDTO;
import com.eugene.product_service.dto.ProductPurchaseResponseDTO;
import com.eugene.product_service.entity.Category;
import com.eugene.product_service.entity.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {

    public ProductDTO toProductDTO(Product product) {
        if (product == null){
            return null;
        }
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAvailableQuantity(),
                product.getPrice(),
                product.getCategory()
        );

    }

    public Product toProduct(ProductDTO productDTO) {
        if (productDTO == null){
            return null;
        }

        return Product.builder()
                .id(productDTO.id())
                .name(productDTO.name())
                .description(productDTO.description())
                .price(productDTO.price())
                .category(productDTO.category())
                .build();
    }


    public ProductPurchaseResponseDTO toProductPurchaseResponse(Product product, double quantity){

        if(product == null){
            return null;
        }

        return new ProductPurchaseResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                quantity
        );
    }
}
