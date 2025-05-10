package com.function.graph1.service;

import java.sql.*;

import com.function.graph1.connection.DatabaseConnection;
import com.function.graph1.model.UserRole;

public class UserRoleService {

    // Asignar un rol a un usuario
    public void assignRoleToUser(UserRole userRole) throws SQLException {
        String sql = "INSERT INTO USER_ROLES (user_id, role_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, userRole.getUserId());
            stmt.setLong(2, userRole.getRoleId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("foreign key")
                    || e.getMessage().toLowerCase().contains("constraint")) {
                throw new RuntimeException("Error: El rol especificado no existe o no es válido.", e);
            }

        }
    }

    // Eliminar un rol de un usuario
    public void removeRoleFromUser(UserRole userRole) throws SQLException {
        String sql = "DELETE FROM USER_ROLES WHERE user_id = ? AND role_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, userRole.getUserId());
            stmt.setLong(2, userRole.getRoleId());
            stmt.executeUpdate();
        }
    }
}
