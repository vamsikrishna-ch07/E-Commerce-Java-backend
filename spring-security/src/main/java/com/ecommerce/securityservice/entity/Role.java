package com.ecommerce.securityservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

// This is now a DTO, not a JPA Entity. All JPA annotations are removed.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    private Long id;
    private String name;
    private Collection<Permission> permissions;
}
