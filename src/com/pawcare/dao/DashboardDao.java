package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardDao {

    public String getDashboardData(int userId) {
        String firstName = "Pet Owner";
        String lastName = "";
        String email = "";
        String phone = "";

        List<String> pets = new ArrayList<>();
        List<String> appointments = new ArrayList<>();
        int adoptionCount = 0;

        try (Connection conn = DatabaseConfig.getConnection()) {
            
            // Fetch Profile Info
            String userSql = "SELECT first_name, last_name, email, phone FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    firstName = escapeJson(rs.getString("first_name"));
                    lastName = escapeJson(rs.getString("last_name"));
                    email = escapeJson(rs.getString("email"));
                    phone = escapeJson(rs.getString("phone"));
                }
            }

            // Fetch User Pets
            String petSql = "SELECT pet_id, name, species, breed, gender, age FROM user_pets WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(petSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    pets.add(String.format("{\"id\":%d,\"name\":\"%s\",\"species\":\"%s\",\"breed\":\"%s\",\"gender\":\"%s\",\"age\":\"%s\"}",
                            rs.getInt("pet_id"),
                            escapeJson(rs.getString("name")),
                            escapeJson(rs.getString("species")),
                            escapeJson(rs.getString("breed")),
                            escapeJson(rs.getString("gender")),
                            escapeJson(rs.getString("age"))));
                }
            }

            // Fetch User Appointments
            String apptSql = "SELECT a.appointment_date, a.appointment_time, a.status, p.name AS pet_name, 'Service Appointment' AS service_type " +
                             "FROM service_appointments a JOIN user_pets p ON a.pet_id = p.pet_id WHERE a.user_id = ? " +
                             "UNION ALL " +
                             "SELECT v.appointment_date, v.appointment_time, v.status, p.name AS pet_name, 'Veterinary Visit' AS service_type " +
                             "FROM vet_appointments v JOIN user_pets p ON v.pet_id = p.pet_id WHERE v.user_id = ? " +
                             "ORDER BY appointment_date DESC, appointment_time DESC";
            try (PreparedStatement stmt = conn.prepareStatement(apptSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    appointments.add(String.format("{\"appointment_date\":\"%s\",\"appointment_time\":\"%s\",\"status\":\"%s\",\"pet_name\":\"%s\",\"service_type\":\"%s\"}",
                            escapeJson(rs.getString("appointment_date")),
                            escapeJson(rs.getString("appointment_time")),
                            escapeJson(rs.getString("status")),
                            escapeJson(rs.getString("pet_name")),
                            escapeJson(rs.getString("service_type"))));
                }
            }

            // Count for Adoption Inquiries
            String adoptSql = "SELECT COUNT(*) AS total FROM adoption_applications WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(adoptSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    adoptionCount = rs.getInt("total");
                }
            } catch (SQLException e) {
                System.err.println("Adoption count query error: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.format("{\"user\":{\"first_name\":\"%s\",\"last_name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"},\"pets\":[%s],\"appointments\":[%s],\"adoption_count\":%d}",
                firstName, lastName, email, phone, String.join(",", pets), String.join(",", appointments), adoptionCount);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", " ")
                    .replace("\r", " ")
                    .replace("\t", " ");
    }
}