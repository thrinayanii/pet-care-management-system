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

        try (Connection conn = DatabaseConfig.getConnection()) {
            
            // 1. Fetch Profile Info
            String userSql = "SELECT first_name, last_name, email, phone FROM users WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    firstName = rs.getString("first_name") != null ? rs.getString("first_name") : "";
                    lastName = rs.getString("last_name") != null ? rs.getString("last_name") : "";
                    email = rs.getString("email") != null ? rs.getString("email") : "";
                    phone = rs.getString("phone") != null ? rs.getString("phone") : "";
                }
            }

            // 2. Fetch User Pets
            String petSql = "SELECT pet_id, name, species, breed, gender, age FROM user_pets WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(petSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    pets.add(String.format("{\"id\":%d,\"name\":\"%s\",\"breed\":\"%s\",\"gender\":\"%s\"}",
                            rs.getInt("pet_id"),
                            rs.getString("name") != null ? rs.getString("name") : "",
                            rs.getString("breed") != null ? rs.getString("breed") : "",
                            rs.getString("gender") != null ? rs.getString("gender") : ""));
                }
            }

            // 3. Fetch User Appointments (Fixed p.pet_id join)
            String apptSql = "SELECT a.appointment_date, a.appointment_time, a.status, p.name AS pet_name, 'Service Appointment' AS service_type " +
                             "FROM service_appointments a JOIN user_pets p ON a.pet_id = p.pet_id WHERE a.user_id = ? " +
                             "UNION " +
                             "SELECT v.appointment_date, v.appointment_time, v.status, p.name AS pet_name, 'Veterinary Visit' AS service_type " +
                             "FROM vet_appointments v JOIN user_pets p ON v.pet_id = p.pet_id WHERE v.user_id = ? " +
                             "ORDER BY appointment_date DESC, appointment_time DESC";
            try (PreparedStatement stmt = conn.prepareStatement(apptSql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    appointments.add(String.format("{\"appointment_date\":\"%s\",\"appointment_time\":\"%s\",\"status\":\"%s\",\"pet_name\":\"%s\",\"service_type\":\"%s\"}",
                            rs.getString("appointment_date"),
                            rs.getString("appointment_time"),
                            rs.getString("status"),
                            rs.getString("pet_name"),
                            rs.getString("service_type")));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.format("{\"user\":{\"first_name\":\"%s\",\"last_name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"},\"pets\":[%s],\"appointments\":[%s]}",
                firstName, lastName, email, phone, String.join(",", pets), String.join(",", appointments));
    }
}