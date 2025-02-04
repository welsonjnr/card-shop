package com.productcard.card.shop.dto;

import com.productcard.card.shop.controller.ProductController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductDtoAssembler implements RepresentationModelAssembler<ProductDto, EntityModel<ProductDto>> {

    @Override
    public EntityModel<ProductDto> toModel(ProductDto productDto) {
        return EntityModel.of(productDto,
                linkTo(methodOn(ProductController.class).getProductById(productDto.getId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).getAllProductsPageableHateoas(0, 12, "name", "asc")).withRel("products"));
    }
}
