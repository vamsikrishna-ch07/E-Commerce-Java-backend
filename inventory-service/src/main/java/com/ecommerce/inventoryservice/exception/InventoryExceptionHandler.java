package com.ecommerce.inventoryservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<?> handleNotFound(InventoryNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
