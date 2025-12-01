
## Core Microservices (Business Logic)

- **User Service** – manages users (registration, profile, roles)
- **Product Service** – manages products (catalog, inventory, categories)
- **Order Service** – manages customer orders
- **Payment Service** – handles payments
- **Cart Service** – manages shopping carts

---

### 🔹 Infrastructure & Support Services

- **Auth (JWT Security) Service** – responsible for authentication & issuing JWT tokens
- **API Gateway** – single entry point for all clients (routes requests, validates JWT)
- **Service Registry (Eureka/Consul)** – service discovery
- **Config Server** – centralized configuration management




=======
left to create:# Microservices Overview
🆕 Product Service	Handles products (CRUD: add, update, delete, view)
🆕 Inventory Service	Manages stock for each product
🆕 Cart Service	Add/remove items for a user's cart
🆕 Order Service  Places an order from cart


✅ Next Step (Step-by-Step)
🔹 Step 1: Create Product Service
Responsibilities:

Add product

Get product list

Get product by ID

Update / delete product

Tables: product(id, name, description, price)

Register with Eureka and get configs from Config Server.

🔹 Step 2: Create Inventory Service
Responsibilities:

Track stock for products

Update stock after order

Tables: inventory(id, product_id, available_quantity)

Communicates with Product Service (via RestTemplate or Feign)

🔹 Step 3: Create Cart Service
Responsibilities:

Add product to user's cart

Remove product

View cart

Tables: cart(id, user_id), cart_items(id, cart_id, product_id, quantity)

🔹 Step 4: Create Order Service
Responsibilities:

Place order

Reduce inventory

Store order data

Tables: order(id, user_id, order_date, total_amount)
order_items(order_id, product_id, quantity, price)


