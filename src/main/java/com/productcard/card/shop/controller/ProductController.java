package com.productcard.card.shop.controller;

import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.dto.ProductDtoHateoas;
import com.productcard.card.shop.exceptions.AlreadyExistsException;
import com.productcard.card.shop.exceptions.ProductNotFoundException;
import com.productcard.card.shop.exceptions.ResourceNotFoundException;
import com.productcard.card.shop.model.Product;
import com.productcard.card.shop.request.AddProductRequest;
import com.productcard.card.shop.request.ProductUpdateRequest;
import com.productcard.card.shop.response.ApiResponse;
import com.productcard.card.shop.service.product.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
@Tag(name = "Product", description = "Endpoints for Managing Products")
public class ProductController {
    private final IProductService productService;

    @GetMapping(value = "/all",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/x-yaml"})
    @Operation(summary = "Finds all Products", description = "Finds all Products from database",
    tags = {"Product"},
    responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Sucess", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProductDto.class)))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Unathorized", responseCode = "401", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
    })
    //@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
    public ResponseEntity<ApiResponse> getAllProducts(){
        List<Product> products = productService.getAllProducts();
        List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
        return ResponseEntity.ok(new ApiResponse("Success", convertedProducts));
    }

    @Operation(summary = "Find a Product By Id", description = "Find a Product by Id from database",
            tags = {"Product"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Sucess", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Unathorized", responseCode = "401", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
            })
    //@CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("product/{productId}/product")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId){
        try {
            Product product = productService.getProductById(productId);
            ProductDto productDto = productService.convertToDto(product);

            return ResponseEntity.ok(new ApiResponse("Success", productDto));
        } catch (ResourceNotFoundException | ProductNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("product/{productId}/product/hateoas")
    public ResponseEntity<ApiResponse> getProductByIdHateoas(@PathVariable Long productId){
        try {
            Product product = productService.getProductById(productId);
            ProductDtoHateoas productDto = productService.convertToDtoHateoas(product);

            return ResponseEntity.ok(new ApiResponse("Success", productDto));
        } catch (ResourceNotFoundException | ProductNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    @Operation( summary = "Add a new product", description = "Adds a new product to the database.", tags = {"Product"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product data to be added", required = true, content = @Content( mediaType = "application/json", schema = @Schema(implementation = AddProductRequest.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "200", description = "Product added successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "409", description = "Product already exists", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Error", content = @Content)
                    }
    )
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product){
        try {
            Product theProduct = productService.addProduct(product);
            return ResponseEntity.ok(new ApiResponse("Add product success!", theProduct));
        } catch (AlreadyExistsException e){
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/product/{productId}/update")
    @Operation( summary = "Update a Product", description = "Update a product to the database.", tags = {"Product"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Product data to be updated", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductUpdateRequest.class))
            ),
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product to be updated", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product Updated successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable Long productId){
        try {
            Product theProduct = productService.updateProduct(request, productId);
            ProductDto productDto = productService.convertToDto(theProduct);

            return ResponseEntity.ok(new ApiResponse("Update product success", productDto));
        } catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}/delete")
    @Operation( summary = "Delete a Product", description = "Delte a product from the database.", tags = {"Product"},
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product to be deleted", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product Deleted successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId){
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Delete product success!", productId));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/by/brand-and-name")
    @Operation( summary = "Finds Products By Name and Brand", description = "Finds Products By Name and Brand.", tags = {"Product"},
            parameters = {
            @Parameter(name = "brandName", description = "Brand name of the product", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "productName", description = "Product name", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam String brandName, @RequestParam String productName){
        try {
            List<Product> products = productService.getProductsByBrandAndName(brandName, productName);

            if(products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }

            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/category-and-brand")
    @Operation( summary = "Finds Products By Category Name and Brand", description = "Finds Products By Category Name and Brand.", tags = {"Product"},
            parameters = {
                    @Parameter(name = "category", description = "Category name of the product", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "brand", description = "Brand name of product", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String category, @RequestParam String brand){
        try {
            List<Product> products = productService.getProductsByCategoryAndBrand(category, brand);

            if(products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }

            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/{name}/products")
    @Operation( summary = "Finds Products By Name", description = "Finds Products By Name.", tags = {"Product"},
            parameters = {
                    @Parameter(name = "name", description = "Product name", required = true, in = ParameterIn.QUERY),
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product retrieved successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name){
        try {
            List<Product> products = productService.getProductsByName(name);

            if(products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }

            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/by-brand")
    @Operation( summary = "Finds Products By Brand", description = "Finds Products By Brand.", tags = {"Product"},
            parameters = {
                    @Parameter(name = "brand", description = "Product brand", required = true, in = ParameterIn.QUERY),
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product retrieved successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> findProductByBrand(@RequestParam String brand){
        try {
            List<Product> products = productService.getAllProductsByBrand(brand);

            if(products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }

            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/{category}/all/products")
    @Operation( summary = "Finds All Products By Category", description = "Finds Products By Category Name.", tags = {"Product"},
            parameters = {
                    @Parameter(name = "category", description = "Product Category", required = true, in = ParameterIn.QUERY),
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product retrieved successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> findProductByCategory(@PathVariable String category){
        try {
            List<Product> products = productService.getAllProductsByCategory(category);

            if(products.isEmpty()){
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No products found ", null));
            }

            List<ProductDto> convertedProducts = productService.getConvertedProducts(products);
            return ResponseEntity.ok(new ApiResponse("success", convertedProducts));
        } catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/count/by-brand/and-name")
    @Operation( summary = "Count Products By Name and Brand", description = "Count Products By Name and Brand.", tags = {"Product"},
            parameters = {
                    @Parameter(name = "brand", description = "Brand name of the product", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "name", description = "Product name", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product Not Found", content = @Content( mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
            }
    )
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@RequestParam String brand, @RequestParam String name){
        try {
            var productCount = productService.countProductsByBrandAndName(brand, name);
            return ResponseEntity.ok(new ApiResponse("Product count!", productCount));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));
        }
    }

}
