package com.pabalinga.bankingsystem.dao;

import com.pabalinga.bankingsystem.db.DB;
import com.pabalinga.bankingsystem.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public static void createTransaction(int accountId, String type, double amount, String details) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, type, amount, details) VALUES (?,?,?,?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, details);
            ps.executeUpdate();
        }
    }

    public static List<Transaction> findByAccountId(int accountId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id=? ORDER BY id DESC";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getInt("id"), rs.getInt("account_id"), rs.getString("type"), rs.getDouble("amount"), rs.getTimestamp("timestamp"), rs.getString("details")));
                }
            }
        }
        return list;
    }
}
