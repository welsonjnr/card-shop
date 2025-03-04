package com.productcard.card.shop.mapper;

import com.productcard.card.shop.dto.CartDto;
import com.productcard.card.shop.dto.CartItemDto;
import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.model.Cart;
import com.productcard.card.shop.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductMapper productMapper;

    public CartItemDto convertCartItemToDto(CartItem cartItem){
        ProductDto productDto = productMapper.convertToDto(cartItem.getProduct());
        return new CartItemDto(cartItem.getId(), cartItem.getQuantity(), cartItem.getUnitPrice(), productDto);
    }

    public CartDto convertToCartDto(Cart cart){
        return new CartDto(cart.getId(),
                cart.getItems().stream().map(this::convertCartItemToDto).collect(Collectors.toSet()),
                cart.getTotalAmount());
    }

}
