package com.example.librarymanagementsystem2;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminLoginController extends LoginBaseController {

    private double x = 0;
    private double y = 0;

    public void switchToLogin(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("login-view.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void LoginBtn() throws SQLException {
        if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Incorrect Username/Password.");
        } else {
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                // truy van xem rs co ton tai khong
                String selctData = "SELECT username, password FROM admin WHERE username = ? AND password = ?";
                pst = connect.prepareStatement(selctData);
                pst.setString(1,si_username.getText());
                pst.setString(2,si_password.getText());
                rs = pst.executeQuery();
                if(rs.next()) {
                    getData.username = si_username.getText();
                    // ẩn đi loginForm
                    si_loginBtn.getScene().getWindow().hide();
                    // mở ra của sổ DASHBOARD
                    Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    root.setOnMousePressed((MouseEvent event) -> {
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    root.setOnMouseDragged((MouseEvent event) -> {
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT); // stage trong suốt

                    stage.setScene(scene);
                    stage.show();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error Message", "Incorrect Username/Password.");
                }

                connect.commit();
            } catch (Exception e) {
                connect.rollback();
                e.printStackTrace();
            }
        }
    }

    public void regBtn() throws SQLException { // không dùng tham số event thì không cần ActionEvent
        if (su_username.getText().isEmpty() || su_password.getText().isEmpty()
        || su_question.getSelectionModel().getSelectedItem() == null
        || su_answer.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error Message", "Please fill all the blanks.");
        } else {
            String regData = "INSERT INTO admin (username, password, question, answer, date)" +
                    " VALUES(?,?,?,?,?)";
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                // KIEM TRA XEM USERNAME TON TAI CHUA
                String checkUsername = "SELECT username FROM admin WHERE username = " +
                        "'" + su_username.getText() + "'";
                pst = connect.prepareStatement(checkUsername);
                rs = pst.executeQuery();
                if (rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Error Message", "Username is already taken");
                } else {
                    pst = connect.prepareStatement(regData);
                    pst.setString(1, su_username.getText());
                    pst.setString(2, su_password.getText());
                    pst.setString(3, su_question.getSelectionModel().getSelectedItem().toString());
                    pst.setString(4, su_answer.getText());

                    Date date = new Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    pst.setDate(5, sqlDate);
                    pst.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Information Massage", "Successfully registered");

                    su_username.setText("");
                    su_password.setText("");
                    su_question.getSelectionModel().clearSelection();
                    su_answer.setText("");

                    TranslateTransition slider = new TranslateTransition();
                    slider.setNode(side_form);
                    slider.setToX(0);
                    slider.setDuration(Duration.seconds(0.5));

                    slider.setOnFinished((ActionEvent e) -> {
                        side_alreadyHave.setVisible(false);
                        side_CreateBtn.setVisible(true);
                    });
                    slider.play();
                }

                // đúng hết thì commit
                connect.commit();
            } catch (Exception e) {
                connect.rollback();
                e.printStackTrace();
            }
        }
    }

    private String[] questionList = {"What is your favorite Color?", "What is your favorite food?", "What is your birth date?"};
    public void regLquestionList() {
        List<String> listQ = new ArrayList<String>();
        for (String data: questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        su_question.setItems(listData);
    }

    public void proceedBtn() throws SQLException {

        if (fp_username.getText().isEmpty() || fp_question.getSelectionModel().getSelectedItem() == null
        || fp_answer.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Message", "Please fill all blank fields");

        } else {
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                String selectData = "SELECT username, question, answer FROM admin WHERE username = ? AND question = ? AND answer = ?";
                pst = connect.prepareStatement(selectData);
                pst.setString(1, fp_username.getText());
                pst.setString(2, fp_question.getSelectionModel().getSelectedItem().toString());
                pst.setString(3, fp_answer.getText());

                rs = pst.executeQuery();

                if(rs.next()) {
                    np_newPassForm.setVisible(true);
                    fp_questionForm.setVisible(false);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error Massage", "Incorrect Information");
                }

                connect.commit();
            } catch (Exception e) {
                connect.rollback();
                e.printStackTrace();
            }
        }

    }

    public void changePassBtn() throws SQLException {

        if (np_newPassword.getText().isEmpty() || np_conirmPassword.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Massage", "Incorrect Information");

        } else if (!np_newPassword.getText().equals(np_conirmPassword.getText())){

            showAlert(Alert.AlertType.ERROR, "Error Massage", "Passwords do not match");

        } else {
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                String updatePass = "UPDATE admin SET password = ? WHERE username = ?";
                pst = connect.prepareStatement(updatePass);
                pst.setString(1,np_newPassword.getText());
                pst.setString(2,fp_username.getText());
                pst.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Information Massage", "Successfully changed Password!");

                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);

                // thay đổi mật khẩu xong thì làm trống
                np_newPassword.setText("");
                np_conirmPassword.setText("");
                fp_username.setText("");
                fp_question.getSelectionModel().clearSelection();
                fp_answer.setText("");

                connect.commit();
            } catch(Exception e) {
                connect.rollback();
                e.printStackTrace();
            }
        }

    }

    public void forgotPassQuestionList() {
        List<String> listQ = new ArrayList<>();

        for (String data: questionList) {
            listQ.add(data);
        }

        ObservableList listData = FXCollections.observableArrayList(listQ);
        fp_question.setItems(listData);
    }

}