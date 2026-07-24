package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import com.pawcare.model.AdminUser;
import com.pawcare.model.PetOwner;
import com.pawcare.model.User;
import com.pawcare.model.Volunteer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDao {

    // 1. REGISTER NEW USER
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getRole());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0 && user instanceof Volunteer) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        insertVolunteerDetails(userId, ((Volunteer) user).getPreferredTask());
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    private void insertVolunteerDetails(int userId, String preferredTask) {
        String sql = "INSERT INTO volunteers (user_id, preferred_task) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, preferredTask != null ? preferredTask : "daycare");
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting volunteer details: " + e.getMessage());
        }
    }

    // 2. LOGIN USER
    public User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password_hash = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String fName = rs.getString("first_name");
                    String lName = rs.getString("last_name");
                    String userEmail = rs.getString("email");
                    String pass = rs.getString("password_hash");
                    String phone = rs.getString("phone");
                    String role = rs.getString("role");

                    switch (role.toLowerCase()) {
                        case "admin":
                            return new AdminUser(id, fName, lName, userEmail, pass, phone);
                        case "volunteer":
                            return new Volunteer(id, fName, lName, userEmail, pass, phone, "daycare");
                        default:
                            return new PetOwner(id, fName, lName, userEmail, pass, phone);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
        return null;
    }
}