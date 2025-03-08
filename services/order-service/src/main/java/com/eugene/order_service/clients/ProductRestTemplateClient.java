//package com.eugene.order_service.clients;
//
//import com.eugene.order_service.dto.ProductPurchaseDTO;
//import com.eugene.order_service.dto.ProductPurchaseResponseDTO;
//import com.eugene.order_service.exceptions.BusinessException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//
//@Service
//public class ProductRestTemplateClient {
//
//    @Value("${application.config.product-serviceURL}")
//    private String url;
//
//    // This line initializes a new instance of the RestTemplate class, which is used to make HTTP requests.
//    public RestTemplate restTemplate = new RestTemplate();
//
//    public List<ProductPurchaseResponseDTO> purchaseProducts(List<ProductPurchaseDTO> productPurchaseDTO) {
//
//        // create an instance of HttpHeaders and set the Content-Type header to application/json.
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
//
//        HttpEntity<List<ProductPurchaseDTO>> request = new HttpEntity<>(productPurchaseDTO, headers);
//        ParameterizedTypeReference<List<ProductPurchaseResponseDTO>> responseType = new ParameterizedTypeReference<List<ProductPurchaseResponseDTO>>() {
//        };
//
//        ResponseEntity<List<ProductPurchaseResponseDTO>> responseEntity = restTemplate.exchange(
//                url + "/purchase", HttpMethod.POST, request, responseType);
//
//
//        if (responseEntity.getStatusCode().isError()) {
//            throw new BusinessException("An error occurred while processing the products purchase: " + responseEntity.getStatusCode());
//        }
//
//        return responseEntity.getBody();
//
//    }
//}