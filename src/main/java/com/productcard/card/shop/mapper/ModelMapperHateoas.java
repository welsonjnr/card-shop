package com.productcard.card.shop.mapper;

import com.productcard.card.shop.dto.ProductDtoHateoas;
import com.productcard.card.shop.model.Product;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperHateoas {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Product, ProductDtoHateoas>() {
            @Override
            protected void configure() {
                map().setKey(source.getId());
            }
        });

        modelMapper.addMappings(new PropertyMap<ProductDtoHateoas, Product>() {
            @Override
            protected void configure() {
                map().setId(source.getKey());
            }
        });

        return modelMapper;
    }
}