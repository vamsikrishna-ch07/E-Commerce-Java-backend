package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('USER') or hasAnyAuthority('SCOPE_products.read', 'SCOPE_internal.read')")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable("productId") Long productId) {
        InventoryResponse inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createInventory(@RequestBody StockUpdateRequest request) {
        inventoryService.createInventory(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateStock(@RequestBody StockUpdateRequest request) {
        inventoryService.updateStock(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/reduce")
    @PreAuthorize("hasRole('USER') or hasAuthority('SCOPE_internal.write')")
    public ResponseEntity<Void> reduceStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.reduceStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/restore")
    @PreAuthorize("hasRole('USER') or hasAuthority('SCOPE_internal.write')")
    public ResponseEntity<Void> restoreStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.restoreStock(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
