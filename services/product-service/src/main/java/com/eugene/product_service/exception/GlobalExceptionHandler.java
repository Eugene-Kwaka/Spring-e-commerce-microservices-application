package com.eugene.product_service.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
/* Central Exception Handling component that provides application-wide exception handling
 * Removes the need for the try-catch blocks in the Controller class.
 * Handles the CustomerNotFoundException by return a 404 HttpStatus code (NOT FOUND)
 * Handles the MethodArgumentNotValidException from @Valid annotations in the CustomerController and then returns structured responses.
 * Handles ConstraintsViolationException like @NotNull, @NotBlank @Email and returns each field-specific error message.*/
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductPurchaseException.class)
    public ResponseEntity<String> handleProductPurchaseException(ProductPurchaseException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

    }

    // This method handles the MethodArgumentNotValidException which occurs when validation fails for the method arguments.
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ProductErrorResponse> handleMethodArgumentException(MethodArgumentNotValidException e){

        // Creating a new HashMap to store the field names and their corresponding error messages.
        var errors = new HashMap<String, String>();

        /*
         * The getBindingResult().getAllErrors() method gets all validation errors from the BindingResult object.
         * Uses a forEach() loop to process each error.
         */
        e.getBindingResult().getAllErrors()
                .forEach(error -> {
                    // The ((FieldError) error).getField() casts the error to the FieldError type and gets the name of the field that failed validation.
                    var fieldName = ((FieldError) error).getField();
                    // Gets the error message associated with the validation failure.
                    var errorMessage = error.getDefaultMessage();
                    // Adds the fieldName and errorMessage to the errors HashMap
                    errors.put(fieldName, errorMessage);
                });


        // Returns a ResponseEntity with HTTP Status 400 (BAD REQUEST).
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ProductErrorResponse(errors));
    }


    /*
     * This method handles the ConstraintViolationException that is thrown when the fields with the validations such as @NotNull, @NotBlank
     * are violated.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProductErrorResponse> handleConstraintViolation(ConstraintViolationException e) {

        //HashMap to store the fieldNames(Key) and their corresponding violation messages(Values).
        Map<String, String> errors = new HashMap<>();

        // Gets the set of constraint violations from the exception
        // Loops through each violation using ForEach() loop.
        e.getConstraintViolations().forEach(violation ->
                /* For each violation:
                 * violation.getPropertyPath(): Gets the path to the invalid property.
                 * toString(): Converts the path to a string.
                 * violation.getMessage(): Gets the validation error message.
                 * errors.put(): Adds the path and message to the HashMap.
                 * */
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ProductErrorResponse(errors));
    }
}

