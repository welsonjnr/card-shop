package com.productcard.card.shop.service.cart;

import com.productcard.card.shop.model.CartItem;

public interface ICartItemService {
    void addCartItem(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);

    CartItem getCartItem(Long cartId, Long productId);
}
