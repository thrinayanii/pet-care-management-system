package com.pawcare.model;

import java.sql.Timestamp;

public class User {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String passwordHash;
    protected String phone;
    protected String role;
    protected Timestamp createdAt;

    public User() {}

    public User(int id, String firstName, String lastName, String email, String passwordHash, String phone, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // Polymorphic method to determine dashboard path
    public String getDashboardPath() {
        switch (this.role.toLowerCase()) {
            case "admin": return "../admin/admin_dashboard.html";
            case "volunteer": return "../volunteer/volunteer_dashboard.html";
            default: return "../pet_owner/petowner_dashboard.html";
        }
    }
}