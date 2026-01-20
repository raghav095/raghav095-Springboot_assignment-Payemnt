# Spring Boot E-Commerce Backend

A minimal E-Commerce Backend API built with **Spring Boot** and **MongoDB**.
This project allows users to list products, add items to a cart, place orders, and simulate payments using a mock payment service.

## 🚀 Key Features

-   **Product Management**: Create and list products.
-   **Cart System**: Add items, view cart, and clear cart.
-   **Order Processing**: Convert cart items to orders.
-   **Mock Payment Integration**: Simulate payments without external credentials (includes Webhook support).
-   **Embedded Database**: Uses an in-memory MongoDB that works out-of-the-box on all platforms (including Mac ARM64/M1/M2).

## 🛠️ Technology Stack

-   **Java 17**
-   **Spring Boot 3.2.2**
-   **Spring Data MongoDB**
-   **Embedded MongoDB** (for zero-setup local development)
-   **Maven**

## ⚙️ Setup & Running

**Prerequisites**:
-   Java 17 or later installed.
-   Maven (optional, wrapper included).

### 1. Clone the Repository
```bash
git clone https://github.com/raghav095/raghav095-Springboot_assignment-Payemnt.git
cd springboot-payment-assignment
```

### 2. Run the Application
You do **not** need to install MongoDB. The application will start an embedded instance automatically.
```bash
./mvnw spring-boot:run
```
*The app runs on `http://localhost:8080`.*

### 3. Verify Functionality
A Python script is included to test the full create-order-pay flow.
```bash
python3 test_flow.py
```

## 🔌 API Endpoints

### Products
-   `POST /api/products` - Create a product
    ```json
    { "name": "Laptop", "price": 50000.0, "stock": 10 }
    ```
-   `GET /api/products` - List all products

### Cart
-   `POST /api/cart/add` - Add item to cart
-   `GET /api/cart/{userId}` - View user's cart
-   `DELETE /api/cart/{userId}/clear` - Clear cart

### Orders
-   `POST /api/orders` - Create order from cart
-   `GET /api/orders/{orderId}` - Get order details

### Payments (Mock)
-   `POST /api/payments/create` - Initiate payment
-   `POST /api/webhooks/payment` - Webhook callback (called automatically in Mock mode)

## 📝 Configuration

The application is configured in `src/main/resources/application.properties`.
-   **`payment.mode=mock`**: Enables the internal mock payment service.
-   **`de.flapdoodle.mongodb.embedded.version=6.0.8`**: Ensures compatibility with Mac Apple Silicon (ARM64).

## 🧪 Testing

To run the unit tests:
```bash
./mvnw test
```
