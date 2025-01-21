package com.productcard.card.shop.service.product;

import com.productcard.card.shop.controller.ProductController;
import com.productcard.card.shop.dto.ImageDto;
import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.dto.ProductDtoHateoas;
import com.productcard.card.shop.exceptions.AlreadyExistsException;
import com.productcard.card.shop.model.Category;
import com.productcard.card.shop.model.Image;
import com.productcard.card.shop.model.Product;
import com.productcard.card.shop.repository.CategoryRepository;
import com.productcard.card.shop.repository.ImageRepository;
import com.productcard.card.shop.repository.ProductRepository;
import com.productcard.card.shop.exceptions.ProductNotFoundException;
import com.productcard.card.shop.request.AddProductRequest;
import com.productcard.card.shop.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    @Value("${api.prefix}")
    private String apiPrefix;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest request) {

        if(productExists(request.getName(), request.getBrand())) {
            throw new AlreadyExistsException(request.getBrand() + " " + request.getName() +
                    " already exists!, you may update this product instead!");
        }

        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                                .orElseGet(() -> {
                                    Category newCategory = new Category(request.getCategory().getName());
                                    return categoryRepository.save(newCategory);
                                });

        request.setCategory(category);

        return productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {
        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository :: save)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);

        return existingProduct;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product Not Found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).orElseThrow(() -> {throw new ProductNotFoundException("Product Not Found!!");});
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getAllProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getAllProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products){
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product){
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);
        return productDto;
    }

    @Override
    public ProductDtoHateoas convertToDtoHateoas(Product product){
        ProductDtoHateoas productDto = modelMapper.map(product, ProductDtoHateoas.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);

        //Hateoas default implementation
        //productDto.add(linkTo(methodOn(ProductController.class).getProductById(product.getId())).withSelfRel());

        //I needed to do it this way because I parameterized the api prefix in application.properties
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

        Link selfLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder
                        .methodOn(ProductController.class)
                        .getProductById(product.getId()))
                .withSelfRel()
                .withType("GET")
                .withHref(String.format("%s%s/products/product/%d/product", baseUrl, apiPrefix, product.getId()));

        Link deleteLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).deleteProduct(product.getId()))
                .withRel("delete")
                .withType("DELETE")
                .withHref(String.format("%s%s/products/product/%d/delete", baseUrl,apiPrefix, product.getId()));

        Link updateLink = WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(ProductController.class).updateProduct(null, product.getId()))
                .withRel("update")
                .withType("PUT")
                .withHref(String.format("%s%s/products/product/%d/update", baseUrl,apiPrefix, product.getId()));

        return productDto.add(selfLink).add(deleteLink).add(updateLink);
    }

}
