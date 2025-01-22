package com.productcard.card.shop.model;

import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.request.AddProductRequest;
import com.productcard.card.shop.request.ProductUpdateRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MockProduct {

    public static Product createProduct(){
        Category category = new Category("Electronics");
        return new Product(
                "Product Test",
                "Brand Test",
                new BigDecimal("999.99"),
                100,
                "Latest model For Test",
                category
        );
    }

    public static Product createProduct(Long id){
        Product product = new Product(
                "Product Test",
                "Brand Test",
                new BigDecimal("999.99"),
                100,
                "Latest model For Test",
                new Category("Electronics")
        );

        product.setId(id);

        return product;
    }

    public static List<Product> createListProduct(int listSize){

        return IntStream.rangeClosed(1, listSize)
                .mapToObj(i -> new Product(
                        "Product " + i,
                        "Brand",
                        new BigDecimal("50.00"),
                        10 * i,
                        "Description of Product DTO: " + i,
                        MockCategory.createCategory()
                ))
                .collect(Collectors.toList());
    }

    public static ProductDto createProductDto(Long id){
        ProductDto productDto = new ProductDto();
        productDto.setId(id);
        productDto.setName("Product Dto");
        productDto.setBrand("Brand Dto");
        productDto.setPrice(new BigDecimal("100.00"));
        productDto.setInventory(30);
        productDto.setDescription("Product Dto for JUnit");
        productDto.setCategory(MockCategory.createCategory());
        productDto.setImages(new ArrayList<>());

        return productDto;
    }

    public static Product createProductUpdated(){
        Product product = new Product();
        product.setId(1L);
        product.setName("Product Update Test");
        product.setBrand("Brand Update Test");
        product.setPrice(new BigDecimal("200.00"));
        product.setInventory(50);
        product.setDescription("New Model Latest model For Test");
        product.setCategory(new Category("Smart"));
        return product;
    }

    public static AddProductRequest createAddProductRequest() {
        AddProductRequest request = new AddProductRequest();
        request.setName("Product Test");
        request.setBrand("Brand Test");
        request.setPrice(new BigDecimal("999.99"));
        request.setInventory(100);
        request.setDescription("Latest model For Test");
        request.setCategory(new Category("Electronics"));
        return request;
    }

    public static ProductUpdateRequest createProductUpdateRequest() {
        ProductUpdateRequest request = new ProductUpdateRequest();
        request.setId(1L);
        request.setName("Product Update Test");
        request.setBrand("Brand Update Test");
        request.setPrice(new BigDecimal("200.00"));
        request.setInventory(50);
        request.setDescription("New Model Latest model For Test");
        request.setCategory(new Category("Smart"));
        return request;
    }
}
