package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.controllers;



import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Product;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }


    @GetMapping("/products/{sku}")
    public Product getProductBySku(@PathVariable String sku) {
        return productService.getBySku(sku);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) throws Exception {
        return productService.createNewProduct(product);
    }




}