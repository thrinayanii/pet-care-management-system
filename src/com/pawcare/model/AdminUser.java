package com.pawcare.model;

public class AdminUser extends User {
    public AdminUser() { super(); }

    public AdminUser(int id, String firstName, String lastName, String email, String passwordHash, String phone) {
        super(id, firstName, lastName, email, passwordHash, phone, "admin");
    }
}