package com.productcard.card.shop.service.order;

import com.productcard.card.shop.dto.OrderDto;
import com.productcard.card.shop.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
