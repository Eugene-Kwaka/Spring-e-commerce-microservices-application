package com.eugene.order_service.exceptions;

// import jakarta.persistence.EntityNotFoundException;
// import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.validation.FieldError;
// import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

// import java.util.HashMap;
// import java.util.Map;

public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

    }

}
