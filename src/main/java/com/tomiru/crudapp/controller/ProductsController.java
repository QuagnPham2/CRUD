package com.tomiru.crudapp.controller;

import com.tomiru.crudapp.models.Product;
import com.tomiru.crudapp.models.ProductDto;
import com.tomiru.crudapp.services.ProductsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsService productService;

    @GetMapping({"", "/"})
    public String showProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        model.addAttribute("productDto", new ProductDto());
        return "products/createproduct";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        Product product = productService.createProduct(productDto, result);
        if (product == null) {
            return "products/createproduct";
        }
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        productService.getProductById(id).ifPresentOrElse(product -> {
            model.addAttribute("product", product);
            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            model.addAttribute("productDto", productDto);
        }, () -> model.addAttribute("error", "Product not found"));
        return "products/editproduct";
    }

    @PostMapping("/edit")
    public String updateProduct(@RequestParam int id, @Valid @ModelAttribute ProductDto productDto, BindingResult result) {
        try {
            Product product = productService.updateProduct(id, productDto, result);
            if (product == null) {
                return "products/editproduct";
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        try {
            productService.deleteProduct(id);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
