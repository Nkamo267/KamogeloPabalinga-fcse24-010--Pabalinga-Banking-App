package com.pabalinga.bankingsystem.dao;

import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.db.DB;
import com.pabalinga.bankingsystem.util.HashUtil;

import java.sql.*;

public class UserDAO {

    public static User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, role, customer_id FROM users WHERE username=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer cid = null;
                    Object obj = rs.getObject("customer_id");
                    if (obj != null) cid = rs.getInt("customer_id");
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("role"), cid);
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
        return createUser(username, plainPassword, role, null);
    }

    public static int createUser(String username, String plainPassword, String role, Integer customerId) throws SQLException {
        String sql = "INSERT INTO users (username,password,role,customer_id) VALUES (?, ?, ?, ?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, HashUtil.sha256(plainPassword));
            ps.setString(3, role);
            if (customerId == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, customerId);
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
