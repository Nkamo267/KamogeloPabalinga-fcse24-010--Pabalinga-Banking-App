package com.pabalinga.bankingsystem.service;

import com.pabalinga.bankingsystem.dao.TransactionDAO;
import com.pabalinga.bankingsystem.model.Transaction;

import java.sql.SQLException;
import java.util.List;

public class TransactionService {
    public List<Transaction> getTransactionsForAccount(int accountId) throws SQLException {
        return TransactionDAO.findByAccountId(accountId);
    }
}
