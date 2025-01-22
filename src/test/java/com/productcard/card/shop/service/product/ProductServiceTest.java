package com.productcard.card.shop.service.product;

import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.exceptions.AlreadyExistsException;
import com.productcard.card.shop.exceptions.ProductNotFoundException;
import com.productcard.card.shop.model.*;
import com.productcard.card.shop.repository.CategoryRepository;
import com.productcard.card.shop.repository.ImageRepository;
import com.productcard.card.shop.repository.ProductRepository;
import com.productcard.card.shop.request.AddProductRequest;
import com.productcard.card.shop.request.ProductUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    @BeforeEach
    void setUp() {}

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

        var result = productService.addProduct(request);

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

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class, () -> productService.addProduct(request));

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

    @Test
    void shouldReturnAllProducts() {

        List<Product> mockProducts = MockProduct.createListProduct(10);

        when(productRepository.findAll()).thenReturn(mockProducts);

        var result = productService.getAllProducts();

        assertNotNull(result, "The result should not be null");
        assertEquals(10, result.size(), "The size of the result should match the number of mock products");
        assertEquals("Product 1", result.get(0).getName(), "The name of the first product should match");
        assertEquals("Brand", result.get(1).getBrand(), "The brand of the second product should match");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnAllProductsByCategory() {

        List<Product> mockProducts = MockProduct.createListProduct(5);

        when(productRepository.findByCategoryName("Electronics")).thenReturn(mockProducts);

        var result = productService.getAllProductsByCategory("Electronics");

        assertNotNull(result, "The result should not be null");
        assertEquals(5, result.size(), "The size of the result should match the number of mock products");
        assertEquals("Electronics", result.get(0).getCategory().getName(), "The Category of the product " + 0 + " should match");
        assertEquals("Electronics", result.get(2).getCategory().getName(), "The Category of the product " + 2 + " should match");
        assertEquals("Electronics", result.get(4).getCategory().getName(), "The Category of the product " + 4 + " should match");
        verify(productRepository, times(1)).findByCategoryName("Electronics");
    }

    @Test
    void shouldReturnNoneProductByCategory() {

        List<Product> mockProducts = new ArrayList<>();

        when(productRepository.findByCategoryName("Electronics")).thenReturn(mockProducts);

        var result = productService.getAllProductsByCategory("Electronics");

        assertNotNull(result, "The result should not be null");
        assertEquals(0, result.size(), "The size of the result should match the number of mock products");
        verify(productRepository, times(1)).findByCategoryName("Electronics");
    }

    @Test
    void shouldReturnAllProductsByBrand() {

        List<Product> mockProducts = MockProduct.createListProduct(5);

        when(productRepository.findByBrand("Brand")).thenReturn(mockProducts);

        var result = productService.getAllProductsByBrand("Brand");

        assertNotNull(result, "The result should not be null");
        assertEquals(5, result.size(), "The size of the result should match the number of mock products");
        assertEquals("Brand", result.get(0).getBrand(), "The brand of the "+0+" product should match");
        assertEquals("Brand", result.get(2).getBrand(), "The brand of the "+2+" product should match");
        assertEquals("Brand", result.get(4).getBrand(), "The brand of the "+4+" product should match");
        verify(productRepository, times(1)).findByBrand("Brand");
    }

    @Test
    void shouldReturnNoneProductByBrand() {

        List<Product> mockProducts = new ArrayList<>();

        when(productRepository.findByBrand("Brand")).thenReturn(mockProducts);

        var result = productService.getAllProductsByBrand("Brand");

        assertNotNull(result, "The result should not be null");
        assertEquals(0, result.size(), "The size of the result should match the number of mock products");
        verify(productRepository, times(1)).findByBrand("Brand");
    }

    @Test
    void shouldReturnAllProductsByCategoryAndBrand() {

        List<Product> mockProducts = MockProduct.createListProduct(5);

        when(productRepository.findByCategoryNameAndBrand("Electronics","Brand")).thenReturn(mockProducts);

        var result = productService.getProductsByCategoryAndBrand("Electronics","Brand");

        assertNotNull(result, "The result should not be null");
        assertEquals(5, result.size(), "The size of the result should match the number of mock products");
        assertEquals("Brand", result.get(0).getBrand(), "The brand of the "+0+" product should match");
        assertEquals("Electronics", result.get(0).getCategory().getName(), "The Category of the product " + 0 + " should match");
        assertEquals("Brand", result.get(2).getBrand(), "The brand of the "+2+" product should match");
        assertEquals("Electronics", result.get(2).getCategory().getName(), "The Category of the product " + 2 + " should match");
        assertEquals("Brand", result.get(4).getBrand(), "The brand of the "+4+" product should match");
        assertEquals("Electronics", result.get(4).getCategory().getName(), "The Category of the product " + 4 + " should match");
        verify(productRepository, times(1)).findByCategoryNameAndBrand("Electronics","Brand");
    }

    @Test
    void shouldReturnNoneProductByCategoryAndBrand() {

        List<Product> mockProducts = new ArrayList<>();

        when(productRepository.findByCategoryNameAndBrand("Electronics","Brand")).thenReturn(mockProducts);

        var result = productService.getProductsByCategoryAndBrand("Electronics","Brand");

        assertNotNull(result, "The result should not be null");
        assertEquals(0, result.size(), "The size of the result should match the number of mock products");
        verify(productRepository, times(1)).findByCategoryNameAndBrand("Electronics","Brand");
    }

    @Test
    void shouldReturnAllProductsByName() {

        List<Product> mockProducts = MockProduct.createListProduct(1);

        when(productRepository.findByName("Product 1")).thenReturn(mockProducts);

        var result = productService.getProductsByName("Product 1");

        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.size(), "The size of the result should match the number of mock products");
        verify(productRepository, times(1)).findByName("Product 1");
    }

    @Test
    void shouldReturnNoneProductByName() {

        List<Product> mockProducts = new ArrayList<>();

        when(productRepository.findByName("Product 1")).thenReturn(mockProducts);

        var result = productService.getProductsByName("Product 1");

        assertNotNull(result, "The result should not be null");
        assertEquals(0, result.size(), "The size of the result should match the number of mock products");
        verify(productRepository, times(1)).findByName("Product 1");
    }

    @Test
    void shouldReturnAllProductsByBrandAndName() {

        List<Product> mockProducts = MockProduct.createListProduct(1);

        when(productRepository.findByBrandAndName("Brand", "Product 1")).thenReturn(mockProducts);

        var result = productService.getProductsByBrandAndName("Brand", "Product 1");

        assertNotNull(result, "The result should not be null");
        assertEquals(1, result.size(), "The size of the result should match the number of mock products");
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Brand", result.get(0).getBrand());
        verify(productRepository, times(1)).findByBrandAndName("Brand", "Product 1");
    }

    @Test
    void shouldReturnNoneProductByBrandAndName() {

        List<Product> mockProducts = new ArrayList<>();

        when(productRepository.findByBrandAndName("Brand", "Product 1")).thenReturn(mockProducts);

        var result = productService.getProductsByBrandAndName("Brand", "Product 1");

        assertNotNull(result, "The result should not be null");
        assertEquals(0, result.size(), "The size of the result should match the number of mock products");
        verify(productRepository, times(1)).findByBrandAndName("Brand", "Product 1");
    }

    @Test
    void shouldReturnTheCountOfProductsByBrandAndName() {

        Long listSize = 20L;

        when(productRepository.countByBrandAndName("Brand", "Product 1")).thenReturn(listSize);

        var result = productService.countProductsByBrandAndName("Brand", "Product 1");

        assertNotNull(result, "The result should not be null");
        assertEquals(20L, result);
        verify(productRepository, times(1)).countByBrandAndName("Brand", "Product 1");
    }

    @Test
    void shouldReturnZeroInTheCountOfProductsByBrandAndName() {

        Long listSize = 0L;

        when(productRepository.countByBrandAndName("Brand", "Product 1")).thenReturn(listSize);

        var result = productService.countProductsByBrandAndName("Brand", "Product 1");

        assertNotNull(result, "The result should not be null");
        assertEquals(0L, result);
        verify(productRepository, times(1)).countByBrandAndName("Brand", "Product 1");
    }

    @Test
    void shouldConvertProductToProductDto() {

        Product product = MockProduct.createProduct(1L);
        ProductDto expectedDto = MockProduct.createProductDto(1L);

        when(modelMapper.map(product, ProductDto.class)).thenReturn(expectedDto);
        when(imageRepository.findByProductId(product.getId())).thenReturn(new ArrayList<>());

        var result = productService.convertToDto(product);

        assertNotNull(result, "The result should not be null");
        assertEquals(result.getId(), expectedDto.getId());
        assertEquals(result.getName(), expectedDto.getName());
        assertEquals(result.getBrand(), expectedDto.getBrand());
        assertEquals(result.getDescription(), expectedDto.getDescription());
        assertEquals(result.getPrice(), expectedDto.getPrice());
        assertEquals(result.getImages(), new ArrayList<>());
        assertEquals(0, result.getImages().size());
    }

}
