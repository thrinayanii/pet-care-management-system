package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetDao {

    public boolean addPet(int userId, String name, String species, String breed, String gender, String age, String notes) {
        String sql = "INSERT INTO user_pets (user_id, name, species, breed, gender, age, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, species);
            stmt.setString(4, breed != null ? breed : "");
            stmt.setString(5, gender != null ? gender : "");
            stmt.setString(6, age != null ? age : "");
            stmt.setString(7, notes != null ? notes : "");

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getPetsByUserId(int userId) {
        List<String> pets = new ArrayList<>();
        String sql = "SELECT pet_id, name, species, breed, gender, age, notes FROM user_pets WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String petJson = String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"species\":\"%s\",\"breed\":\"%s\",\"gender\":\"%s\",\"age\":\"%s\",\"notes\":\"%s\"}",
                    rs.getInt("pet_id"),
                    escapeJson(rs.getString("name")),
                    escapeJson(rs.getString("species")),
                    escapeJson(rs.getString("breed")),
                    escapeJson(rs.getString("gender")),
                    escapeJson(rs.getString("age")),
                    escapeJson(rs.getString("notes"))
                );
                pets.add(petJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}