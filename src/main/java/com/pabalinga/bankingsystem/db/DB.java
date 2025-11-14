package com.pabalinga.bankingsystem.db;

import com.pabalinga.bankingsystem.util.HashUtil;

import java.sql.*;

public class DB {
    // change credentials here if needed
    private static final String URL = "jdbc:mysql://localhost:3306/bankingdb?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException e) { e.printStackTrace(); }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Call at app startup to ensure tables exist and default admin present
    public static void init() {
        String createUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(100) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, role ENUM('ADMIN','TELLER','CUSTOMER') NOT NULL)";
        String createCustomers = "CREATE TABLE IF NOT EXISTS customers (id INT AUTO_INCREMENT PRIMARY KEY, firstname VARCHAR(100), surname VARCHAR(100), address VARCHAR(255), phone VARCHAR(50))";
        String createAccounts = "CREATE TABLE IF NOT EXISTS accounts (id INT AUTO_INCREMENT PRIMARY KEY, account_number VARCHAR(50) UNIQUE NOT NULL, customer_id INT NOT NULL, type ENUM('SAVINGS','INVESTMENT','CHEQUE') NOT NULL, balance DOUBLE DEFAULT 0, branch VARCHAR(100), status ENUM('ACTIVE','FROZEN') DEFAULT 'ACTIVE', FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE)";
        String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (id INT AUTO_INCREMENT PRIMARY KEY, account_id INT NOT NULL, type VARCHAR(50), amount DOUBLE, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, details VARCHAR(255), FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE)";

        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute(createUsers);
            s.execute(createCustomers);
            s.execute(createAccounts);
            s.execute(createTransactions);

            // ensure default admin exists (username: admin, password: admin123)
            String checkAdmin = "SELECT COUNT(*) FROM users WHERE role='ADMIN'";
            try (PreparedStatement ps = c.prepareStatement(checkAdmin); ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement ins = c.prepareStatement("INSERT INTO users (username,password,role) VALUES (?,?,?)")) {
                        ins.setString(1, "admin");
                        ins.setString(2, HashUtil.sha256("admin123"));
                        ins.setString(3, "ADMIN");
                        ins.executeUpdate();
                        System.out.println("Default admin created -> username: admin password: admin123");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
