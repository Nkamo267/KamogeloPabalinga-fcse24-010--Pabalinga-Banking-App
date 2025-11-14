package com.pabalinga.bankingsystem.ui;

import com.pabalinga.bankingsystem.dao.CustomerDAO;
import com.pabalinga.bankingsystem.model.Customer;
import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.service.AccountService;
import com.pabalinga.bankingsystem.service.AuthService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AdminUI {
    private final User admin;
    private final AuthService authService = new AuthService();

    public AdminUI(User admin) { this.admin = admin; }

    public void start(Stage stage) {
        stage.setTitle("Admin Console - " + admin.getUsername());

        ObservableList<Customer> custObs = FXCollections.observableArrayList();
        ListView<Customer> custList = new ListView<>(custObs);
        custList.setPrefWidth(320);

        Button btnRefresh = new Button("Refresh Customers");
        Button btnCreateUser = new Button("Create User");
        Button btnResetPwd = new Button("Reset Password");
        Button btnApplyInterest = new Button("Apply Interest");
        Label lblStatus = new Label();

        btnRefresh.setOnAction(e -> loadCustomers(custObs));
        btnCreateUser.setOnAction(e -> showCreateUserDialog(lblStatus));
        btnResetPwd.setOnAction(e -> showResetPwdDialog(lblStatus));
        btnApplyInterest.setOnAction(e -> {
            new AccountService().applyMonthlyInterest();
            lblStatus.setText("Interest applied");
        });

        VBox right = new VBox(10, btnCreateUser, btnResetPwd, btnApplyInterest, lblStatus);
        right.setPadding(new Insets(10));

        HBox root = new HBox(10, new VBox(new Label("Customers"), custList, btnRefresh), right);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 800, 500));
        stage.show();

        loadCustomers(custObs);
    }

    private void loadCustomers(ObservableList<Customer> obs) {
        try {
            obs.clear();
            List<Customer> all = CustomerDAO.findAll();
            obs.addAll(all);
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void showCreateUserDialog(Label status) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Create User (Admin)");
        TextField u = new TextField(); u.setPromptText("username");
        PasswordField p = new PasswordField(); p.setPromptText("password");
        ComboBox<String> roles = new ComboBox<>(); roles.getItems().addAll("ADMIN","TELLER","CUSTOMER"); roles.getSelectionModel().select("CUSTOMER");
        VBox vb = new VBox(8, new Label("Username"), u, new Label("Password"), p, new Label("Role"), roles);
        vb.setPadding(new Insets(8));
        dlg.getDialogPane().setContent(vb);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    authService.createUser(u.getText().trim(), p.getText(), roles.getValue());
                    status.setText("User created");
                } catch (SQLException ex) { ex.printStackTrace(); status.setText("Error: " + ex.getMessage()); }
            }
        });
    }

    private void showResetPwdDialog(Label status) {
        TextInputDialog tid = new TextInputDialog();
        tid.setHeaderText("Enter username to reset password to 'password123'");
        tid.showAndWait().ifPresent(username -> {
            try {
                boolean ok = authService.resetPassword(username, "password123");
                status.setText(ok ? "Password reset" : "User not found");
            } catch (SQLException ex) { ex.printStackTrace(); status.setText("Error: " + ex.getMessage()); }
        });
    }
}
