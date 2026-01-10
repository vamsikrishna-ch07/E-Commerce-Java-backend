E-Commerce Microservices Project - Testing Guide (Updated)
This guide provides a step-by-step process to test the complete, decoupled workflow of the E-Commerce microservices project using Postman.
🚀 Prerequisites
1.
Start the Environment: Ensure all services are running via Docker Compose.
Shell Script
docker-compose up --build
2.
Postman: You will need Postman to send HTTP requests.
3.
Base URL: All requests go through the API Gateway at http://localhost:8080.
4.
Authentication: Most endpoints are protected. You will need a valid JWT Access Token from the authentication flow. For every protected request, add the following header:
◦
Key: Authorization
◦
Value: Bearer <YOUR_ACCESS_TOKEN>
🧪 Step-by-Step Testing Flow
1. User Registration & Authentication
   First, you need to register a user and get an access token.
   A. Register a New User
   •
   Endpoint: POST http://localhost:8080/user-service/api/v1/users/register
   •
   Auth: None (Public)
   •
   Body (JSON):
   JSON
   {
   "username": "testuser",
   "email": "test@example.com",
   "password": "password123"
   }
   •
   Expected Result: 201 Created.
   B. Get Access Token
   This project uses an OAuth2 Authorization Code Flow. The easiest way to get a token for testing is through the browser:
1.
Navigate to this URL in your browser: http://localhost:8080/oauth2/authorization/gateway
2.
You will be redirected to the Spring Authorization Server login page. Log in with the user you just registered (testuser / password123).
3.
After a successful login, you will be redirected back. Your browser's developer tools (Network tab) will show a request to /oauth2/token. The response to this request contains your access_token.
4.
Copy this token to use in the Authorization header for the next steps.
2. Product & Inventory Setup (Admin Role Required)
   To test this, you need a user with ROLE_ADMIN. You may need to grant this role manually in the users table of the user_db.
   A. Create a Product
   •
   Endpoint: POST http://localhost:8080/product-service/api/v1/products
   •
   Auth: Bearer Token (Admin)
   •
   Body (JSON):
   JSON
   {
   "name": "Gaming Laptop",
   "description": "High-performance gaming laptop",
   "brand": "TechCorp",
   "category": "Electronics",
   "price": 1499.99,
   "imageUrl": "http://example.com/laptop.jpg"
   }
   •
   Expected Result: 201 Created. Returns product details. Note the id of the product.
   B. Initialize Inventory for the Product
   •
   Endpoint: POST http://localhost:8080/inventory-service/api/v1/inventory
   •
   Auth: Bearer Token (Admin)
   •
   Body (JSON) (Use the productId from the step above):
   JSON
   {
   "productId": 1,
   "quantity": 50
   }
   •
   Expected Result: 201 Created.
3. Add Product to Cart (User Role)
   Switch back to using the regular User's Bearer Token.
   A. Add Item to Cart
   •
   Endpoint: POST http://localhost:8080/cart-service/api/v1/cart/add
   •
   Auth: Bearer Token (User)
   •
   Body (JSON) (Use the productId from step 2):
   JSON
   {
   "productId": 1,
   "quantity": 1
   }
   •
   Expected Result: 201 Created. Returns the updated cart contents.
4. Decoupled Order & Payment Flow
   This is the core workflow. It is now three distinct steps.
   A. Place the Order (Status: PENDING)
   This creates the order but does not process any payment.
   •
   Endpoint: POST http://localhost:8080/order-service/api/v1/orders/checkout
   •
   Auth: Bearer Token (User)
   •
   Body (JSON):
   JSON
   {
   "shippingAddress": "123 Order Street, Testville, 12345"
   }
   •
   Verification:
   i.
   Expected Result: 201 Created. The response will contain the new order details.
   ii.
   Note the id (this is the orderId) and the totalPrice from the response.
   iii.
   Check the orders table in order_db. The new order should have a status of PENDING.
   B. Initiate Manual Payment
   This simulates the user starting the payment process.
   •
   Endpoint: POST http://localhost:8080/payment-service/api/v1/payments/initiate
   •
   Auth: Bearer Token (User)
   •
   Body (JSON) (Use the orderId and totalPrice from the previous step):
   JSON
   {
   "orderId": 1,
   "amount": 1499.99,
   "paymentMethod": "CREDIT_CARD"
   }
   •
   Verification:
   i.
   Expected Result: 201 Created. The response contains the payment details.
   ii.
   Note the transactionId from the response.
   iii.
   Check the payments table in payment_db. The new payment record should have a status of PENDING.
   C. Verify Manual Payment (Status: COMPLETED)
   This simulates a successful payment confirmation from a payment gateway.
   •
   Endpoint: POST http://localhost:8080/payment-service/api/v1/payments/verify
   •
   Auth: Bearer Token (User)
   •
   Body (JSON) (Use the transactionId from the previous step):
   JSON
   {
   "transactionId": "ff201e27-56e8-46f3-8a38-a2647bd673a3"
   }
   •
   Verification:
   i.
   Expected Result: 200 OK. The response shows the payment status as COMPLETED.
   ii.
   Check payment_db: The payments table row for this transaction should now have its status updated to COMPLETED.
   iii.
   Check order_db: The orders table row for your order should now have its status automatically updated from PENDING to PROCESSING. This confirms the internal communication worked.
5. Other Service Endpoints (Examples)
   Get User Profile
   •
   Endpoint: GET http://localhost:8080/user-service/api/v1/users/me
   •
   Auth: Bearer Token (User)
   •
   Expected Result: 200 OK with the user's profile details.
   Get Wishlist
   •
   Endpoint: GET http://localhost:8080/wishlist-service/api/v1/wishlist
   •
   Auth: Bearer Token (User)
   •
   Expected Result: 200 OK with the user's wishlist







======
http://localhost:8089/oauth2/authorize?response_type=code&client_id=client&scope=openid%20profile&redirect_uri=http://127.0.0.1:8080/login/oauth2/code/gateway