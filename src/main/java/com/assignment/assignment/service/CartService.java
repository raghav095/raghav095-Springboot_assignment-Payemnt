package com.assignment.assignment.service;

import com.assignment.assignment.dto.CartItemResponse;
import com.assignment.assignment.model.CartItem;
import com.assignment.assignment.model.Product;
import com.assignment.assignment.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
/**
 * Service class for managing Shopping Cart operations.
 * Handles adding items, retrieving cart, and clearing cart.
 */
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    public CartItem addToCart(String userId, String productId, Integer quantity) {
        // Check if item already exists in cart
        List<CartItem> existingItems = cartRepository.findByUserId(userId)
                .stream()
                .filter(item -> item.getProductId().equals(productId))
                .collect(Collectors.toList());

        if (!existingItems.isEmpty()) {
            // Update existing item
            CartItem existingItem = existingItems.get(0);
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            return cartRepository.save(existingItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem(userId, productId, quantity);
            return cartRepository.save(cartItem);
        }
    }

    public List<CartItemResponse> getCartItems(String userId) {
        List<CartItem> cartItems = cartRepository.findByUserId(userId);

        return cartItems.stream().map(cartItem -> {
            Product product = productService.getProductById(cartItem.getProductId());
            CartItemResponse.ProductInfo productInfo = null;

            if (product != null) {
                productInfo = new CartItemResponse.ProductInfo(
                        product.getId(),
                        product.getName(),
                        product.getPrice());
            }

            return new CartItemResponse(
                    cartItem.getId(),
                    cartItem.getProductId(),
                    cartItem.getQuantity(),
                    productInfo);
        }).collect(Collectors.toList());
    }

    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }

    public List<CartItem> getCartItemsRaw(String userId) {
        return cartRepository.findByUserId(userId);
    }
}
