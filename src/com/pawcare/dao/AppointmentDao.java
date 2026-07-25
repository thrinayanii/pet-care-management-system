package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    public boolean createAppointment(int userId, int petId, String serviceType, String date, String time, String notes) {
        boolean isVet = serviceType.startsWith("vet");
        String table = isVet ? "vet_appointments" : "service_appointments";
        
        int serviceId = 1;
        if (serviceType.contains("_")) {
            try {
                serviceId = Integer.parseInt(serviceType.split("_")[1]);
            } catch (Exception ignored) {}
        }

        String sql = "INSERT INTO " + table + " (user_id, pet_id, service_id, appointment_date, appointment_time, notes, status) VALUES (?, ?, ?, ?, ?, ?, 'Confirmed')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, petId);
            stmt.setInt(3, serviceId);
            stmt.setString(4, date);
            stmt.setString(5, time);
            stmt.setString(6, notes);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAppointmentsByUserId(int userId) {
        List<String> appointments = new ArrayList<>();
        
        String sql = "SELECT a.appointment_date, a.appointment_time, a.status, p.name AS pet_name, 'Service Appointment' AS service_type, a.notes " +
                     "FROM service_appointments a JOIN user_pets p ON a.pet_id = p.pet_id WHERE a.user_id = ? " +
                     "UNION " +
                     "SELECT v.appointment_date, v.appointment_time, v.status, p.name AS pet_name, 'Veterinary Visit' AS service_type, v.notes " +
                     "FROM vet_appointments v JOIN user_pets p ON v.pet_id = p.pet_id WHERE v.user_id = ? " +
                     "ORDER BY appointment_date DESC, appointment_time DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String apptJson = String.format(
                    "{\"appointment_date\":\"%s\",\"appointment_time\":\"%s\",\"status\":\"%s\",\"pet_name\":\"%s\",\"service_type\":\"%s\",\"notes\":\"%s\"}",
                    escapeJson(rs.getString("appointment_date")),
                    escapeJson(rs.getString("appointment_time")),
                    escapeJson(rs.getString("status")),
                    escapeJson(rs.getString("pet_name")),
                    escapeJson(rs.getString("service_type")),
                    escapeJson(rs.getString("notes"))
                );
                appointments.add(apptJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}