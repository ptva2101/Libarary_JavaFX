package com.example.librarymanagementsystem2;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class UserLoginController extends LoginBaseController implements Initializable {
    @FXML
    private TextField su_phoneNumber;

    @FXML
    private TextField su_email;


    private String[] questionList = {"Who is your favorite teacher?", "What is your favorite food?", "What is your favorite leisure activity?"};

    private double x = 0;
    private double y = 0;

    public void LoginBtn() throws SQLException {
        if (si_username.getText().isEmpty() || si_password.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Message", "Incorrect Student's ID/Password");

        } else {
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                // truy van xem rs co ton tai khong
                String selctData = "SELECT msv, password FROM user WHERE msv = ? AND password = ?";
                pst = connect.prepareStatement(selctData);
                pst.setString(1,si_username.getText());
                pst.setString(2,si_password.getText());
                rs = pst.executeQuery();
                if(rs.next()) {
                    getData.username = si_username.getText();

                    si_loginBtn.getScene().getWindow().hide();

                    Parent root = FXMLLoader.load(getClass().getResource("userDashboard.fxml"));
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
                    showAlert(Alert.AlertType.ERROR, "Error Message", "Incorrect Student's ID/Password.");
                }

                connect.commit();
            } catch (Exception e) {
                connect.rollback();
                e.printStackTrace();
            }
        }
    }

    public void clearReg() {
        su_id.setText("");
        su_username.setText("");
        su_phoneNumber.setText("");
        su_email.setText("");
        su_password.setText("");
        su_question.getSelectionModel().clearSelection();
        su_answer.setText("");
    }

    public void regBtn() throws SQLException {
        if (su_id.getText().isEmpty()|| su_username.getText().isEmpty() || su_password.getText().isEmpty()
                || su_question.getSelectionModel().getSelectedItem() == null
                || su_answer.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Message", "Please fill all the blanks.");

        } else {
            String regData = "INSERT INTO user (msv ,name, phonenumber, email, password, question, answer, createdDate)" +
                    " VALUES(?,?,?,?,?,?,?,?)";
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                String checkUsername = "SELECT msv FROM user WHERE msv = " +
                        "'" + su_id.getText() + "'";
                pst = connect.prepareStatement(checkUsername);
                rs = pst.executeQuery();
                if (rs.next()) {
                    showAlert(Alert.AlertType.ERROR, "Error Message", "ID is already available");
                } else {
                    pst = connect.prepareStatement(regData);
                    pst.setString(1, su_id.getText());
                    pst.setString(2, su_username.getText());
                    pst.setString(3, su_phoneNumber.getText());
                    pst.setString(4, su_email.getText());
                    pst.setString(5, su_password.getText());
                    pst.setString(6, su_question.getSelectionModel().getSelectedItem().toString());
                    pst.setString(7, su_answer.getText());

                    Date date = new Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                    pst.setDate(8, sqlDate);

                    pst.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Information Massage", "Successfully registered");

                    clearReg();

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


    public void proceedBtn() throws SQLException {

        if (fp_username.getText().isEmpty() || fp_question.getSelectionModel().getSelectedItem() == null
                || fp_answer.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Message", "Please fill all blank fields");

        } else {
            connect = database.connectDB();
            connect.setAutoCommit(false);
            try {
                String selectData = "SELECT msv, question, answer FROM user WHERE msv = ? AND question = ? AND answer = ?";
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
                String updatePass = "UPDATE user SET password = ? WHERE msv = ?";
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

    public void initialize(URL url, ResourceBundle resourceBundle) {
        su_question.setItems(FXCollections.observableArrayList(questionList));

    }
}