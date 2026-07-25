package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RescuePetDao {

    public List<String> getAvailableRescuePets() {
        List<String> pets = new ArrayList<>();
        String sql = "SELECT pet_id, kennel_no, name, species, breed, age_display, age, gender, trait, description FROM rescue_pets WHERE LOWER(status) = 'available' ORDER BY pet_id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String petJson = String.format(
                    "{\"pet_id\":%d,\"kennel_no\":\"%s\",\"name\":\"%s\",\"species\":\"%s\",\"breed\":\"%s\",\"age_display\":\"%s\",\"age\":%d,\"gender\":\"%s\",\"trait\":\"%s\",\"description\":\"%s\"}",
                    rs.getInt("pet_id"),
                    escapeJson(rs.getString("kennel_no")),
                    escapeJson(rs.getString("name")),
                    escapeJson(rs.getString("species")),
                    escapeJson(rs.getString("breed")),
                    escapeJson(rs.getString("age_display")),
                    rs.getInt("age"),
                    escapeJson(rs.getString("gender")),
                    escapeJson(rs.getString("trait")),
                    escapeJson(rs.getString("description"))
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