package com.pabalinga.bankingsystem.service;

import com.pabalinga.bankingsystem.dao.UserDAO;
import com.pabalinga.bankingsystem.model.User;

import java.sql.SQLException;

public class AuthService {

    public User login(String username, String password) throws SQLException {
        boolean ok = UserDAO.validateCredentials(username, password);
        if (!ok) return null;
        return UserDAO.findByUsername(username);
    }

    public int createUser(String username, String password, String role) throws SQLException {
        return UserDAO.createUser(username, password, role);
    }

    public boolean resetPassword(String username, String newPassword) throws SQLException {
        return UserDAO.resetPassword(username, newPassword);
    }
}
