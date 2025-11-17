package com.pabalinga.bankingsystem.service;

import com.pabalinga.bankingsystem.dao.AccountDAO;
import com.pabalinga.bankingsystem.dao.TransactionDAO;
import com.pabalinga.bankingsystem.model.Account;
import com.pabalinga.bankingsystem.model.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AccountService {

    public Account openAccount(int customerId, String type, double initialDeposit, String branch) throws Exception {
        if ("INVESTMENT".equalsIgnoreCase(type) && initialDeposit < 500.0) throw new Exception("Investment accounts require minimum BWP500 opening deposit.");
        String accNum = "AC" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        int id = AccountDAO.createAccount(accNum, customerId, type.toUpperCase(), initialDeposit, branch);
        Account acc = AccountDAO.findByAccountNumber(accNum);
        if (initialDeposit > 0) TransactionDAO.createTransaction(acc.getId(), "DEPOSIT", initialDeposit, "Initial deposit");
        return acc;
    }

    public void deposit(Account acc, double amount) throws Exception {
        if (amount <= 0) throw new Exception("Invalid amount");
        acc.setBalance(acc.getBalance() + amount);
        AccountDAO.updateBalance(acc.getId(), acc.getBalance());
        TransactionDAO.createTransaction(acc.getId(), "DEPOSIT", amount, "Deposit");
    }

    public void withdraw(Account acc, double amount) throws Exception {
        if (amount <= 0) throw new Exception("Invalid amount");
        if ("SAVINGS".equalsIgnoreCase(acc.getType())) throw new Exception("Withdrawals not allowed from Savings");
        if (amount > acc.getBalance()) throw new Exception("Insufficient funds");
        acc.setBalance(acc.getBalance() - amount);
        AccountDAO.updateBalance(acc.getId(), acc.getBalance());
        TransactionDAO.createTransaction(acc.getId(), "WITHDRAWAL", amount, "Withdrawal");
    }

    public void transfer(Account from, Account to, double amount) throws Exception {
        if (from.getId() == to.getId()) throw new Exception("Same account");
        withdraw(from, amount);
        deposit(to, amount);
        TransactionDAO.createTransaction(from.getId(), "TRANSFER_OUT", amount, "To " + to.getAccountNumber());
        TransactionDAO.createTransaction(to.getId(), "TRANSFER_IN", amount, "From " + from.getAccountNumber());
    }

    public List<Account> getAccountsForCustomer(int customerId) throws SQLException {
        return AccountDAO.findByCustomerId(customerId);
    }

    public List<Account> getAllAccounts() throws SQLException {
        return AccountDAO.findAll();
    }

    public List<Transaction> getTransactionsForAccount(Account acc) throws SQLException {
        return TransactionDAO.findByAccountId(acc.getId());
    }

    public void applyMonthlyInterest() {
        try {
            List<Account> all = AccountDAO.findAll();
            for (Account a : all) {
                double rate = 0;
                if ("INVESTMENT".equalsIgnoreCase(a.getType())) rate = 0.05;
                if ("SAVINGS".equalsIgnoreCase(a.getType())) rate = 0.0005;
                if (rate > 0 && a.getBalance() > 0) {
                    double interest = a.getBalance() * rate;
                    a.setBalance(a.getBalance() + interest);
                    AccountDAO.updateBalance(a.getId(), a.getBalance());
                    TransactionDAO.createTransaction(a.getId(), "INTEREST", interest, "Monthly interest");
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public void deleteAccount(Account acc) throws Exception {
        if (acc == null) throw new Exception("No account provided");
        if (acc.getBalance() != 0) throw new Exception("Cannot delete account with non-zero balance");
        try {
            AccountDAO.closeAccount(acc.getId());
        } catch (SQLException ex) {
            throw new Exception("Failed to delete account: " + ex.getMessage(), ex);
        }
    }
}
