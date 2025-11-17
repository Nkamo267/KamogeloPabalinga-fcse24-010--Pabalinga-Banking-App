package com.pabalinga.bankingsystem.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String role; // ADMIN, TELLER, CUSTOMER
    private Integer customerId; // optional link to customers.id for CUSTOMER role
    public User(int id, String username, String passwordHash, String role) {
        this(id, username, passwordHash, role, null);
    }

    public User(int id, String username, String passwordHash, String role, Integer customerId) {
        this.id = id; this.username = username; this.passwordHash = passwordHash; this.role = role; this.customerId = customerId;
    }

    public User(String username, String passwordHash, String role) {
        this(username, passwordHash, role, null);
    }

    public User(String username, String passwordHash, String role, Integer customerId) {
        this.username = username; this.passwordHash = passwordHash; this.role = role; this.customerId = customerId;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public Integer getCustomerId() { return customerId; }
}
