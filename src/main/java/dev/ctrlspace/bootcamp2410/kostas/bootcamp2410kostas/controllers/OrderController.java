package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.controllers;


import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Product;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.User;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.DBService;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.EShopSimulationService;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.OrderService;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Order;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.UserService.*;

@RestController
public class OrderController {

    private EShopSimulationService eShopSimulationService;

    private OrderService orderService;

    private DBService dbService;

    private UserService userService;


    public OrderController(EShopSimulationService eShopSimulationService,
                           OrderService orderService,
                           DBService dbService,UserService userService) {
        this.eShopSimulationService = eShopSimulationService;
        this.orderService = orderService;
        this.dbService = dbService;
        this.userService = userService;
    }

    @DeleteMapping("/orders/{orderNumber}")
    public void deleteOrder(@PathVariable String orderNumber, @RequestHeader("email") String email, @RequestHeader("password") String pass) throws Exception {

        User authenticatedUser = userService.login(email, pass);


        orderService.deleteOrder(orderNumber, authenticatedUser);
    }


    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/run-simulation")
    public List<Order> runSimulation() throws Exception {

        eShopSimulationService.run();


        return orderService.getAllOrders();
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders(){

        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{orderNumber}")
    public Order getOrderById(@PathVariable String orderNumber){
        return orderService.getOrderByOrderNumber(orderNumber);
    }




    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody Order order) throws Exception {


        //request validation - throw error
        if (order.getOrderNumber() != null ) {
            throw new Exception("Order Number should be null");
        }

        return orderService.createNewOrder(order.getUser(), order.getProducts());


    }

    @PutMapping("/orders/{orderNumber}")
    public Order updateOrder(@PathVariable String orderNumber, @RequestBody Order order, @RequestHeader("email") String email, @RequestHeader("password") String pass) throws Exception {

        User authenticatedUser = userService.login(email, pass);

        Order existongOrder = orderService.getOrderByOrderNumber(orderNumber);

        if (order.getOrderNumber() != null ) {
            throw new Exception("Order Number is not allowed in the request body...");
        }

        if (existongOrder == null) {
            throw new Exception("Order not found");
        }

        if (!authenticatedUser.getEmail().equals(existongOrder.getUser().getEmail())) {
            throw new Exception("You are not authorized to update this order");
        }

        return orderService.updateOrder(orderNumber, order, authenticatedUser);
    }



//        return orderService.createNewOrder(order.getUser(), order.getProducts());

//
//    @PutMapping("/orders/{orderNumber}")
//    public Order updateOrder(@PathVariable String orderNumber, @RequestBody Order order){
//        return orderService.updateOrder(orderNumber, order);
//    }
//
//    @DeleteMapping("/orders/{orderNumber}")
//    public void deleteOrder(@PathVariable String orderNumber){
//        orderService.deleteOrder(orderNumber);
//    }



}