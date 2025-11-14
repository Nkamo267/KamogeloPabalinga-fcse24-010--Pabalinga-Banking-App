package com.pabalinga.bankingsystem.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.pabalinga.bankingsystem.db.DB;
import com.pabalinga.bankingsystem.model.Account;

public class AccountDAO {

    public static int createAccount(String accountNumber, int customerId, String type, double balance, String branch) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, customer_id, type, balance, branch) VALUES (?,?,?,?,?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, accountNumber);
            ps.setInt(2, customerId);
            ps.setString(3, type);
            ps.setDouble(4, balance);
            ps.setString(5, branch);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    private static Account loadFromRs(ResultSet rs) throws SQLException {
        return new Account(rs.getInt("id"), rs.getString("account_number"), rs.getInt("customer_id"), rs.getString("type"), rs.getDouble("balance"), rs.getString("branch"), rs.getString("status"));
    }

    public static Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return loadFromRs(rs);
            }
        }
        return null;
    }

    public static List<Account> findByCustomerId(int customerId) throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(loadFromRs(rs));
            }
        }
        return list;
    }

    public static List<Account> findAll() throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection c = DB.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) list.add(loadFromRs(rs));
        }
        return list;
    }

    public static void updateBalance(int accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance=? WHERE id=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }
}
