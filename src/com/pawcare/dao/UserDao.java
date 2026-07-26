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
import java.sql.Types;

public class UserDao {

    // Register User
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
                        insertVolunteerDetails(userId, ((Volunteer) user).getPreferredServiceId());
                    }
                }
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    private void insertVolunteerDetails(int userId, Integer preferredServiceId) {
        String sql = "INSERT INTO volunteers (user_id, preferred_service_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            if (preferredServiceId != null) {
                stmt.setInt(2, preferredServiceId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting volunteer details: " + e.getMessage());
        }
    }

    // Login User
    public User loginUser(String email, String password) {
        String sql = "SELECT u.*, v.preferred_service_id FROM users u " +
                     "LEFT JOIN volunteers v ON u.id = v.user_id " +
                     "WHERE u.email = ? AND u.password_hash = ?";

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
                            int prefIdInt = rs.getInt("preferred_service_id");
                            Integer prefServiceId = rs.wasNull() ? 4 : prefIdInt; // Default to service_id 4 (Dog Walking) if null
                            return new Volunteer(id, fName, lName, userEmail, pass, phone, prefServiceId);
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

    public boolean updateUserProfile(int userId, String firstName, String lastName, String email, String phone) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setInt(5, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }
}