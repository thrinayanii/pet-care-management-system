package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDao {

    // Fetch complete overview data for Admin Dashboard
    public String getAdminDashboardData() {
        int totalUsers = 0;
        int pendingShifts = 0;
        int pendingAppts = 0;
        int totalRescuePets = 0;

        List<String> shiftRequests = new ArrayList<>();
        List<String> appointments = new ArrayList<>();
        List<String> rescuePets = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection()) {

            // 1. Stats Counter
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) totalUsers = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM volunteer_shift_requests WHERE status = 'Pending'");
                if (rs.next()) pendingShifts = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM service_appointments WHERE status = 'pending'");
                if (rs.next()) pendingAppts = rs.getInt(1);

                rs = stmt.executeQuery("SELECT COUNT(*) FROM rescue_pets");
                if (rs.next()) totalRescuePets = rs.getInt(1);
            }

            // 2. Pending Volunteer Shift Requests
            String shiftSql = "SELECT r.request_id, u.first_name, u.last_name, s.name AS task_title, sa.appointment_date, r.status " +
                              "FROM volunteer_shift_requests r " +
                              "JOIN users u ON r.user_id = u.id " +
                              "JOIN service_appointments sa ON r.task_id = sa.appointment_id " +
                              "JOIN other_services s ON sa.service_id = s.service_id " +
                              "ORDER BY r.requested_at DESC";

            try (PreparedStatement stmt = conn.prepareStatement(shiftSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    shiftRequests.add(String.format(
                        "{\"request_id\":%d,\"volunteer_name\":\"%s %s\",\"task_title\":\"%s\",\"date\":\"%s\",\"status\":\"%s\"}",
                        rs.getInt("request_id"),
                        escapeJson(rs.getString("first_name")),
                        escapeJson(rs.getString("last_name")),
                        escapeJson(rs.getString("task_title")),
                        escapeJson(rs.getString("appointment_date")),
                        escapeJson(rs.getString("status"))
                    ));
                }
            }

            // 3. Service Appointments List
            String apptSql = "SELECT sa.appointment_id, u.first_name, u.last_name, s.name AS service_name, " +
                             "sa.appointment_date, sa.appointment_time, sa.status " +
                             "FROM service_appointments sa " +
                             "JOIN users u ON sa.user_id = u.id " +
                             "JOIN other_services s ON sa.service_id = s.service_id " +
                             "ORDER BY sa.appointment_date ASC";

            try (PreparedStatement stmt = conn.prepareStatement(apptSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(String.format(
                        "{\"appointment_id\":%d,\"owner_name\":\"%s %s\",\"service_name\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"status\":\"%s\"}",
                        rs.getInt("appointment_id"),
                        escapeJson(rs.getString("first_name")),
                        escapeJson(rs.getString("last_name")),
                        escapeJson(rs.getString("service_name")),
                        escapeJson(rs.getString("appointment_date")),
                        escapeJson(rs.getString("appointment_time")),
                        escapeJson(rs.getString("status"))
                    ));
                }
            }

            // 4. Rescue Pets List
            String petSql = "SELECT pet_id, kennel_no, name, species, breed, status FROM rescue_pets ORDER BY pet_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(petSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rescuePets.add(String.format(
                        "{\"pet_id\":%d,\"kennel_no\":\"%s\",\"name\":\"%s\",\"species\":\"%s\",\"breed\":\"%s\",\"status\":\"%s\"}",
                        rs.getInt("pet_id"),
                        escapeJson(rs.getString("kennel_no")),
                        escapeJson(rs.getString("name")),
                        escapeJson(rs.getString("species")),
                        escapeJson(rs.getString("breed")),
                        escapeJson(rs.getString("status"))
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.format(
            "{\"stats\":{\"total_users\":%d,\"pending_shifts\":%d,\"pending_appts\":%d,\"total_pets\":%d},\"shift_requests\":[%s],\"appointments\":[%s],\"rescue_pets\":[%s]}",
            totalUsers, pendingShifts, pendingAppts, totalRescuePets,
            String.join(",", shiftRequests),
            String.join(",", appointments),
            String.join(",", rescuePets)
        );
    }

    // Approve or Reject Shift Request
    public boolean updateShiftStatus(int requestId, String newStatus) {
        String updateReqSql = "UPDATE volunteer_shift_requests SET status = ? WHERE request_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateReqSql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, requestId);
            boolean success = stmt.executeUpdate() > 0;

            // If approved, assign volunteer to service appointment so it drops off open pool
            if (success && "Approved".equalsIgnoreCase(newStatus)) {
                String assignSql = "UPDATE service_appointments sa " +
                                   "JOIN volunteer_shift_requests r ON sa.appointment_id = r.task_id " +
                                   "JOIN volunteers v ON r.user_id = v.user_id " +
                                   "SET sa.assigned_volunteer_id = v.volunteer_id " +
                                   "WHERE r.request_id = ?";
                try (PreparedStatement assignStmt = conn.prepareStatement(assignSql)) {
                    assignStmt.setInt(1, requestId);
                    assignStmt.executeUpdate();
                }
            }

            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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