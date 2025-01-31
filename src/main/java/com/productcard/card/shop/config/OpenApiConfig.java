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
                        .title("Title Test")
                        .version("v1")
                        .description("Teste Swagger")
                        .termsOfService("url")
                        .license(new License().name("Apache 2.0")
                                .url("url.que.vai.aparecer.com")));
    }

}
