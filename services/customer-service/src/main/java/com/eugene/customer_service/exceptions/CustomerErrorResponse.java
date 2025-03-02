package com.eugene.customer_service.exceptions;

import java.util.Map;


/*
* To provide a structured error response containing validation errors using a HashMap
* Stores both the fieldName and the errorMessage for failing fieldName*/
public record CustomerErrorResponse(
        Map<String, String> errors
) {
}
