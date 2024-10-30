package com.productcard.card.shop.service.cart;

import com.productcard.card.shop.model.Cart;
import com.productcard.card.shop.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
