package com.eugene.product_service.exception;

import java.util.Map;

/*
 * To provide a structured error response containing validation errors using a HashMap
 * Stores both the fieldName and the errorMessage for failing fieldName*/
public record ProductErrorResponse(
        Map<String, String> errors
) {
}