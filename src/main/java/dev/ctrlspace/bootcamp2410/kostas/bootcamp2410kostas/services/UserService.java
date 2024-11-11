package dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.services;

import dev.ctrlspace.bootcamp2410.kostas.bootcamp2410kostas.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {

    private DBService dbService;

    public UserService(DBService dbService) {
        this.dbService = dbService;
    }

    public List<User> getAllUsers() {
        return dbService.getAllUsers();
    }

    public User getByEmail(String email) {
        return dbService.getUserByEmail(email);
    }

    public User createNewUser(User user) throws Exception {
        // Validate the user input
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new Exception("User email cannot be null or empty");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new Exception("User name cannot be null or empty");
        }

        // Check if a user with the same email already exists
        User existingUser = dbService.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            throw new Exception("User with email " + user.getEmail() + " already exists");
        }

        // Insert the new user into the database
        dbService.insertUser(user);
        System.out.println("User created successfully with email: " + user.getEmail());

        return user;
    }
}



