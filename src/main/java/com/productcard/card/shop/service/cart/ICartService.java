package com.productcard.card.shop.service.cart;

import com.productcard.card.shop.dto.CartDto;
import com.productcard.card.shop.model.Cart;
import com.productcard.card.shop.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);
    CartDto toDto(Cart cart);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
