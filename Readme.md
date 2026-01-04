# E-Commerce Microservices Project - Testing Guide

This guide provides a step-by-step process to test all microservices using Postman. The services are interconnected, so the order of testing is important.

## 🚀 Prerequisites

1.  **Start the Environment**: Ensure all services are running via Docker Compose.
    ```bash
    docker-compose up --build
    ```
2.  **Postman**: Install Postman to send HTTP requests.
3.  **Base URL**: All requests go through the API Gateway at `http://localhost:8080`.

---

## 🧪 Step-by-Step Testing Flow

### 1. User Registration & Authentication (User Service & Auth Service)
*Before doing anything else, you need a user and a token.*

#### A. Register a New User
*   **Endpoint**: `POST http://localhost:8080/api/v1/users/register`
*   **Auth**: None (Public)
*   **Body (JSON)**:
    ```json
    {
      "username": "john_doe",
      "email": "john@example.com",
      "password": "password123"
    }
    ```
*   **Expected Result**: `201 Created`. Returns user details (ID, username, etc.). Note the `id` for later use.

#### B. Login (Get Access Token)
*Since this project uses OAuth2/OIDC with Spring Authorization Server, the login flow is typically browser-based or involves an authorization code flow. However, for testing APIs directly, we often use a client credential flow or a resource owner password flow if configured, or simply simulate the token acquisition if you have a frontend.*

*For this specific setup (Authorization Code Flow with `client` and `secret`), you would typically:*
1.  Open a browser to `http://spring-security:8089/oauth2/authorize?response_type=code&client_id=client&scope=openid profile&redirect_uri=http://127.0.0.1:8080/login/oauth2/code/gateway`.
2.  Login with the user credentials created above.
3.  The gateway handles the token exchange.

*For Postman Testing (Simpler Approach if supported or just for context):*
If you cannot easily simulate the browser flow in Postman without a collection runner script, ensure you have a valid **JWT Access Token**.
*   **Header for all subsequent requests**: `Authorization: Bearer <YOUR_ACCESS_TOKEN>`

---

### 2. Product Management (Product Service & Inventory Service)
*Need products to buy.*

#### A. Create a Product (Admin Role Required)
*   **Endpoint**: `POST http://localhost:8080/api/v1/products`
*   **Auth**: Bearer Token (User with `ROLE_ADMIN`)
*   **Body (JSON)**:
    ```json
    {
      "name": "Smartphone X",
      "description": "Latest model with high-res camera",
      "brand": "TechBrand",
      "category": "Electronics",
      "price": 699.99,
      "imageUrl": "http://example.com/phone.jpg"
    }
    ```
*   **Expected Result**: `201 Created`. Returns product details including `id`. **Note this `productId`.**

#### B. Initialize Inventory (Admin Role Required)
*   **Endpoint**: `POST http://localhost:8080/api/v1/inventory`
*   **Auth**: Bearer Token (User with `ROLE_ADMIN`)
*   **Body (JSON)**:
    ```json
    {
      "productId": 1,
      "quantity": 100
    }
    ```
*   **Expected Result**: `201 Created`.

#### C. Get All Products (Public/User)
*   **Endpoint**: `GET http://localhost:8080/api/v1/products`
*   **Auth**: None or Bearer Token
*   **Expected Result**: `200 OK`. List of products.

#### D. Get Inventory Details (User vs Admin)
*   **Endpoint**: `GET http://localhost:8080/api/v1/inventory/{productId}`
*   **Auth**: Bearer Token
*   **Scenario 1: User Token**
    *   **Expected Result**: `200 OK`.
    *   **Body**: `{"productId": 1, "inStock": true}` (Quantity is hidden).
*   **Scenario 2: Admin Token**
    *   **Expected Result**: `200 OK`.
    *   **Body**: `{"productId": 1, "quantity": 100, "inStock": true}` (Quantity is visible).

---

### 3. Shopping Cart (Cart Service)
*Add the product to your cart.*

#### A. Add Item to Cart
*   **Endpoint**: `POST http://localhost:8080/api/v1/cart/add`
*   **Auth**: Bearer Token (User)
*   **Body (JSON)**:
    ```json
    {
      "productId": 1,
      "quantity": 2
    }
    ```
*   **Expected Result**: `201 Created`. Returns updated cart.

#### B. View Cart
*   **Endpoint**: `GET http://localhost:8080/api/v1/cart`
*   **Auth**: Bearer Token (User)
*   **Expected Result**: `200 OK`. Shows items in cart.

---

### 4. Order Placement (Order Service)
*Checkout the cart to create an order.*

#### A. Checkout (Place Order)
*   **Endpoint**: `POST http://localhost:8080/api/v1/orders/checkout`
*   **Auth**: Bearer Token (User)
*   **Body (JSON)**:
    ```json
    {
      "shippingAddress": "123 Main St, Springfield, USA"
    }
    ```
*   **Expected Result**: `201 Created`. Returns Order ID and status `PENDING` (or similar). **Note the `orderId`.**
*   *Behind the scenes*: This should trigger Inventory Service to reduce stock and clear the Cart.

---

### 5. Payment Processing (Payment Service)
*Pay for the order.*

#### A. Initiate Payment
*   **Endpoint**: `POST http://localhost:8080/api/v1/payments/initiate`
*   **Auth**: Bearer Token (User)
*   **Body (JSON)**:
    ```json
    {
      "orderId": 1,
      "amount": 1399.98,
      "paymentMethod": "CREDIT_CARD"
    }
    ```
*   **Expected Result**: `201 Created`. Returns a transaction ID or payment link.

#### B. Verify Payment (Simulate Success)
*   **Endpoint**: `POST http://localhost:8080/api/v1/payments/verify`
*   **Auth**: Bearer Token (User)
*   **Body (JSON)**:
    ```json
    {
      "orderId": 1,
      "transactionId": "txn_12345abc"
    }
    ```
*   **Expected Result**: `200 OK`. Payment status `SUCCESS`.
*   *Behind the scenes*: This might update the Order status to `PAID`.

---

### 6. Post-Order Verification

#### A. Check Order Status
*   **Endpoint**: `GET http://localhost:8080/api/v1/orders/{orderId}`
*   **Auth**: Bearer Token (User)
*   **Expected Result**: `200 OK`. Order status should be `PAID` (if payment logic updates it) or `CONFIRMED`.

#### B. Check Inventory (Optional)
*   **Endpoint**: `GET http://localhost:8080/api/v1/inventory/{productId}`
*   **Auth**: Bearer Token (User)
*   **Expected Result**: `200 OK`. Quantity should be `98` (100 - 2).

---

### 7. Wishlist (Wishlist Service) - Optional Flow

#### A. Add to Wishlist
*   **Endpoint**: `POST http://localhost:8080/api/v1/wishlist/{productId}`
*   **Auth**: Bearer Token (User)
*   **Expected Result**: `201 Created`.

#### B. Get Wishlist
*   **Endpoint**: `GET http://localhost:8080/api/v1/wishlist`
*   **Auth**: Bearer Token (User)
*   **Expected Result**: `200 OK`.

---

### 8. User Profile & Address (User Service)

#### A. Add Address
*   **Endpoint**: `POST http://localhost:8080/api/v1/users/me/addresses`
*   **Auth**: Bearer Token (User)
*   **Body (JSON)**:
    ```json
    {
      "street": "456 Elm St",
      "city": "Metropolis",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    }
    ```
*   **Expected Result**: `201 Created`.

#### B. Get Profile
*   **Endpoint**: `GET http://localhost:8080/api/v1/users/me`
*   **Auth**: Bearer Token (User)
*   **Expected Result**: `200 OK`.

---

## 📝 Summary of JSON Bodies

**User Registration:**
```json
{ "username": "testuser", "email": "test@test.com", "password": "password" }
```

**Create Product:**
```json
{ "name": "Laptop", "description": "Gaming Laptop", "brand": "Alienware", "category": "Computers", "price": 1500.00, "imageUrl": "url" }
```

**Add to Cart:**
```json
{ "productId": 1, "quantity": 1 }
```

**Checkout:**
```json
{ "shippingAddress": "123 Test St" }
```

**Initiate Payment:**
```json
{ "orderId": 1, "amount": 1500.00, "paymentMethod": "PAYPAL" }
```




//pending work is when the order processing that dat we should send to payment intiative method 
after it successful than it calls to order service and ther we can see the message like confirmed the order from processing


