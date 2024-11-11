package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.controllers;


import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Product;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.User;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.DBService;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.EShopSimulationService;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.OrderService;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private EShopSimulationService eShopSimulationService;

    private OrderService orderService;

    private DBService dbService;


    public OrderController(EShopSimulationService eShopSimulationService,
                           OrderService orderService,
                           DBService dbService) {
        this.eShopSimulationService = eShopSimulationService;
        this.orderService = orderService;
        this.dbService = dbService;
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