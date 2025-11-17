package com.pabalinga.bankingsystem.dao;

import com.pabalinga.bankingsystem.db.DB;
import com.pabalinga.bankingsystem.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public static int createCustomer(String firstname, String surname, String address, String phone) throws SQLException {
        String sql = "INSERT INTO customers (firstname, surname, address, phone) VALUES (?,?,?,?)";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, firstname);
            ps.setString(2, surname);
            ps.setString(3, address);
            ps.setString(4, phone);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public static Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getInt("id"), rs.getString("firstname"), rs.getString("surname"), rs.getString("address"), rs.getString("phone"));
                }
            }
        }
        return null;
    }

    public static List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection c = DB.getConnection(); Statement s = c.createStatement(); ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Customer(rs.getInt("id"), rs.getString("firstname"), rs.getString("surname"), rs.getString("address"), rs.getString("phone")));
            }
        }
        return list;
    }
}
