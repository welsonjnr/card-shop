package com.productcard.card.shop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.productcard.card.shop.model.Category;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonPropertyOrder({"id", "name", "brand", "price", "inventory", "description", "category", "images"})
public class ProductDtoHateoas extends RepresentationModel<ProductDtoHateoas> {

    @JsonProperty("id")
    private Long key;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String description;
    private Category category;
    private List<ImageDto> images;
}
