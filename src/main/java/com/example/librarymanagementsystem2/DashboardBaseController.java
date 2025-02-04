package com.example.librarymanagementsystem2;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardBaseController extends BaseController {
    protected Connection connect;
    protected Connection connect1;
    protected PreparedStatement pst;
    protected PreparedStatement pst1;
    protected ResultSet rs;
    protected ResultSet rs1;

    protected final static String jan = "January";
    protected final static String feb = "February";
    protected final static String mar = "March";
    protected final static String apr = "April";
    protected final static String may = "May";
    protected final static String jun = "June";
    protected final static String jul = "July";
    protected final static String aug = "August";
    protected final static String sept = "September";
    protected final static String oct = "October";
    protected final static String nov = "November";
    protected final static String dec = "December";

    String[] months = {jan, feb, mar, apr, may, jun, jul, aug, sept, oct, nov, dec};


    public void switchForm(ActionEvent event) throws SQLException{}
    public void displayUsername() throws SQLException{}
    public void close(){}
    public void minimize(){}
    public void logout(){}
    public void openSnakeBotWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatBot.fxml"));
            StackPane snakeBotRoot = loader.load();
            Stage newStage = new Stage();
            newStage.setTitle("SnakeBot");
            Scene newScene = new Scene(snakeBotRoot);
            newStage.setScene(newScene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
