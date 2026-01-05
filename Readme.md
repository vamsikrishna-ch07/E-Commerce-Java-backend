# E-Commerce Microservices Backend

A scalable **E-Commerce backend system** built using **Spring Boot Microservices**, **Spring Security OAuth2**, **API Gateway**, and **Docker**.  
This project demonstrates **real-world service separation, security, and order-payment lifecycle management**.

---

## 🏗️ Architecture Overview

- **API Gateway (Reactive)** – Central entry point, routing, and security
- **Auth Service** – OAuth2 / OIDC authentication & JWT token issuance
- **User Service** – User management & profile handling
- **Product Service** – Product catalog management
- **Inventory Service** – Stock management & availability checks
- **Cart Service** – User cart operations
- **Order Service** – Order lifecycle management
- **Payment Service** – Payment initiation & verification
- **Shared Module** – Common security, DTOs, Feign interceptors, utilities

---

## 🔐 Security Design

- Centralized authentication using **Spring Authorization Server**
- JWT-based authorization across all services
- Security configuration shared via **common module**
- Role-based access control (`USER`, `ADMIN`)
- API Gateway handles token validation (Reactive Security)

---

## 🔄 Order & Payment Flow

1. User adds products to cart
2. Checkout creates an **Order** with `PROCESSING` status
3. Inventory stock is reduced during checkout
4. Payment is initiated independently
5. Payment verification updates order status to `PAID / COMPLETED`

> Order and Payment services are **fully decoupled**, following real-world e-commerce design principles.

---

## 📦 Order Status Lifecycle

PROCESSING → PAID / COMPLETED → (SHIPPED - Future Scope)



---

## 🧱 Inventory Visibility

- **Users**: Can only view stock availability (`inStock`)
- **Admins**: Can view exact stock quantities

---

## 🚀 Technology Stack

- Java 17
- Spring Boot
- Spring Cloud Gateway (Reactive)
- Spring Security OAuth2 / OIDC
- Spring Authorization Server
- Feign Client
- Docker & Docker Compose
- MySQL / PostgreSQL (configurable)

---

## 🧪 Project Purpose

- Practice enterprise-level microservice architecture
- Demonstrate correct service boundaries
- Implement secure, scalable backend design
- Resume-focused, interview-ready project

---

## 📌 Notes

- Shipping and delivery flows are intentionally out of scope
- Payment gateway integration is simulated
- Designed for learning and demonstration, not production deployment

---
===============================

