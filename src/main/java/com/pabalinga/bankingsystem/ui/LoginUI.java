package com.pabalinga.bankingsystem.ui;

import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginUI {
    private final AuthService authService = new AuthService();

    public void start(Stage stage) {
        stage.setTitle("Banking System - Login");

        Label lblTitle = new Label("Banking System");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField txtUser = new TextField();
        txtUser.setPromptText("username");
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("password");
        Label lblStatus = new Label();

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText();
            try {
                User user = authService.login(u, p);
                if (user == null) {
                    lblStatus.setText("Invalid credentials");
                    return;
                }
                // route by role
                switch (user.getRole()) {
                    case "ADMIN": new AdminUI(user).start(new Stage()); break;
                    case "TELLER": new TellerUI(user).start(new Stage()); break;
                    case "CUSTOMER": new CustomerUI(user).start(new Stage()); break;
                    default: lblStatus.setText("Unknown role");
                }
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                lblStatus.setText("Error: " + ex.getMessage());
            }
        });

        VBox root = new VBox(10, lblTitle, txtUser, txtPass, btnLogin, lblStatus);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }
}
