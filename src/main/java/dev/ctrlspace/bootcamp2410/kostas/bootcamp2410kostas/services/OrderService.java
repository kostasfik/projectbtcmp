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

    /**
     * Gets a Map of products and their quantities and validates if the products are available in stock
     *
     * @param uniqueProducts
     * @throws Exception
     */
    private void validateProductAvailable(Map<Product, Double> uniqueProducts) throws Exception {
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

    public void deleteOrder(String orderNumber, User authenticatedUser) throws Exception {

        Order order = getOrderByOrderNumber(orderNumber);

        if(order == null) {
            throw new Exception("Order not found");
        }

        if (!authenticatedUser.getEmail().equals(order.getUser().getEmail())) {
            throw new Exception("You are not authorized to delete this order");
        }


        dbService.deleteOrder(order);

    }

    private static Map<Product, Double> getUniqueProducts(List<ProductCart> cart) {
        Map<Product, Double> uniqueProducts = new HashMap<>();
        for (ProductCart productCart : cart) {
            if (!uniqueProducts.containsKey(productCart.getProduct())) {
                uniqueProducts.put(productCart.getProduct(), productCart.getCartQuantity());
            } else {
                uniqueProducts.put(productCart.getProduct(), uniqueProducts.get(productCart.getProduct()) + productCart.getCartQuantity());
            }
        }
        return uniqueProducts;
    }


    public Order updateOrder(String orderNumber, Order updateOrder, User authenticatedUser) throws Exception {

        Order existingOrder = getOrderByOrderNumber(orderNumber);

        if (!validStatuses.contains(updateOrder.getOrderStatus())) {
            throw new Exception("Invalid order status");
        }

        if(updatableStatuses.contains(updateOrder.getOrderStatus())) {
            existingOrder.setOrderStatus(updateOrder.getOrderStatus());
        }

        User newUser = userService.getByEmail(updateOrder.getUser().getEmail());
        existingOrder.setUser(newUser);


        Map<Product, Double> uniqueProducts = getUniqueProducts(updateOrder.getProducts());

        validateProductAvailable(uniqueProducts);

        productService.returnProductsToStock(existingOrder.getProducts());
        productService.removeProductsFromStock(updateOrder.getProducts());

        List<ProductCart> cart = updateOrder.getProducts();
        for (ProductCart productCart : updateOrder.getProducts()) {
            Product existingProduct = dbService.getProductBySKU(productCart.getProduct().getSku());
            productCart.setProduct(existingProduct);
        }

        existingOrder.setProducts(updateOrder.getProducts());

        return existingOrder;
    }

}
