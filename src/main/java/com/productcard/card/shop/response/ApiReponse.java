package com.productcard.card.shop.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiReponse {
    private String message;
    private Object data;
}
