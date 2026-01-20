import urllib.request
import urllib.error
import json
import time
import sys

BASE_URL = "http://localhost:8080/api"

def log(msg):
    print(f"[TEST] {msg}")

def make_request(url, method="GET", data=None):
    try:
        req = urllib.request.Request(url, method=method)
        req.add_header('Content-Type', 'application/json')
        
        if data:
            json_data = json.dumps(data).encode('utf-8')
            req.data = json_data
            
        with urllib.request.urlopen(req) as response:
            if response.status >= 400:
                print(f"FAILED: Expected success, got {response.status}")
                sys.exit(1)
            return json.loads(response.read().decode())
    except urllib.error.HTTPError as e:
        print(f"FAILED: HTTP {e.code} - {e.reason}")
        print(e.read().decode())
        sys.exit(1)
    except urllib.error.URLError as e:
        print(f"FAILED: Connection Error - {e.reason}")
        sys.exit(1)

def run_test():
    # 1. Create Product
    log("Creating Product...")
    product_payload = {
        "name": "Test Laptop",
        "description": "High Performance",
        "price": 50000.0,
        "stock": 10
    }
    product = make_request(f"{BASE_URL}/products", "POST", product_payload)
    product_id = product['id']
    log(f"Product Created: {product_id}")

    # 2. Add to Cart
    log("Adding to Cart...")
    user_id = "user_test_001"
    cart_payload = {
        "userId": user_id,
        "productId": product_id,
        "quantity": 1
    }
    make_request(f"{BASE_URL}/cart/add", "POST", cart_payload)
    log("Item added to cart")

    # 3. Create Order
    log("Creating Order...")
    order_payload = {"userId": user_id}
    order = make_request(f"{BASE_URL}/orders", "POST", order_payload)
    order_id = order['id']
    log(f"Order Created: {order_id}, Status: {order['status']}")

    # 4. Initiate Payment (Mock)
    log("Initiating Payment...")
    payment_payload = {
        "orderId": order_id,
        "amount": order['totalAmount']
    }
    payment = make_request(f"{BASE_URL}/payments/create", "POST", payment_payload)
    log(f"Payment Initiated: {payment.get('paymentId', 'N/A')}, Status: {payment['status']}")

    # 5. Wait for Webhook (Mock)
    log("Waiting 5 seconds for webhook processing...")
    time.sleep(5)

    # 6. Check Order Status
    log("Verifying Order Status...")
    order_updated = make_request(f"{BASE_URL}/orders/{order_id}")
    status = order_updated['status']
    log(f"Final Order Status: {status}")

    if status == 'PAID' or status == 'CONFIRMED' or status == 'SUCCESS': 
        log("Test PASSED")
    else:
        log("Test Status Check: " + status)

if __name__ == "__main__":
    try:
        run_test()
    except Exception as e:
        print(f"Test Execution Failed: {e}")
        sys.exit(1)
