package com.pabalinga.bankingsystem;

import com.pabalinga.bankingsystem.dao.AccountDAO;
import com.pabalinga.bankingsystem.dao.CustomerDAO;
import com.pabalinga.bankingsystem.db.DB;
import com.pabalinga.bankingsystem.model.Account;
import com.pabalinga.bankingsystem.service.AccountService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.*;

public class AccountDeleteIT {

    private int customerId;
    private AccountService service = new AccountService();

    @Before
    public void setup() throws Exception {
        DB.init();
        // create a customer
        customerId = CustomerDAO.createCustomer("Test","User","Addr","000");
    }

    @After
    public void cleanup() throws Exception {
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM transactions")) { ps.executeUpdate(); }
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM accounts")) { ps.executeUpdate(); }
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM users")) { ps.executeUpdate(); }
        try (Connection c = DB.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM customers")) { ps.executeUpdate(); }
    }

    @Test
    public void closeAccount_withZeroBalance_marksClosed() throws Exception {
        String accNum = "ACIT" + System.currentTimeMillis();
        int accId = AccountDAO.createAccount(accNum, customerId, "CHEQUE", 0.0, "Main");
        Account acc = AccountDAO.findByAccountNumber(accNum);
        assertNotNull(acc);
        service.deleteAccount(acc);
        Account closed = AccountDAO.findByAccountNumber(accNum);
        assertNotNull(closed);
        assertEquals("CLOSED", closed.getStatus());
    }

    @Test(expected = Exception.class)
    public void closeAccount_withNonZeroBalance_throws() throws Exception {
        String accNum = "ACIT" + System.currentTimeMillis();
        int accId = AccountDAO.createAccount(accNum, customerId, "CHEQUE", 100.0, "Main");
        Account acc = AccountDAO.findByAccountNumber(accNum);
        assertNotNull(acc);
        service.deleteAccount(acc);
    }
}
