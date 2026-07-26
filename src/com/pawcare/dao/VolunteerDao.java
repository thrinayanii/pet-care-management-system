package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolunteerDao {

    public String getVolunteerDashboardData(int userId) {
        String firstName = "Volunteer";
        String lastName = "";
        String email = "";
        String phone = "";
        Integer preferredServiceId = null;

        List<String> tasks = new ArrayList<>();
        List<String> shifts = new ArrayList<>();
        int approvedCount = 0;

        try (Connection conn = DatabaseConfig.getConnection()) {

            // 1. Fetch User Info & Preferred Service ID
            String userSql = "SELECT u.first_name, u.last_name, u.email, u.phone, v.preferred_service_id " +
                             "FROM users u " +
                             "LEFT JOIN volunteers v ON u.id = v.user_id " +
                             "WHERE u.id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    firstName = escapeJson(rs.getString("first_name"));
                    lastName = escapeJson(rs.getString("last_name"));
                    email = escapeJson(rs.getString("email"));
                    phone = escapeJson(rs.getString("phone"));
                    int prefId = rs.getInt("preferred_service_id");
                    if (!rs.wasNull()) {
                        preferredServiceId = prefId;
                    }
                }
            }

            // 2. Fetch ONLY Volunteer-Eligible Services ('walking', 'daycare', 'boarding') that are Unassigned
            String taskSql = "SELECT sa.appointment_id, s.service_id, s.name AS title, sa.appointment_date, " +
                             "sa.appointment_time, 'Shelter Facility' AS location " +
                             "FROM service_appointments sa " +
                             "JOIN other_services s ON sa.service_id = s.service_id " +
                             "WHERE sa.assigned_employee_id IS NULL " +
                             "AND sa.assigned_volunteer_id IS NULL " +
                             "AND s.category IN ('walking', 'daycare', 'boarding') " +
                             "AND sa.appointment_id NOT IN (" +
                             "    SELECT task_id FROM volunteer_shift_requests WHERE status = 'Approved'" +
                             ") " +
                             "ORDER BY (s.service_id = ?) DESC, sa.appointment_date ASC";

            try (PreparedStatement stmt = conn.prepareStatement(taskSql)) {
                stmt.setObject(1, preferredServiceId, Types.INTEGER);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    boolean isPreferred = preferredServiceId != null && preferredServiceId == rs.getInt("service_id");
                    tasks.add(String.format(
                        "{\"task_id\":%d,\"title\":\"%s\",\"task_date\":\"%s\",\"time\":\"%s\",\"location\":\"%s\",\"is_preferred\":%b,\"icon\":\"pets\"}",
                        rs.getInt("appointment_id"),
                        escapeJson(rs.getString("title")),
                        escapeJson(rs.getString("appointment_date")),
                        escapeJson(rs.getString("appointment_time")),
                        escapeJson(rs.getString("location")),
                        isPreferred
                    ));
                }
            }

            // 3. Fetch User Requested Shifts
            String shiftSql = "SELECT r.request_id, r.status, s.name AS title, sa.appointment_date, 'Shelter Facility' AS location " +
                              "FROM volunteer_shift_requests r " +
                              "JOIN service_appointments sa ON r.task_id = sa.appointment_id " +
                              "JOIN other_services s ON sa.service_id = s.service_id " +
                              "WHERE r.user_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(shiftSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String status = escapeJson(rs.getString("status"));
                    if ("Approved".equalsIgnoreCase(status)) {
                        approvedCount++;
                    }
                    shifts.add(String.format(
                        "{\"id\":%d,\"status\":\"%s\",\"task_title\":\"%s\",\"date\":\"%s\",\"location\":\"%s\"}",
                        rs.getInt("request_id"),
                        status,
                        escapeJson(rs.getString("title")),
                        escapeJson(rs.getString("appointment_date")),
                        escapeJson(rs.getString("location"))
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return String.format(
            "{\"user\":{\"first_name\":\"%s\",\"last_name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\"},\"tasks\":[%s],\"shifts\":[%s],\"approved_count\":%d}",
            firstName, lastName, email, phone, String.join(",", tasks), String.join(",", shifts), approvedCount
        );
    }

    public List<String> getAvailableTasks() {
        List<String> tasks = new ArrayList<>();
        String sql = "SELECT sa.appointment_id, s.name AS title, sa.appointment_date, sa.appointment_time, 'Shelter Facility' AS location " +
                     "FROM service_appointments sa " +
                     "JOIN other_services s ON sa.service_id = s.service_id " +
                     "WHERE sa.assigned_employee_id IS NULL " +
                     "AND sa.assigned_volunteer_id IS NULL " +
                     "AND s.category IN ('walking', 'daycare', 'boarding') " +
                     "ORDER BY sa.appointment_date ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tasks.add(String.format(
                    "{\"task_id\":%d,\"title\":\"%s\",\"task_date\":\"%s\",\"time\":\"%s\",\"location\":\"%s\",\"icon\":\"pets\"}",
                    rs.getInt("appointment_id"),
                    escapeJson(rs.getString("title")),
                    escapeJson(rs.getString("appointment_date")),
                    escapeJson(rs.getString("appointment_time")),
                    escapeJson(rs.getString("location"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public boolean applyForTask(int userId, int taskId) {
        String sql = "INSERT INTO volunteer_shift_requests (user_id, task_id, status) VALUES (?, ?, 'Pending')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, taskId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelShiftRequest(int userId, int requestId) {
        String sql = "DELETE FROM volunteer_shift_requests WHERE request_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean submitInquiry(int userId, String subject, String category, String message) {
        String sql = "INSERT INTO volunteer_inquiries (user_id, subject, category, message, status) VALUES (?, ?, ?, ?, 'Pending')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, subject);
            stmt.setString(3, category);
            stmt.setString(4, message);

            return stmt.executeUpdate() > 0;
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