# Spring Boot E-Commerce Backend Assignment

A complete, compliant, and extended implementation of the minimal e-commerce backend API.
This project satisfies all mandatory requirements and includes all **Bonus Challenges** for extra credit.

## 🎯 Assignment Compliance
| Criteria | Status | Details |
| :--- | :--- | :--- |
| **Product APIs** | ✅ Completed | Create, List, and **Search (Bonus)** |
| **Cart APIs** | ✅ Completed | Add, List, and Clear Cart |
| **Order APIs** | ✅ Completed | Place Order, View Order, **History (Bonus)**, **Cancel (Bonus)** |
| **Payment Integration** | ✅ Completed | Mock Payment Service + Webhook simulation |
| **Order Status** | ✅ Completed | Auto-updates to `PAID` via webhook |
| **Code Quality** | ✅ Completed | Standard Controller/Service/Repository architecture |
| **Postman Collection** | ✅ Included | `postman_collection.json` in root directory |

## 🚀 Key Features
-   **Architecture**: Spring Boot 3.2.2 + Embedded MongoDB.
-   **Zero Configuration**: Runs immediately on any OS (Mac ARM64, Windows, Linux) with no db setup.
-   **Mock Payments**: Simulates the full Razorpay flow (Initiate -> Pending -> Webhook -> Success).

## 🛠️ Setup & Running

**Prerequisites**: Java 17+ and Maven.

1.  **Clone & Run**:
    ```bash
    git clone https://github.com/raghav095/raghav095-Springboot_assignment-Payemnt.git
    cd springboot-payment-assignment
    ./mvnw spring-boot:run
    ```
    *Server starts at `http://localhost:8080`*

2.  **Import Postman Collection**:
    -   Open Postman.
    -   Click **Import**.
    -   Select `postman_collection.json` from the project folder.
    -   Run the requests in order.

## 🔌 API Reference & Usage

### 📦 1. Product APIs
-   **Create Product**
    -   `POST /api/products`
    -   Body: `{ "name": "Laptop", "price": 50000.0, "stock": 10 }`
-   **Get All Products**
    -   `GET /api/products`
-   **Search Products (Bonus)**
    -   `GET /api/products/search?q=laptop`
    -   *Finds products containing "laptop" (case-insensitive).*

### 🛒 2. Cart APIs
-   **Add to Cart**
    -   `POST /api/cart/add`
    -   Body: `{ "userId": "u1", "productId": "p1", "quantity": 1 }`
-   **View Cart**
    -   `GET /api/cart/u1`
-   **Clear Cart**
    -   `DELETE /api/cart/u1/clear`

### 📦 3. Order APIs
-   **Place Order**
    -   `POST /api/orders`
    -   Body: `{ "userId": "u1" }`
    -   *Returns Order ID and status `CREATED`.*
-   **Get Order**
    -   `GET /api/orders/{orderId}`
-   **User Order History (Bonus)**
    -   `GET /api/orders/user/{userId}`
    -   *Lists all orders for a specific user.*
-   **Cancel Order (Bonus)**
    -   `POST /api/orders/{orderId}/cancel`
    -   *Cancels order if not paid and restores stock.*

### 💳 4. Payment APIs (Mock)
-   **Initiate Payment**
    -   `POST /api/payments/create`
    -   Body: `{ "orderId": "{orderId}", "amount": 50000 }`
-   **Webhook Logic**:
    -   The system waits **3 seconds** and then automatically triggers a simulated webhook.
    -   Order status updates from `PENDING` -> `PAID`.

## 📂 Project Structure
```
src/main/java/com/assignment/assignment
├── config       # App Configurations
├── controller   # REST Endpoints (API Layer)
├── dto          # Data Transfer Objects
├── model        # MongoDB Documents (User, Product, Order...)
├── repository   # Data Access Layer
├── service      # Business Logic
└── webhook      # Payment Webhook Handler
```

## 🧪 Troubleshooting
-   **Connection Refused**: Ensure the app is running!
-   **Port Busy**: `server.port` defaults to 8080. Check `application.properties` to change it.
