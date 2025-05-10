package com.function.graph1.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.function.graph1.connection.DatabaseConnection;
import com.function.graph1.model.Role;
import com.function.graph1.model.User;

public class UserService {
    // Crear un nuevo usuario
    public Long createUser(User user) throws SQLException {
        String sql = "BEGIN INSERT INTO usuarios (name, email, password) VALUES (?, ?, ?) RETURNING id INTO ?; END;";

        try (Connection connection = DatabaseConnection.getConnection();
                CallableStatement stmt = connection.prepareCall(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.registerOutParameter(4, java.sql.Types.NUMERIC); // Oracle maneja id como NUMBER

            stmt.execute();

            return stmt.getLong(4);
        }
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");

                User user = new User(id, name, email, password);

                // Agregar roles al usuario
                List<Role> roles = getRolesByUserId(id);
                user.setRoles(roles);

                users.add(user);
            }
        }
        return users;
    }

    // Actualizar un usuario
    public void updateUser(Long id, User user) throws SQLException {
        String sql = "UPDATE usuarios SET name = ?, email = ?, password = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setLong(4, id);
            stmt.executeUpdate();
        }
    }

    // Eliminar un usuario
    public void deleteUser(Long id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // Obtener usuarios filtrados por email (incluyendo sus roles)
    public List<User> getUsersByEmail(String email) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                String password = rs.getString("password");

                User user = new User(id, name, email, password);

                // Agregar roles al usuario
                List<Role> roles = getRolesByUserId(id);
                user.setRoles(roles);

                users.add(user);
            }
        }
        return users;
    }

    // Buscar un usuario por su ID 
    public User getUserById(Long id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");

                User user = new User(id, name, email, password);

                // Agregar roles al usuario
                List<Role> roles = getRolesByUserId(id);
                user.setRoles(roles);

                return user;
            } else {
                return null; // No se encontró el usuario
            }
        }
    }

    // Obtener roles de un usuario
    private List<Role> getRolesByUserId(Long userId) throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.ID, r.ROLE_NAME FROM roles r " +
                "JOIN user_roles ur ON r.id = ur.role_id " +
                "WHERE ur.user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long roleId = rs.getLong("id");
                String roleName = rs.getString("ROLE_NAME");
                roles.add(new Role(roleId, roleName));
            }
        }
        return roles;
    }

}
