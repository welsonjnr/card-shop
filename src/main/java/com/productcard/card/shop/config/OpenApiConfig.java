package com.productcard.card.shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Api of Product Cart")
                        .version("v1")
                        .description("Implementation of a product cart api, using JWT, Swagger and other spring boot technologies ")
                        .termsOfService("https://github.com/welsonjnr/card-shop")
                        .license(new License().name("Card-Shop 1.0")
                                .url("https://github.com/welsonjnr/card-shop")));
    }

}
