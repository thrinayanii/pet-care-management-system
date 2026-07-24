package com.pawcare;

import com.pawcare.dao.UserDao;
import com.pawcare.model.*;

public class TestApp {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();

        System.out.println("--- TESTING USER REGISTRATION ---");
        // Creating a new PetOwner object
        User newOwner = new PetOwner(0, "Thrinayani", "Selvanathan", "thrina@example.com", "pass123", "0771234567");

        boolean isRegistered = userDao.registerUser(newOwner);
        if (isRegistered) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("User registration failed (or email already exists).");
        }

        System.out.println("\n--- TESTING USER LOGIN ---");
        User loggedInUser = userDao.loginUser("thrina@example.com", "pass123");

        if (loggedInUser != null) {
            System.out.println("Login Successful!");
            System.out.println("Welcome, " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName() + "!");
            System.out.println("Assigned Role: " + loggedInUser.getRole());
            System.out.println("Redirect URL: " + loggedInUser.getDashboardPath());
        } else {
            System.out.println("Invalid email or password.");
        }
    }
}