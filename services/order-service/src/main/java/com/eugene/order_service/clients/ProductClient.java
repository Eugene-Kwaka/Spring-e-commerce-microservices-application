package com.eugene.order_service.clients;


import com.eugene.order_service.dto.ProductPurchaseDTO;
import com.eugene.order_service.dto.ProductPurchaseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name="product-service", url="${application.config.product-serviceURL}")
public interface ProductClient {

    @PostMapping("/purchase")
    List<ProductPurchaseResponseDTO> purchaseProducts(List<ProductPurchaseDTO> productPurchasesDTO);
}
