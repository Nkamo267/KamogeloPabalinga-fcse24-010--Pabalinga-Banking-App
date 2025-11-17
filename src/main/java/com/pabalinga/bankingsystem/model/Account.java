package com.pabalinga.bankingsystem.model;

public class Account {
    private int id;
    private String accountNumber;
    private int customerId;
    private String type; // SAVINGS, INVESTMENT, CHEQUE
    private double balance;
    private String branch;
    private String status;

    public Account(int id, String accountNumber, int customerId, String type, double balance, String branch, String status) {
        this.id = id; this.accountNumber = accountNumber; this.customerId = customerId; this.type = type; this.balance = balance; this.branch = branch; this.status = status;
    }

    public Account(String accountNumber, int customerId, String type, double balance, String branch) {
        this.accountNumber = accountNumber; this.customerId = customerId; this.type = type; this.balance = balance; this.branch = branch; this.status = "ACTIVE";
    }

    public int getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public int getCustomerId() { return customerId; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    public void setBalance(double b) { this.balance = b; }
    public String getBranch() { return branch; }
    public String getStatus() { return status; }

    public String toString() { return accountNumber + " | " + type + " | " + balance; }
}
