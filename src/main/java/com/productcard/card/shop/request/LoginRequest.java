package com.productcard.card.shop.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank(message = "Invalid credentials")
    private String email;

    @NotBlank(message = "Invalid credentials")
    private String password;
}
