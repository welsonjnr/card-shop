package com.productcard.card.shop.dto;

import com.productcard.card.shop.mapper.ProductMapper;
import com.productcard.card.shop.model.CartItem;
import com.productcard.card.shop.service.product.ProductService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
