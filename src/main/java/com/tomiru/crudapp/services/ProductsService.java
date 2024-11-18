package com.tomiru.crudapp.services;

import com.tomiru.crudapp.models.Product;
import com.tomiru.crudapp.models.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    private final String uploadDir = "public/images/";

    public List<Product> getAllProducts() {
        return productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public String saveImage(MultipartFile image) throws Exception {
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream, Paths.get(uploadDir, storageFileName), StandardCopyOption.REPLACE_EXISTING);
        }

        return storageFileName;
    }

    public Product createProduct(ProductDto productDto, BindingResult result) {
        if (productDto.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "Image file is required"));
        }
        if (result.hasErrors()) {
            return null;
        }

        String imageFileName;
        try {
            imageFileName = saveImage(productDto.getImageFile());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save image", e);
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(new Date());
        product.setImageFileName(imageFileName);

        return productsRepository.save(product);
    }

    public Optional<Product> getProductById(int id) {
        return productsRepository.findById(id);
    }

    public Product updateProduct(int id, ProductDto productDto, BindingResult result) throws Exception {
        Optional<Product> optionalProduct = productsRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return null;
        }
        Product product = optionalProduct.get();

        if (!productDto.getImageFile().isEmpty()) {
            Path oldImagePath = Paths.get(uploadDir, product.getImageFileName());
            Files.deleteIfExists(oldImagePath);
            product.setImageFileName(saveImage(productDto.getImageFile()));
        }

        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        return productsRepository.save(product);
    }

    public void deleteProduct(int id) throws Exception {
        Optional<Product> optionalProduct = productsRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            Path imagePath = Paths.get(uploadDir, product.getImageFileName());
            Files.deleteIfExists(imagePath);
            productsRepository.delete(product);
        }
    }
}
