# E-Commerce Backend API - Spring Boot Assignment

This is a complete e-commerce backend API built with Spring Boot and MongoDB. It features a fully functional mock payment integration and uses an embedded database for zero-configuration execution.

## Features
✅ **Product Management**: Create and list products.
✅ **Shopping Cart**: Add items, view cart, and clear cart.
✅ **Order Management**: Create orders from cart, view order details.
✅ **Payment Integration**: Mock Service with automatic Webhook simulation (simulating Razorpay).
✅ **Embedded Database**: Runs entirely in-memory using `de.flapdoodle.embed.mongo`, requiring no local MongoDB installation.
✅ **Universal Compatibility**: Pre-configured for Mac (ARM64/Apple Silicon), Windows, and Linux.

## Tech Stack
-   **Backend**: Spring Boot 3.2.2
-   **Database**: Embedded MongoDB (In-Memory)
-   **Payment Gateway**: Mock Service (Simulates Razorpay)
-   **Language**: Java 17
-   **Build Tool**: Maven

## Prerequisites
-   Java 17 or higher
-   Maven (Optional, wrapper provided)
-   **No local MongoDB installation needed** (The app manages its own database instance).

## Setup Instructions

1.  **Clone the repository**
    ```bash
    git clone https://github.com/raghav095/raghav095-Springboot_assignment-Payemnt.git
    cd springboot-payment-assignment
    ```

2.  **Install dependencies**
    ```bash
    ./mvnw clean install
    ```

3.  **Run the application**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

## API Endpoints & Curl Commands

### 1. Product APIs

**Create Product**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop",
    "price": 50000.0,
    "stock": 10
  }'
```

**Get All Products**
```bash
curl -X GET http://localhost:8080/api/products
```

### 2. Cart APIs

**Add Item to Cart**
```bash
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "productId": "REPLACE_WITH_PRODUCT_ID",
    "quantity": 1
  }'
```

**Get User's Cart**
```bash
curl -X GET "http://localhost:8080/api/cart/user123"
```

**Clear User's Cart**
```bash
curl -X DELETE "http://localhost:8080/api/cart/user123/clear"
```

### 3. Order APIs

**Create Order from Cart**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123"
  }'
```

**Get Order Details**
```bash
curl -X GET "http://localhost:8080/api/orders/REPLACE_WITH_ORDER_ID"
```

### 4. Payment APIs

**Create Payment (Mock)**
```bash
curl -X POST http://localhost:8080/api/payments/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "REPLACE_WITH_ORDER_ID",
    "amount": 50000.0
  }'
```
*Note: This will return a status of `PENDING`.*

**Webhook (Automatic)**
Since the application is in `mock` mode, the system **automatically** triggers the webhook logic 3 seconds after payment creation. You do not need to manually call the webhook endpoint unless you are debugging.

**Manual Webhook Call (Optional Debugging)**
```bash
curl -X POST http://localhost:8080/api/webhooks/payment \
  -H "Content-Type: application/json" \
  -d '{
    "event": "payment.captured",
    "payload": {
      "payment": {
        "id": "pay_mock_123",
        "order_id": "REPLACE_WITH_ORDER_ID_FROM_PAYMENT",
        "status": "captured"
      }
    }
  }'
```

## Complete Testing Flow
We have provided a Python script that automates the entire end-to-end testing flow.

**Run the verification script:**
```bash
python3 verify_all_endpoints.py
```

**Steps performed by the script:**
1.  Creates a new Product.
2.  Adds the product to the user's Cart.
3.  Creates an Order from the Cart.
4.  Initiates a Payment.
5.  Waits for the Mock Webhook to process.
6.  Verifies the Order status is updated to `PAID`.

## Database Schema
The application uses the following MongoDB collections (created automatically):
-   `users`: User information
-   `products`: Product catalog
-   `cart_items`: Shopping cart contents
-   `orders`: Order records
-   `order_items`: Line items within orders
-   `payments`: Payment transaction logs

## Razorpay / Mock Integration
The application supports two modes, configured in `application.properties`:

1.  **Mock Mode (Default)**: `payment.mode=mock`
    -   Simulates payment success without external API calls.
    -   Automatically triggers webhooks internally.
    -   Ideal for development and testing.

2.  **Razorpay Mode**: `payment.mode=razorpay`
    -   Requires valid `razorpay.key-id` and `razorpay.key-secret`.
    -   Interacts with real Razorpay APIs.

## Troubleshooting

-   **"Connection refused"**: Make sure the application is running (`./mvnw spring-boot:run`) before running curl commands or the test script.
-   **Port Conflicts**: If port 8080 is occupied, you can change it in specific commands:
    `./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`
-   **Architecture Issues**: If the embedded database fails on Mac (M1/M2), ensure `de.flapdoodle.mongodb.embedded.version=6.0.8` is set in `application.properties` (this is already configured).
