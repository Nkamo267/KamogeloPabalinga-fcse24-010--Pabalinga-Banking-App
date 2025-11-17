package com.pabalinga.bankingsystem.model;

import java.util.Date;

public class Transaction {
    private int id;
    private int accountId;
    private String type;
    private double amount;
    private Date date;
    private String details;

    // for DB-loaded records
    public Transaction(int id, int accountId, String type, double amount, Date date, String details) {
        this.id = id; this.accountId = accountId; this.type = type; this.amount = amount; this.date = date; this.details = details;
    }

    // for new transaction to be saved
    public Transaction(int accountId, String type, double amount, String details) {
        this.accountId = accountId; this.type = type; this.amount = amount; this.details = details; this.date = new Date();
    }

    public int getId() { return id; }
    public int getAccountId() { return accountId; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getDetails() { return details; }

    public String toString() { return date + " | " + type + " | " + amount + " | " + details; }
}
