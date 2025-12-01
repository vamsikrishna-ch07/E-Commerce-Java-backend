ORDER SERVICE — How It Connects to Other Services

Order Service is the central coordinator.
It talks to:

1️⃣ Cart Service – Get items user wants to order
2️⃣ Inventory Service – Verify stock + reduce stock
3️⃣ Payment Service – Handle payment
4️⃣ Product Service (optional) – If additional product info needed
5️⃣ Notification Service (optional) – Send order confirmation mail/SMS
🧠 ORDER SERVICE — Step-by-Step Workflow

When user clicks Place Order:

📌 Step 1: Fetch Cart Items (Cart Service)
GET /api/cart/{userId}


Order service fetches:

List of cart items

Total price

📌 Step 2: Check Inventory (Inventory Service)

For each item:

GET /api/inventory/check?productId=101&quantity=2


If ANY item is out of stock → order fails.

📌 Step 3: Deduct Inventory (after confirmation)
POST /api/inventory/reduce


This ensures two users cannot buy the last piece at the same time.

📌 Step 4: Call Payment Service
POST /api/payment/pay


If payment fails → restore stock (optional).

📌 Step 5: Save Order in DB

Order Service stores:

orderId

userId

item list

total price

payment status

order status (CONFIRMED)

timestamp

📌 Step 6: Clear Cart
DELETE /api/cart/clear/{userId}

📌 Step 7: Send Email/SMS (Notification Service)

(optional but real-time companies use it)


===================================================
1️⃣ Cart Service → Inventory Service

Purpose: When adding item to cart, check stock.

CartService must call:

GET http://inventory-service/api/v1/inventory/check?productId=101&quantity=2

2️⃣ Order Service → Cart Service

Purpose: Get cart items for order placement.

Order Service must call:

GET http://cart-service/api/cart/{userId}

3️⃣ Order Service → Inventory Service

Purpose:

Check stock AGAIN before placing order

Deduct stock

Endpoints:

GET  /api/v1/inventory/check
PUT  /api/v1/inventory/reduce/{productId}/{quantity}

4️⃣ Order Service → Payment Service

Purpose: Handle payment generation.

POST /api/v1/payment/pay


(We will add Payment module later.)

5️⃣ Optional: Order → Notification Service

Purpose: Send order confirmation email.
We'll add later if you want.