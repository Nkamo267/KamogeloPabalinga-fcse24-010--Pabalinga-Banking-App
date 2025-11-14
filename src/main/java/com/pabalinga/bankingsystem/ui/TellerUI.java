package com.pabalinga.bankingsystem.ui;

import com.pabalinga.bankingsystem.dao.CustomerDAO;
import com.pabalinga.bankingsystem.dao.AccountDAO;
import com.pabalinga.bankingsystem.model.Account;
import com.pabalinga.bankingsystem.model.Customer;
import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.service.AccountService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class TellerUI {
    private final User teller;
    private final AccountService accountService = new AccountService();

    public TellerUI(User teller) { this.teller = teller; }

    public void start(Stage stage) {
        stage.setTitle("Teller Console - " + teller.getUsername());

        ObservableList<Customer> custObs = FXCollections.observableArrayList();
        ListView<Customer> custList = new ListView<>(custObs);
        custList.setPrefWidth(360);

        ObservableList<Account> accObs = FXCollections.observableArrayList();
        ListView<Account> accList = new ListView<>(accObs);
        accList.setPrefWidth(360);

        Button btnRefresh = new Button("Refresh");
        Button btnCreateCustomer = new Button("Create Customer");
        Button btnCreateAccount = new Button("Create Account for Selected Customer");
        Label lblStatus = new Label();

        btnRefresh.setOnAction(e -> { loadCustomers(custObs); loadAccounts(accObs); });

        btnCreateCustomer.setOnAction(e -> {
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setTitle("Create Customer");
            TextField fn = new TextField(); fn.setPromptText("First name");
            TextField sn = new TextField(); sn.setPromptText("Surname");
            TextField addr = new TextField(); addr.setPromptText("Address");
            TextField phone = new TextField(); phone.setPromptText("Phone");
            VBox vb = new VBox(8, new Label("First name"), fn, new Label("Surname"), sn, new Label("Address"), addr, new Label("Phone"), phone);
            vb.setPadding(new Insets(10));
            dlg.getDialogPane().setContent(vb);
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dlg.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.OK) {
                    try {
                        int custId = CustomerDAO.createCustomer(fn.getText().trim(), sn.getText().trim(), addr.getText().trim(), phone.getText().trim());
                        lblStatus.setText("Customer created id=" + custId);
                        loadCustomers(custObs);
                    } catch (SQLException ex) { ex.printStackTrace(); lblStatus.setText("Error: " + ex.getMessage()); }
                }
            });
        });

        btnCreateAccount.setOnAction(e -> {
            Customer selected = custList.getSelectionModel().getSelectedItem();
            if (selected == null) { new Alert(Alert.AlertType.WARNING, "Select a customer first").showAndWait(); return; }
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setTitle("Create Account for " + selected.getFirstname() + " " + selected.getSurname());
            ComboBox<String> types = new ComboBox<>(); types.getItems().addAll("SAVINGS","INVESTMENT","CHEQUE"); types.getSelectionModel().selectFirst();
            TextField deposit = new TextField("0");
            TextField branch = new TextField("Main");
            VBox vb = new VBox(8, new Label("Account type"), types, new Label("Initial deposit"), deposit, new Label("Branch"), branch);
            vb.setPadding(new Insets(10));
            dlg.getDialogPane().setContent(vb);
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dlg.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.OK) {
                    try {
                        Account acc = accountService.openAccount(selected.getId(), types.getValue(), Double.parseDouble(deposit.getText().trim()), branch.getText().trim());
                        new Alert(Alert.AlertType.INFORMATION, "Account created: " + acc.getAccountNumber()).showAndWait();
                        loadAccounts(accObs);
                    } catch (Exception ex) { ex.printStackTrace(); new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
                }
            });
        });

        HBox actions = new HBox(8, btnCreateCustomer, btnCreateAccount, btnRefresh);
        actions.setPadding(new Insets(8));

        VBox left = new VBox(8, new Label("Customers"), custList, actions);
        left.setPadding(new Insets(10));
        VBox right = new VBox(8, new Label("Accounts"), accList, new Label(), lblStatus);
        right.setPadding(new Insets(10));

        HBox root = new HBox(10, left, right);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 820, 500));
        stage.show();

        loadCustomers(custObs);
        loadAccounts(accObs);
    }

    private void loadCustomers(ObservableList<Customer> obs) {
        try { obs.clear(); List<Customer> all = CustomerDAO.findAll(); obs.addAll(all); } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadAccounts(ObservableList<Account> obs) {
        try { obs.clear(); obs.addAll(AccountDAO.findAll()); } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
