package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services;

import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private DBService dbService;

    public ProductService(DBService dbService) {
        this.dbService = dbService;
    }

    public List<Product> getAllProducts() {
        return dbService.getAllProducts();
    }

    public Product getBySku(String sku) {
        return dbService.getProductBySKU(sku);
    }


    public Product createNewProduct(Product product) throws Exception {
        // Validate the product input
        if (product.getSku() == null || product.getSku().isEmpty()) {
            throw new Exception("Product SKU cannot be null or empty");
        }
        if (product.getName() == null || product.getName().isEmpty()) {
            throw new Exception("Product name cannot be null or empty");
        }
        if (product.getStockQuantity() < 0) {
            throw new Exception("Stock quantity cannot be negative");
        }

        // Check if product with the same SKU already exists
        Product existingProduct = dbService.getProductBySKU(product.getSku());
        if (existingProduct != null) {
            throw new Exception("Product with SKU " + product.getSku() + " already exists");
        }

        // Insert the new product into the database
        dbService.insertProduct(product);
        System.out.println("Product created successfully with SKU: " + product.getSku());

        return product;
    }


}
