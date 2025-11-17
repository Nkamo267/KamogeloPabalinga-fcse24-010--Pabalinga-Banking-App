package com.pabalinga.bankingsystem.ui;

import com.pabalinga.bankingsystem.model.Account;
import com.pabalinga.bankingsystem.model.Transaction;
import com.pabalinga.bankingsystem.service.TransactionService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class TransactionView extends Stage {
    public TransactionView(Account account) {
        setTitle("Transactions - " + account.getAccountNumber());
        initModality(Modality.APPLICATION_MODAL);

        ObservableList<String> txList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(txList);
        listView.setPrefWidth(380);
        TextArea details = new TextArea(); details.setEditable(false); details.setPrefWidth(380);

        try {
            List<Transaction> transactions = new TransactionService().getTransactionsForAccount(account.getId());
            for (Transaction t : transactions) {
                txList.add(t.getDate() + " | " + t.getType() + " | " + t.getAmount());
            }
            listView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    int idx = listView.getSelectionModel().getSelectedIndex();
                    Transaction selected = transactions.get(idx);
                    details.setText("Date: " + selected.getDate() + "\nType: " + selected.getType() + "\nAmount: " + selected.getAmount() + "\nDetails: " + selected.getDetails());
                }
            });
        } catch (Exception e) {
            details.setText("Error loading transactions: " + e.getMessage());
        }

        VBox left = new VBox(8, new Label("Transactions"), listView);
        left.setPadding(new Insets(10));
        VBox right = new VBox(8, new Label("Details"), details);
        right.setPadding(new Insets(10));
        HBox root = new HBox(10, left, right); root.setPadding(new Insets(10));
        setScene(new Scene(root, 820, 480));
    }
}
