package com.productcard.card.shop.service.product;

import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.dto.ProductDtoHateoas;
import com.productcard.card.shop.model.Product;
import com.productcard.card.shop.request.AddProductRequest;
import com.productcard.card.shop.request.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest request);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest request, Long productId);
    List<Product> getAllProducts();
    Page<ProductDto> getAllProductsPageable(PageRequest pageRequest);
    PagedModel<EntityModel<ProductDto>> getAllProductsPageableHateoas(PageRequest pageRequest);
    List<Product> getAllProductsByCategory(String category);
    List<Product> getAllProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String brand, String name);
    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    ProductDtoHateoas convertToDtoHateoas(Product product);
}
