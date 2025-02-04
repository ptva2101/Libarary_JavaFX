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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginBaseController extends BaseController {
    @FXML
    protected TextField fp_answer;

    @FXML
    protected Button fp_back;

    @FXML
    protected Button fp_proceedBtn;

    @FXML
    protected ComboBox<?> fp_question;

    @FXML
    protected AnchorPane fp_questionForm;

    @FXML
    protected TextField fp_username;

    @FXML
    protected Button np_back;

    @FXML
    protected Button np_changePassBtn;

    @FXML
    protected PasswordField np_conirmPassword;

    @FXML
    protected AnchorPane np_newPassForm;

    @FXML
    protected PasswordField np_newPassword;

    @FXML
    protected Hyperlink si_forgotPass;

    @FXML
    protected Button si_loginBtn;

    @FXML
    protected AnchorPane si_loginForm;

    @FXML
    protected AnchorPane si_loginForm1;

    @FXML
    protected PasswordField si_password;

    @FXML
    protected TextField si_username;

    @FXML
    protected Button side_CreateBtn;

    @FXML
    protected Button side_alreadyHave;

    @FXML
    protected AnchorPane side_form;

    @FXML
    protected TextField su_answer;

    @FXML
    protected PasswordField su_password;

    @FXML
    protected ComboBox<String> su_question;

    @FXML
    protected Button su_signupBtn;

    @FXML
    protected Button returnBtn;

    @FXML
    protected TextField su_username;

    @FXML
    protected TextField su_id;

/*    protected Stage stage;
    protected Scene scene;
    protected Parent root;*/
    protected Connection connect;
    protected PreparedStatement pst;
    protected ResultSet rs;


    public void forgotPassQuestionList() {}

    public void regLquestionList() {}

    public void switchForgotPass() {
        fp_questionForm.setVisible(true);
        si_loginForm.setVisible(false);
        forgotPassQuestionList();
    }


    public void proceedBtn() throws SQLException {}
    public void changePassBtn() throws SQLException {}

    public void switchToLogin(ActionEvent event) throws IOException {}
    public void LoginBtn() throws SQLException {}
    public void regBtn() throws SQLException {}

    public void backToLoginForm() {
        si_loginForm.setVisible(true);
        fp_questionForm.setVisible(false);
    }

    public void backToQuestionForm() {
        fp_questionForm.setVisible(true);
        np_newPassForm.setVisible(false);
    }

    public void switchForm(ActionEvent event) {
        TranslateTransition slider = new TranslateTransition();
        if (event.getSource() == side_CreateBtn) {
            slider.setNode(side_form);
            slider.setToX(550);
            slider.setDuration(Duration.seconds(0.5));

            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(true);
                side_CreateBtn.setVisible(false);

                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);

                regLquestionList();
            });
            slider.play();
        } else if (event.getSource() == side_alreadyHave) {
            slider.setNode(side_form);
            slider.setToX(0);
            slider.setDuration(Duration.seconds(0.5));

            slider.setOnFinished((ActionEvent e) -> {
                side_alreadyHave.setVisible(false);
                side_CreateBtn.setVisible(true);

                fp_questionForm.setVisible(false);
                si_loginForm.setVisible(true);
                np_newPassForm.setVisible(false);
            });
            slider.play();
        }
    }

    public void switchToStart(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Start.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
