package com.eugene.order_service.feignconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//Used to access the current HTTP Request
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
// import com.eugene.order_service.clients.CustomerClient;
// import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Configuration

/**
 * This class customizes the behavior of Feign clients. 
 * Its primary purpose is to propagate the Authorization header from the incoming HTTP request in the order-service to outgoing requests made by Feign clients. 
 * This ensures that the downstream microservices (e.g., customer-service, product-service, payment-service) receive the same authentication/authorization context as the original request.
 */
public class FeignConfig {

    @Bean
    /**
    * The RequestInterceptor class is used to modify or add headers to outgoing Feign client requests.

    * The requestInterceptor() method modifies of adds headers to outgoing Feign client requests.

    * The requestInterceptor method defines a custom interceptor that:

        - Retrieves the current HTTP request using RequestContextHolder.

        - Extracts the Authorization header from the incoming request.
        
        - Adds the Authorization header to the outgoing Feign request if it exists.

    * When a client sends a request to the order-service with an Authorization header (e.g., a JWT token or OAuth2 token), this configuration ensures that the same token is forwarded to downstream services.

    * By propagating the Authorization header, downstream services can validate the token and enforce security policies without requiring the order-service to re-authenticate the user.
    
     */ 

    /**
     * How It Works
        - A client sends a request to the order-service with an Authorization header.
        - The FeignConfig class intercepts outgoing Feign requests.
        - The RequestInterceptor retrieves the Authorization header from the incoming request.
        - If the header exists, it is added to the outgoing Feign request.
        - The downstream service (e.g., customer-service) receives the request with the propagated Authorization header and can validate it.
     */
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {

            @Override
            // This method is invoked for every outgoing Feign client request.
            // The RequestTemplate Represents the Feign request being constructed. It allows you to modify the request, such as adding headers, query parameters, or modifying the body.
            public void apply(RequestTemplate template){

                // The RequestContextHolder.getRequestAttributes() retrieves the current request atributes for the thread.
                // This line retrieves the current HTTP request attributes so that the incoming request can be accessed.
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                // The attributes.getRequest() retrieves the HttpServletRequest object from the ServletRequestAttributes.
                // The line gets the actual HTTP request object containing details about the incoming request, such as, headers, parameters and the body
                HttpServletRequest request = attributes.getRequest();

                // Extracts the Authorization header from the incoming request so it can be forwarded to the outgoing Feign request.
                // The getHeader retrieves the value of Authorization header from the incoming HTTP request.customer-service
                // The header typically contains the Oauth2 token used for authentication and authorization.
                String authorizationHeader = request.getHeader("Authorization");

                // If the authorizationHeader exists as is not empty, then add the add it to the outgoing Feign request
                // The template.header() method allows you to set or modify headers for the request. 
                if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                    template.header("Authorization", authorizationHeader);
                }
            }

        };
    }

}
