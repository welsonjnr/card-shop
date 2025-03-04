package com.productcard.card.shop.mapper;

import com.productcard.card.shop.dto.ImageDto;
import com.productcard.card.shop.dto.ProductDto;
import com.productcard.card.shop.model.Image;
import com.productcard.card.shop.model.Product;
import com.productcard.card.shop.repository.ImageRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    public ProductDto convertToDto(Product product){
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
}
