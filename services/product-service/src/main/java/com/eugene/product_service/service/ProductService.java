package com.eugene.product_service.service;


import com.eugene.product_service.dto.ProductDTO;
import com.eugene.product_service.dto.ProductPurchaseDTO;
import com.eugene.product_service.dto.ProductPurchaseResponseDTO;
import com.eugene.product_service.entity.Product;
import com.eugene.product_service.exception.ProductPurchaseException;
import com.eugene.product_service.mapper.ProductMapper;
import com.eugene.product_service.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;


    public ProductDTO createProduct(ProductDTO productDTO) {

        Product product = productMapper.toProduct(productDTO);

        Product newProduct = productRepository.save(product);

        return productMapper.toProductDTO(newProduct);
    }


    /**
     * Take a list of productPurchaseDTOs*/
    public List<ProductPurchaseResponseDTO> purchaseProducts(List<ProductPurchaseDTO> productPurchaseDTO) {

        // Extract the productId from each productPurchaseDTO(products I am purchasing )in the list and collect them into a list called productIds
        List<Integer> productIds = productPurchaseDTO.stream().map(ProductPurchaseDTO::productId).toList();

        // Find all products stored in the DB available for purchase
        List<Product> storedProducts = productRepository.findAllByIdInOrderById(productIds);

        /**
         * Check if the number of products I want to buy using the productsIds is same to the number of storedProducts in the DB
         * If not, return a ProductPurchaseException */
        if(productIds.size() != storedProducts.size()) {
            throw new ProductPurchaseException("One or more products are not available for purchase");
        }

        // Creates a list of products user wants to purchase and sorts them based on their productIds
        List<ProductPurchaseDTO> purchaseProducts = productPurchaseDTO.stream().sorted(Comparator.comparing(ProductPurchaseDTO::productId)).toList();

        // Create an empty list of purchasedProducts that will be filled with the products the user buys.
        List<ProductPurchaseResponseDTO> purchasedProducts = new ArrayList<>();


        /**
         * Filters through the storedProducts in DB and returns storedProducts whose ID matches the purchaseProduct ID that the user wants to buy.
         * If the storedProduct ID does not match the purchaseProduct ID, an exception is thrown that the purchaseProduct is not found.
         * If the ID matches, it checks if the quantity of the purchaseProduct is greater than the available quantity of the storedProduct, an exception is thrown if the stock quantity is insufficient.
         * If the quantity is sufficient, we subtract the purchaseProduct quantity from the storedProduct quantity and then set the remaining quantity as the new quantity of the storedProduct in the DB.
         * We then save the new storedProduct with its new quantity in the DB.
         * Then convert the purchaseProduct to a purchasedProduct(ProductPurchaseResponseDTO) and add it to the list of purchasedProducts.
         * */
        for(ProductPurchaseDTO purchaseProduct : purchaseProducts){

            Product storedProduct = storedProducts.stream()

                        // filters the stream to include only products whose id matches the productId of the current purchaseProduct
                        .filter(product -> product.getId().equals(purchaseProduct.productId()))

                        // returns the first product in the filtered stream, if any.
                        .findFirst()

                        .orElseThrow(() -> new ProductPurchaseException("Product not found with ID: " + purchaseProduct.productId()));

                if (purchaseProduct.quantity() > storedProduct.getAvailableQuantity()) {
                    throw new ProductPurchaseException("Insufficient stock quantity for the product with ID: " + purchaseProduct.productId());
                }

                // if quantity is enough, update the new quantity of the storedProduct in the DB by deducting from quantity of the purchaseProduct purchased.
                Double newAvailableQuantity = storedProduct.getAvailableQuantity() - purchaseProduct.quantity();

                // set the newAvailableQuantity to the storedProduct after purchase
                storedProduct.setAvailableQuantity(newAvailableQuantity);

                // Save the storedProduct with the new quantity after purchase.
                productRepository.save(storedProduct);

                // Mapping the new purchasedProduct with details from the storedProduct
                ProductPurchaseResponseDTO purchasedProduct = productMapper.toProductPurchaseResponse(storedProduct, purchaseProduct.quantity());

                // Add the purchasedProduct to the list of purchasedProducts.
                purchasedProducts.add(purchasedProduct);
            }


        return purchasedProducts;

    }



    public ProductDTO findProductById(Integer id){

        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found with ID:: " + id));

        return productMapper.toProductDTO(product);
    }

    public List<ProductDTO> findAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(productMapper::toProductDTO).toList();
    }
}
