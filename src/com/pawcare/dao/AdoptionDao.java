package com.pawcare.dao;

import com.pawcare.config.DatabaseConfig;
import java.sql.*;

public class AdoptionDao {

    public boolean submitApplication(int userId, int petId, String housingType, String reason) {
        String sql = "INSERT INTO adoption_applications (user_id, pet_id, housing_type, reason, status) VALUES (?, ?, ?, ?, 'Pending')";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, petId);
            stmt.setString(3, housingType);
            stmt.setString(4, reason);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}