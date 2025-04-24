package com.eugene.product_service.mapper;

import com.eugene.product_service.dto.ProductDTO;
// import com.eugene.product_service.dto.ProductPurchaseDTO;
import com.eugene.product_service.dto.ProductPurchaseResponseDTO;
import com.eugene.product_service.dto.ProductResponseDTO;
import com.eugene.product_service.entity.Category;
import com.eugene.product_service.entity.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductMapper {

    // Converts a product entity from the DB to a productResponseDTO  which is what is shown to the client
    public ProductResponseDTO toProductResponseDTO(Product product) {

        if (product == null){
            return null;
        }

        // Since the ProductResponseDTO is a record, I won't be using the Builder class.
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAvailableQuantity(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getDescription()
        );

    }


    // Converting the ProductDTO record to a Product entity class
    public Product toProduct(ProductDTO productDTO) {

        if (productDTO == null){
            return null;
        }

        return Product.builder()
                .id(productDTO.id())
                .name(productDTO.name())
                .description(productDTO.description())
                .availableQuantity(productDTO.availableQuantity())
                .price(productDTO.price())
                 /**
                 * We are getting the category itself that has been saved in the DB.
                 * We're using the builder pattern for Category because we need to create a reference to an existing category without loading all its data.
                 *  - When creating/updating a product, the  ProductDTO only contains the categoryId (not the full category details)
                 *  - We need to associate the product with its category in the database
                 *  - In JPA/Hibernate, we only need the ID to establish this relationship, we don't need to load the entire category object
                 * Using the builder() pattern for category creates a lightweight reference to the category object with just the ID.
                 * When JPA saves the product, it will use this ID to create the foreign key relationship in the database.
                 */
                .category(
                        Category.builder()
                                .id(productDTO.categoryId())
                                .build()
                )

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
