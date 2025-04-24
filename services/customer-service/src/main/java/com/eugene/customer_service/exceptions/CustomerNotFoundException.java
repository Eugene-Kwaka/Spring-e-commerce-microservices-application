package com.eugene.customer_service.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Purpose: This annotation is from Lombok and generates equals() and hashCode() methods for the class.
 * callSuper = true: Indicates that the equals() and hashCode() methods should include the fields from the superclass (RuntimeException) in their calculations.
 * Why it's used here: Since CustomerNotFoundException extends RuntimeException, this ensures that the equals() and hashCode() methods also consider the fields of RuntimeException (e.g., the exception message).
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerNotFoundException extends RuntimeException{
     private final String msg;
}
