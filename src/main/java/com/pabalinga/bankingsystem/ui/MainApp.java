package com.pabalinga.bankingsystem.ui;

import com.pabalinga.bankingsystem.db.DB;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        DB.init(); // create tables + default admin if needed
        new LoginUI().start(primaryStage);
    }
    public static void main(String[] args) { launch(args); }
}
