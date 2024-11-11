package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services;

import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Order;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Product;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.ProductCart;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class OrderService {

    private DBService dbService;

    private UserService userService;

    private ProductService productService;

    private List<String> validStatuses = Arrays.asList("NEW", "IN_PROGRESS", "COMPLETED", "CANCELLED");
    private List<String> updatableStatuses = Arrays.asList("NEW", "IN_PROGRESS");

    public OrderService(DBService dbService,
                        UserService userService,
                        ProductService productService) {
        this.dbService = dbService;
        this.userService = userService;
        this.productService = productService;
    }



    public Order createNewOrder(User user, List<ProductCart> cart) throws Exception {

        Order order = new Order();

        User existingUser = dbService.getUserByEmail(user.getEmail());
        if (existingUser == null) {
            throw new Exception("User not found");
        }

        Map<Product, Double> uniqueProducts = new HashMap<>();
        for (ProductCart productCart : cart) {
            if (!uniqueProducts.containsKey(productCart.getProduct())) {
                uniqueProducts.put(productCart.getProduct(), productCart.getCartQuantity());
            } else {
                uniqueProducts.put(productCart.getProduct(), uniqueProducts.get(productCart.getProduct()) + productCart.getCartQuantity());
            }
        }


        List<String> errors = new ArrayList<>();
        for (Map.Entry<Product, Double> entry : uniqueProducts.entrySet()) {

            Product toBeOrderedProduct = entry.getKey();
            Double toBeOrderedQuantity = entry.getValue();

            Product existingProduct = dbService.getProductBySKU(toBeOrderedProduct.getSku());

            if (existingProduct == null) {
                errors.add("Product with Name " + toBeOrderedProduct.getName() + " not found");
                continue;
            }

            if (existingProduct.getStockQuantity() < toBeOrderedQuantity) {
                errors.add("Not enough quantity for product with Name " + toBeOrderedProduct.getName());
                continue;
            }

        }


        if (!errors.isEmpty()) {
            String errorMessage = "Errors occurred while creating order:";
            for (String error : errors) {
                errorMessage += "\n" + error;
            }
            throw new Exception(errorMessage);
        }

        for (ProductCart productCart : cart) {

            Product existingProduct = dbService.getProductBySKU(productCart.getProduct().getSku());

            existingProduct.setStockQuantity(existingProduct.getStockQuantity() - productCart.getCartQuantity());
        }

        order.setOrderNumber("ORD-" + UUID.randomUUID());
        order.setOrderDate(Instant.now().toString());
        order.setOrderStatus("NEW");
        order.setUser(existingUser);
        order.setProducts(cart);

        dbService.insertOrder(order);
        System.out.println("Order created successfully with order number: " + order.getOrderNumber());

        return order;
    }


    public List<Order> getAllOrders() {

        return dbService.getAllOrders();
    }



    public Order getOrderByOrderNumber(String orderNumber) {
        return dbService.getAllOrders().stream()
                .filter(order -> order.getOrderNumber().equals(orderNumber))
                .findFirst()
                .orElse(null);
    }
}
