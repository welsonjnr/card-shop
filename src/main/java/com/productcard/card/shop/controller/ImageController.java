package com.productcard.card.shop.controller;

import com.productcard.card.shop.dto.ImageDto;
import com.productcard.card.shop.exceptions.ResourceNotFoundException;
import com.productcard.card.shop.model.Image;
import com.productcard.card.shop.response.ApiReponse;
import com.productcard.card.shop.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiReponse> saveImages(@RequestParam List<MultipartFile> files,
                                                 @RequestParam Long productId){
        try {
            List<ImageDto> imageDtos = imageService.saveImages(files, productId);
            return ResponseEntity.ok(new ApiReponse("Upload success!", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiReponse("Upload failed!", e.getMessage()));
        }
    }

    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        Image image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage()
                .getBytes(1, (int) image.getImage().length()));

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/image/{imageId}/update")
    public ResponseEntity<ApiReponse> updateImage(@PathVariable Long imageId, @RequestBody MultipartFile file){
        try {
            Image image = imageService.getImageById(imageId);
            if(image != null){
                imageService.updateImage(file, imageId);
                return ResponseEntity.ok(new ApiReponse("Update success!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiReponse(e.getMessage(), null));
        }

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiReponse("Update failed!", INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/image/{imageId}/delete")
    public ResponseEntity<ApiReponse> deleteImage(@PathVariable Long imageId){
        try {
            Image image = imageService.getImageById(imageId);
            if(image != null){
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiReponse("Delete success!", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiReponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiReponse("Delete success!", INTERNAL_SERVER_ERROR));
    }

}
