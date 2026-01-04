package com.ecommerce.inventoryservice.controller;

import com.ecommerce.inventoryservice.dto.InventoryResponse;
import com.ecommerce.inventoryservice.dto.StockUpdateRequest;
import com.ecommerce.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAnyAuthority('SCOPE_products.read', 'SCOPE_internal.read')")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable("productId") Long productId, Authentication authentication) {
        // This check remains to differentiate logic within the service if needed (e.g., returning more data for admins)
        boolean isAdminOrInternal = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN") || authority.equals("SCOPE_internal.read"));

        InventoryResponse inventory = inventoryService.getInventoryByProductId(productId, isAdminOrInternal);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> createInventory(@RequestBody StockUpdateRequest request) {
        inventoryService.createInventory(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/stock")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> updateStock(@RequestBody StockUpdateRequest request) {
        inventoryService.updateStock(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/reduce")
    @PreAuthorize("hasAuthority('SCOPE_internal.write')")
    public ResponseEntity<Void> reduceStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.reduceStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{productId}/restore")
    @PreAuthorize("hasAuthority('SCOPE_internal.write')")
    public ResponseEntity<Void> restoreStock(@PathVariable Long productId, @RequestParam Integer quantity) {
        inventoryService.restoreStock(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
