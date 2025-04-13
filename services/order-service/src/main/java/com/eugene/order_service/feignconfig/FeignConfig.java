package com.eugene.order_service.feignconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//Used to access the current HTTP Request
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {

            @Override
            public void apply(RequestTemplate template){
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                    template.header("Authorization", authorizationHeader);
                }
            }

        };
    }

}
