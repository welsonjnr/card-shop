package com.productcard.card.shop.service.product;

import com.productcard.card.shop.exceptions.AlreadyExistsException;
import com.productcard.card.shop.exceptions.ProductNotFoundException;
import com.productcard.card.shop.model.Category;
import com.productcard.card.shop.model.MockProduct;
import com.productcard.card.shop.model.Product;
import com.productcard.card.shop.repository.CategoryRepository;
import com.productcard.card.shop.repository.ImageRepository;
import com.productcard.card.shop.repository.ProductRepository;
import com.productcard.card.shop.request.AddProductRequest;
import com.productcard.card.shop.request.ProductUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductService productService;

    @Value("${api.prefix}")
    private String apiPrefix;

    @BeforeEach
    void setUp() {
        apiPrefix = "/api/v1";
    }

    @Test
    void shouldReturnAProductById() {

        Product product = MockProduct.createProduct();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var result = productService.getProductById(1L);

        assertNotNull(result, "The product should not be null");
        assertEquals(result.getId(), 1L);
        assertEquals(result.getName(), "Product Test");
        assertEquals(result.getBrand(), "Brand Test");
        assertEquals(result.getPrice(), new BigDecimal("999.99"));
        assertEquals(result.getInventory(), 100);
        assertEquals(result.getDescription(), "Latest model For Test");
        assertEquals(result.getCategory().getName(), "Electronics");
    }

    @Test
    void shouldNotReturnAProductByIdAndThrowAProductNotFoundException() {

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));

        assertEquals("Product Not Found!", exception.getMessage());
        assertTrue(exception instanceof ProductNotFoundException);
    }

    @Test
    void shouldAddProductWithNewCategory() {

        AddProductRequest request = MockProduct.createAddProductRequest();

        when(productRepository.existsByNameAndBrand("Product Test", "Brand Test")).thenReturn(false);
        when(categoryRepository.findByName("Electronics")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(request.getCategory());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.addProduct(request);

        assertNotNull(result);
        assertEquals("Product Test", result.getName());
        assertEquals("Brand Test", result.getBrand());
        assertEquals(request.getCategory(), result.getCategory());
        verify(categoryRepository).save(any(Category.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldAddProductWithExistingCategory() {

        AddProductRequest request = MockProduct.createAddProductRequest();

        when(productRepository.existsByNameAndBrand("Product Test", "Brand Test")).thenReturn(false);
        when(categoryRepository.findByName("Electronics")).thenReturn(request.getCategory());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.addProduct(request);

        assertNotNull(result);
        assertEquals("Product Test", result.getName());
        assertEquals("Brand Test", result.getBrand());
        assertEquals(request.getCategory(), result.getCategory());
        verify(categoryRepository, never()).save(any(Category.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldThrowAlreadyExistsExceptionIfProductExists() {

        AddProductRequest request = MockProduct.createAddProductRequest();

        when(productRepository.existsByNameAndBrand("Product Test", "Brand Test")).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> {
            productService.addProduct(request);
        });

        assertEquals("Brand Test Product Test already exists!, you may update this product instead!", exception.getMessage());
        assertTrue(exception instanceof AlreadyExistsException);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldUpdateAProductSuccessfully() {

        Product product = MockProduct.createProductUpdated();
        ProductUpdateRequest request = MockProduct.createProductUpdateRequest();

        when(productRepository.findById(request.getId())).thenReturn(Optional.of(product));
        when(categoryRepository.findByName(request.getCategory().getName())).thenReturn(request.getCategory());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = productService.updateProduct(request, request.getId());

        assertNotNull(updatedProduct);
        assertEquals("Product Update Test", updatedProduct.getName());
        assertEquals("Brand Update Test", updatedProduct.getBrand());
        assertEquals(new BigDecimal("200.00"), updatedProduct.getPrice());
        assertEquals(50, updatedProduct.getInventory());
        assertEquals("New Model Latest model For Test", updatedProduct.getDescription());
        assertEquals("Smart", updatedProduct.getCategory().getName());

        verify(productRepository).findById(request.getId());
        verify(categoryRepository).findByName("Smart");
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExist() {

        ProductUpdateRequest request = MockProduct.createProductUpdateRequest();

        when(productRepository.findById(request.getId())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(request, request.getId()));

        verify(productRepository).findById(request.getId());
        verifyNoInteractions(categoryRepository);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteAProductSuccessfully() {

        Product product = MockProduct.createProduct(1L);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProductById(product.getId());

        verify(productRepository).findById(product.getId());
        verify(productRepository).deleteById(product.getId());
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenProductDoesNotExistWhenDelete() {

        Long productId = 99L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(productId));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).deleteById(anyLong());
    }

}
