package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.controllers;

import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.User;
import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) throws Exception {
        // Validate request body and delegate to service method
        return userService.createNewUser(user);
    }

}
