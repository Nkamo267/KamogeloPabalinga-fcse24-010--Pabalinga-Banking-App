package com.pabalinga.bankingsystem.dao;

import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.db.DB;
import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.util.HashUtil;

import java.sql.*;

public class UserDAO {

    public static User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, role FROM users WHERE username=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role"));
                }
            }
        }
        return null;
    }

    public static boolean validateCredentials(String username, String password) throws SQLException {
        User u = findByUsername(username);
        if (u == null) return false;
        return u.getPasswordHash().equals(HashUtil.sha256(password));
    }

    public static int createUser(String username, String plainPassword, String role) throws SQLException {
        String sql = "INSERT INTO users (username,password,role) VALUES (?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, HashUtil.sha256(plainPassword));
            ps.setString(3, role);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public static boolean resetPassword(String username, String newPlainPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE username=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, HashUtil.sha256(newPlainPassword));
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }
}
