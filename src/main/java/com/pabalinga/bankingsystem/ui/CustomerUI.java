package com.pabalinga.bankingsystem.ui;

import com.pabalinga.bankingsystem.dao.AccountDAO;
import com.pabalinga.bankingsystem.model.Account;
import com.pabalinga.bankingsystem.model.User;
import com.pabalinga.bankingsystem.service.AccountService;
import com.pabalinga.bankingsystem.dao.TransactionDAO;
import com.pabalinga.bankingsystem.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class CustomerUI {
    private final User customerUser;
    private final AccountService accountService = new AccountService();
    private final ObservableList<Account> accObs = FXCollections.observableArrayList();

    public CustomerUI(User customerUser) { this.customerUser = customerUser; }

    public void start(Stage stage) {
        stage.setTitle("Customer Portal - " + customerUser.getUsername());

        ListView<Account> accountList = new ListView<>(accObs);
        accountList.setPrefWidth(360);
        TextArea details = new TextArea(); details.setEditable(false); details.setPrefWidth(360);

        Button btnRefresh = new Button("Refresh");
        Button btnViewTxns = new Button("View Transactions");
        Button btnDeposit = new Button("Deposit");
        Button btnWithdraw = new Button("Withdraw");
        Button btnTransfer = new Button("Transfer");

        btnRefresh.setOnAction(e -> loadAccounts());
        btnViewTxns.setOnAction(e -> {
            Account a = accountList.getSelectionModel().getSelectedItem();
            if (a == null) { new Alert(Alert.AlertType.WARNING, "Select account").showAndWait(); return; }
            TransactionView tv = new TransactionView(a);
            tv.showAndWait();
        });
        btnDeposit.setOnAction(e -> doDeposit(accountList));
        btnWithdraw.setOnAction(e -> doWithdraw(accountList));
        btnTransfer.setOnAction(e -> doTransfer(accountList));

        accountList.getSelectionModel().selectedItemProperty().addListener((obs,oldV,newV)->{
            if (newV != null) details.setText("Account: " + newV.getAccountNumber() + "\nType: " + newV.getType() + "\nBalance: " + newV.getBalance());
        });

        HBox txControls = new HBox(8, btnDeposit, btnWithdraw, btnTransfer, btnViewTxns, btnRefresh);
        txControls.setPadding(new Insets(8));

        VBox left = new VBox(8, new Label("Your Accounts"), accountList, txControls);
        left.setPadding(new Insets(10));
        VBox right = new VBox(8, new Label("Details"), details);
        right.setPadding(new Insets(10));

        HBox root = new HBox(10, left, right);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 820, 480));
        stage.show();

        loadAccounts();
    }

    private void loadAccounts() {
        try {
            accObs.clear();
            // In this demo we map user.id -> customerId for customers
            accObs.addAll(accountService.getAccountsForCustomer(customerUser.getId()));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void doDeposit(ListView<Account> list) {
        Account acc = list.getSelectionModel().getSelectedItem();
        if (acc == null) { new Alert(Alert.AlertType.WARNING, "Select account").showAndWait(); return; }
        TextInputDialog tid = new TextInputDialog(); tid.setHeaderText("Amount to deposit:");
        tid.showAndWait().ifPresent(s -> {
            try { double amt = Double.parseDouble(s); accountService.deposit(acc, amt); new Alert(Alert.AlertType.INFORMATION, "Deposited").showAndWait(); loadAccounts(); }
            catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
        });
    }

    private void doWithdraw(ListView<Account> list) {
        Account acc = list.getSelectionModel().getSelectedItem();
        if (acc == null) { new Alert(Alert.AlertType.WARNING, "Select account").showAndWait(); return; }
        TextInputDialog tid = new TextInputDialog(); tid.setHeaderText("Amount to withdraw:");
        tid.showAndWait().ifPresent(s -> {
            try { double amt = Double.parseDouble(s); accountService.withdraw(acc, amt); new Alert(Alert.AlertType.INFORMATION, "Withdrawn").showAndWait(); loadAccounts(); }
            catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
        });
    }

    private void doTransfer(ListView<Account> list) {
        Account from = list.getSelectionModel().getSelectedItem();
        if (from == null) { new Alert(Alert.AlertType.WARNING, "Select source account").showAndWait(); return; }
        Dialog<ButtonType> dlg = new Dialog<>();
        TextField target = new TextField(); target.setPromptText("Target account number");
        TextField amt = new TextField(); amt.setPromptText("Amount");
        VBox vb = new VBox(8, new Label("Target account number"), target, new Label("Amount"), amt); vb.setPadding(new Insets(8));
        dlg.getDialogPane().setContent(vb);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                try {
                    Account to = AccountDAO.findByAccountNumber(target.getText().trim());
                    if (to == null) { new Alert(Alert.AlertType.WARNING, "Target not found").showAndWait(); return; }
                    accountService.transfer(from, to, Double.parseDouble(amt.getText().trim()));
                    new Alert(Alert.AlertType.INFORMATION, "Transferred").showAndWait();
                    loadAccounts();
                } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
            }
        });
    }
}
