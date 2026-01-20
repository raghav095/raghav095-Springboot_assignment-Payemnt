package com.assignment.assignment.dto;

public class CartItemResponse {
    private String id;
    private String productId;
    private Integer quantity;
    private ProductInfo product;

    public CartItemResponse() {}

    public CartItemResponse(String id, String productId, Integer quantity, ProductInfo product) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.product = product;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ProductInfo getProduct() {
        return product;
    }

    public void setProduct(ProductInfo product) {
        this.product = product;
    }

    public static class ProductInfo {
        private String id;
        private String name;
        private Double price;

        public ProductInfo() {}

        public ProductInfo(String id, String name, Double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        // Getters and setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
