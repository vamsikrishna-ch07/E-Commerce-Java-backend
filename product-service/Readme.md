Test APIs (Postman)
    Method	URL	Body
    POST	/api/v1/products	raw JSON
    GET	/api/v1/products	—
    GET	/api/v1/products/{id}	—
    PUT	/api/v1/products/{id}	raw JSON
    DELETE	/api/v1/products/{id}	—


1️⃣ Scalability

Inventory updates frequently.
Products rarely change.

So keeping them separate allows the Inventory Service to scale independently.

2️⃣ Performance

Inventory is hit thousands of times per second (for stock checking).

If product and inventory were in the same service, high traffic on inventory would overload product.