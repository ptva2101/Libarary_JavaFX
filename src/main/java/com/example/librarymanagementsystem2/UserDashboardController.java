package com.example.librarymanagementsystem2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.print.attribute.standard.Media;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserDashboardController extends DashboardBaseController implements Initializable {
    @FXML
    private ToggleButton audio;

    @FXML
    private ImageView audioImage;

    @FXML
    private Button close;

    @FXML
    private TextField dashBoard_email;

    @FXML
    private Label dashboard_ID;

    @FXML
    private Label dashboard_TBB;

    @FXML
    private Button dashboard_btn;

    @FXML
    private Button dashboard_deletebtn;

    @FXML
    private AnchorPane dashboard_form;

    @FXML
    private ImageView dashboard_image;

    @FXML
    private Button dashboard_importImage;

    @FXML
    private TextField dashboard_name;

    @FXML
    private PasswordField dashboard_password;

    @FXML
    private TextField dashboard_phoneNumber;

    @FXML
    private Button dashboard_updatebtn;

    @FXML
    private TableColumn<?, ?> library_authorcol;

    @FXML
    private Button library_btn;

    @FXML
    private AnchorPane library_form;

    @FXML
    private TableColumn<?, ?> library_genrecol;

    @FXML
    private TableColumn<?, ?> library_isbncol;

    @FXML
    private TableColumn<?, ?> library_pubDatecol;

    @FXML
    private TextField library_search;

    @FXML
    private TableView<bookData> library_table;

    @FXML
    private TableColumn<?, ?> library_titlecol;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button minimize;

    @FXML
    private Button myBorrowedBooks_btn;

    @FXML
    private TableColumn<?, ?> my_authorcol;

    @FXML
    private TableColumn<?, ?> my_bookTitlecol;

    @FXML
    private TableColumn<?, ?> my_genrecol;

    @FXML
    private TableColumn<?, ?> my_isbncol;

    @FXML
    private TableColumn<?, ?> my_pubDatecol;

    @FXML
    private TableView<ListIssue> my_table;

    @FXML
    private AnchorPane myborrow_form;

    @FXML
    private Label username;

    @FXML
    private TableColumn<?, ?> my_dueDatecol;

    @FXML
    private TableColumn<?, ?> my_issueDatecol;

    @FXML
    private TableColumn<?, ?> my_returnDatecol;

    @FXML
    private TextField my_search;

    @FXML
    private BarChart<String, Number> userChart;

    @FXML
    private AnchorPane userChartPane;

    @FXML
    private ToggleButton audioStatus;

    @FXML
    private Button nextAudio;
    @FXML
    private AnchorPane snowContainer;

    @FXML
    private TextField dashboard_answer;

    @FXML
    private ComboBox<String> dashboard_question;

    private Image image;

    private String id = getData.username;

    public void insertAva() throws MalformedURLException {
        FileChooser open = new FileChooser();
        open.setTitle("Open Image File");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*png", "*jpg", "*gif"));

        File file = open.showOpenDialog(main_form.getScene().getWindow());

        if (file != null) {
            getData.path = file.getAbsolutePath();
            image = new Image(file.toURL().toString(), 134, 152, false, true);

            dashboard_image.setImage(image);
        }
    }

    public void updateUser() throws SQLException {
        if (dashboard_name.getText().isEmpty() || dashboard_phoneNumber.getText().isEmpty()
                || dashBoard_email.getText().isEmpty() || dashboard_password.getText().isEmpty()
                || dashboard_question.getSelectionModel().getSelectedItem() == null || dashboard_answer.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Error Message", "Please fill all the empty fields");
        } else {
            connect = database.connectDB();
            connect.setAutoCommit(false); // Begin transaction

            try {
                // Confirm update action
                Optional<ButtonType> option = showChooseAlter(Alert.AlertType.INFORMATION, "Information Message",
                        "Are you sure you want to UPDATE your information?");
                if (option.isPresent() && option.get() == ButtonType.OK) {
                    // Update user details
                    String sqlUpdate = "UPDATE user SET name = ?, phonenumber = ?, email = ?, password = ?," +
                            "question = ?, answer = ? WHERE msv = ?";
                    pst = connect.prepareStatement(sqlUpdate);
                    pst.setString(1, dashboard_name.getText());
                    pst.setString(2, dashboard_phoneNumber.getText());
                    pst.setString(3, dashBoard_email.getText());
                    pst.setString(4, dashboard_password.getText());
                    pst.setString(5, dashboard_question.getSelectionModel().getSelectedItem());
                    pst.setString(6, dashboard_answer.getText());
                    pst.setString(7, dashboard_ID.getText());
                    pst.executeUpdate();

                    // Update image if provided
                    if (getData.path != null && !getData.path.isEmpty()) {
                        String sqlImage = "UPDATE user SET image = ? WHERE msv = ?";
                        pst = connect.prepareStatement(sqlImage);

                        String uri = getData.path;
                        uri = uri.replace("\\", "\\\\");
                        pst.setString(1, uri);
                        pst.setString(2, dashboard_ID.getText());
                        pst.executeUpdate();
                    }

                    connect.commit(); // Commit all updates
                    showAlert(Alert.AlertType.INFORMATION, "Information Message", "Successfully Updated!");
                }
            } catch (SQLException e) {
                connect.rollback(); // Rollback transaction in case of error
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error Message", "Update failed! Please try again.");
            } finally {
                connect.setAutoCommit(true); // Reset auto-commit mode
            }
        }
    }

    public void deleteUser() {
        connect = database.connectDB();
        try {
            String sqlcheck = "SELECT COUNT(*) FROM issue WHERE msv = ?" + "AND returnDate is null";
            pst = connect.prepareStatement(sqlcheck);
            pst.setString(1, dashboard_ID.getText());
            rs = pst.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.ERROR, "Error Message", "Cannot delete an account that is currently borrowing books.");
            } else {
                Optional<ButtonType> option = showChooseAlter(Alert.AlertType.INFORMATION, "Information Message", "Are you sure you want to delete your account?");

                if (option.get() == ButtonType.OK) {
                    stopMusic();
                    String sqlDelete = "DELETE FROM user WHERE msv = ?;";
                    pst = connect.prepareStatement(sqlDelete);
                    pst.setString(1, dashboard_ID.getText());
                    pst.executeUpdate();

                    showAlert(Alert.AlertType.INFORMATION, "Information Message", "Successfully Deleted!");

                    logout.getScene().getWindow().hide();
                    Parent root = FXMLLoader.load(getClass().getResource("userLogin.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<ListIssue> borrowedListData() throws SQLException {
        ObservableList<ListIssue> list = FXCollections.observableArrayList();
        connect = database.connectDB();
        connect.setAutoCommit(false);

        try {
            String sql = "SELECT i.*, b.* FROM issue i " +
                    "JOIN book b ON i.isbn = b.isbn " +
                    "WHERE i.msv = ?;";
            pst = connect.prepareStatement(sql);
            pst.setString(1, dashboard_ID.getText());
            rs = pst.executeQuery();

            ListIssue listIssueD;

            while (rs.next()) {
                listIssueD = new ListIssue(rs.getString("i.isbn"), rs.getString("b.title"), rs.getString("b.author"),
                        rs.getString("b.genre"), rs.getDate("b.pub_date"), rs.getDate("i.issueDate"),
                        rs.getDate("i.dueDate"), rs.getDate("i.returnDate"));
                list.add(listIssueD);
            }


        } catch (Exception e) {
            connect.rollback();
            e.printStackTrace();
        }
        return list;
    }

    public void myBorrowedBookSearch() {
        FilteredList<ListIssue> filter = new FilteredList<>(borrowedList, e -> true);

        my_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filter.setPredicate(predicateBorrowedBookD -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();
                if (predicateBorrowedBookD.getIsbn().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateBorrowedBookD.getTitle().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateBorrowedBookD.getAuthor().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateBorrowedBookD.getGenre().toLowerCase().contains(searchKey)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<ListIssue> sortedList = new SortedList<>(filter);
        sortedList.comparatorProperty().bind(my_table.comparatorProperty());
        my_table.setItems(sortedList);
    }

    private ObservableList<ListIssue> borrowedList;

    public void borrowedShowList() throws SQLException {
        borrowedList = borrowedListData();
        my_isbncol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        my_bookTitlecol.setCellValueFactory(new PropertyValueFactory<>("title"));
        my_authorcol.setCellValueFactory(new PropertyValueFactory<>("author"));
        my_genrecol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        my_pubDatecol.setCellValueFactory(new PropertyValueFactory<>("pub_date"));
        my_issueDatecol.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        my_returnDatecol.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        my_dueDatecol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        my_table.setItems(borrowedList);
    }

    public ObservableList<bookData> availableBooksListData() throws SQLException {

        ObservableList<bookData> listData = FXCollections.observableArrayList();
        connect = database.connectDB();
        connect.setAutoCommit(false);

        try {
            String sql = "SELECT * FROM book";
            pst = connect.prepareStatement(sql);
            rs = pst.executeQuery();

            bookData bookD;

            while (rs.next()) {
                bookD = new bookData(rs.getString("isbn"), rs.getString("title")
                        , rs.getString("author"), rs.getString("genre")
                        , rs.getString("image"), rs.getDate("pub_date")
                        , rs.getInt("quantity"), rs.getInt("remain"), rs.getInt("issued"));
                listData.add(bookD);
            }
            connect.commit();
        } catch (Exception e) {
            connect.rollback();
            e.printStackTrace();
        }
        return listData;
    }

    private ObservableList<bookData> availableBooksList;

    public void availableBooksShowListData() throws SQLException {
        availableBooksList = availableBooksListData(); //list book trong csdl
        library_isbncol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        library_titlecol.setCellValueFactory(new PropertyValueFactory<>("title"));
        library_authorcol.setCellValueFactory(new PropertyValueFactory<>("author"));
        library_pubDatecol.setCellValueFactory(new PropertyValueFactory<>("date"));
        library_genrecol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        library_table.setItems(availableBooksList);
    }

    public void availableBooksSearch() {

        FilteredList<bookData> filter = new FilteredList<>(availableBooksList, e -> true);

        library_search.textProperty().addListener((observable, oldValue, newValue) -> {

            filter.setPredicate(predicateBookData -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (predicateBookData.getIsbn().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateBookData.getTitle().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateBookData.getAuthor().toLowerCase().contains(searchKey)) {
                    return true;
                } else if (predicateBookData.getGenre().toLowerCase().contains(searchKey)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<bookData> sortList = new SortedList(filter);
        sortList.comparatorProperty().bind(library_table.comparatorProperty());
        library_table.setItems(sortList);
    }

    public void switchForm(ActionEvent event) throws SQLException {
        if (event.getSource() == dashboard_btn) {
            dashboard_form.setVisible(true);
            myborrow_form.setVisible(false);
            library_form.setVisible(false);

            dashboard_btn.setStyle("-fx-background-color: linear-gradient(to top right, #4c96a1, #bff4ff)");
            myBorrowedBooks_btn.setStyle("-fx-background-color: transparent");
            library_btn.setStyle("-fx-background-color: transparent");

            userBarChart();
        } else if (event.getSource() == myBorrowedBooks_btn) {
            dashboard_form.setVisible(false);
            myborrow_form.setVisible(true);
            library_form.setVisible(false);

            dashboard_btn.setStyle("-fx-background-color: transparent");
            myBorrowedBooks_btn.setStyle("-fx-background-color: linear-gradient(to top right, #4c96a1, #bff4ff)");
            library_btn.setStyle("-fx-background-color: transparent");

            borrowedShowList();
        } else if (event.getSource() == library_btn) {
            dashboard_form.setVisible(false);
            myborrow_form.setVisible(false);
            library_form.setVisible(true);

            dashboard_btn.setStyle("-fx-background-color: transparent");
            myBorrowedBooks_btn.setStyle("-fx-background-color: transparent");
            library_btn.setStyle("-fx-background-color: linear-gradient(to top right, #4c96a1, #bff4ff)");

            availableBooksShowListData();
        }

    }

    public void close() {
        System.exit(0);
    }

    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    public void logout() {
        try {
            Optional<ButtonType> option = showChooseAlter(Alert.AlertType.INFORMATION, "Information message", "Are you sure you want to logout?");

            if (option.get().equals(ButtonType.OK)) {
                stopMusic();
                logout.getScene().getWindow().hide();

                Parent root = FXMLLoader.load(getClass().getResource("userLogin.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayUsername() throws SQLException {
        connect = database.connectDB();
        connect.setAutoCommit(false);
        String sqlName = "SELECT * FROM user WHERE msv = " + "'" + id + "'";
        pst = connect.prepareStatement(sqlName);
        rs = pst.executeQuery();
        rs.next();

        dashboard_ID.setText(rs.getString("msv"));
        dashboard_name.setText(rs.getString("name"));
        dashboard_phoneNumber.setText(rs.getString("phonenumber"));
        dashBoard_email.setText(rs.getString("email"));
        dashboard_password.setText(rs.getString("password"));
        dashboard_question.setValue(rs.getString("question"));
        dashboard_answer.setText(rs.getString("answer"));
        username.setWrapText(true);
        username.setText(rs.getString("name"));

        String sqlTtB = "SELECT COUNT(*) FROM issue WHERE msv = " + "'" + id + "'";
        pst = connect.prepareStatement(sqlTtB);
        rs = pst.executeQuery();
        rs.next();

        dashboard_TBB.setText(String.valueOf(rs.getInt("COUNT(*)")));
    }

    public void getAva() throws SQLException {
        connect = database.connectDB();
        connect.setAutoCommit(false);
        String sqlName = "SELECT image FROM user WHERE msv = " + "'" + id + "'";
        pst = connect.prepareStatement(sqlName);
        rs = pst.executeQuery();
        rs.next();

        getData.path = rs.getString("image");

        String uri = "file:" + rs.getString("image");
        image = new Image(uri, 134, 152, false, true);
        dashboard_image.setImage(image);
    }


    public int getBorrowData(int m) throws SQLException {
        connect = database.connectDB();
        connect.setAutoCommit(false);
        int borrowBook = 0;
        String sql = "SELECT COUNT(*) FROM issue WHERE msv = " + "'" + id + "'" +
                "AND MONTH(issueDate) = " + "'" + m + "'" + "AND YEAR(issueDate) = YEAR(NOW());";
        pst = connect.prepareStatement(sql);
        rs = pst.executeQuery();
        rs.next();

        borrowBook = rs.getInt("COUNT(*)");
        return borrowBook;
    }

    public int getReturnData(int m) throws SQLException {
        connect = database.connectDB();
        connect.setAutoCommit(false);
        int returnBook = 0;
        String sql = "SELECT COUNT(*) FROM issue WHERE msv = " + "'" + id + "'" +
                "AND MONTH(returnDate) = " + "'" + m + "'" + "AND YEAR(returnDate) = YEAR(NOW());";
        pst = connect.prepareStatement(sql);
        rs = pst.executeQuery();
        rs.next();

        returnBook = rs.getInt("COUNT(*)");
        return returnBook;
    }

    public void userBarChart() throws SQLException {
        userChart.getData().clear();

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Borrowed Books");

        for (int i = 0; i < months.length; i++) {
            int bookCount = getBorrowData(i + 1); // Fetch user count for the month
            series1.getData().add(new XYChart.Data<>(months[i], bookCount));
        }

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("Returned Books");

        for (int i = 0; i < months.length; i++) {
            int bookCount = getReturnData(i + 1); // Fetch user count for the month
            series2.getData().add(new XYChart.Data<>(months[i], bookCount));
        }

        userChart.getData().addAll(series1, series2);
    }

    private String[] musicList = {
            "/com/example/librarymanagementsystem2/music/chrismas.wav",
            "/com/example/librarymanagementsystem2/music/LikeADream.wav",
            "/com/example/librarymanagementsystem2/music/GOTTheme.wav",
            "/com/example/librarymanagementsystem2/music/digiuatroirucro.wav"
    };
    private int currentTrackIndex = 0; // To keep track of the current song
    private Clip clip;

    public void setAudio() {
        try {
            playMusic(currentTrackIndex); // Start playing the first track

            // Load image resources
            URL onURL = getClass().getResource("/com/example/librarymanagementsystem2/pictureStyle/AudioOn.png");
            URL offURL = getClass().getResource("/com/example/librarymanagementsystem2/pictureStyle/AudioOff1.png");
            if (onURL == null || offURL == null) {
                throw new IllegalArgumentException("One or more image files not found!");
            }

            Image image1 = new Image(onURL.toExternalForm());
            Image image2 = new Image(offURL.toExternalForm());
            audioImage.setImage(image1); // Default image

            // Toggle button behavior
            audio.setSelected(false); // Default state
            audio.setOnAction(event -> {
                if (audio.isSelected()) {
                    audioImage.setImage(image2);
                    if (clip != null) stopMusic(); // Turn off audio
                } else {
                    audioImage.setImage(image1);
                    if (clip != null) {
                        clip.loop(Clip.LOOP_CONTINUOUSLY); // Resume infinite play
                        clip.start();
                    }
                }
            });

            // Handle "Next" button to switch songs
            nextAudio.setOnAction(event -> {
                if (clip != null) {
                    clip.stop();
                    clip.close();
                }
                currentTrackIndex = (currentTrackIndex + 1) % musicList.length; // Cycle through the playlist
                playMusic(currentTrackIndex);
            });

            // Handle resource cleanup
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (clip != null) clip.close();
            }));
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally display an error to the user in the UI
        }
    }

    private void playMusic(int trackIndex) {
        try {
            String trackPath = musicList[trackIndex];
            URL musicURL = getClass().getResource(trackPath);
            if (musicURL == null) {
                throw new IllegalArgumentException("Music file not found: " + trackPath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicURL);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Infinite loop
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            // Optionally handle errors like showing a message in the UI
        }
    }

    public void stopMusic() {
        clip.stop();
    }
    private String[] questionList = {"Who is your favorite teacher?", "What is your favorite food?", "What is your favorite leisure activity?"};

    public void initialize(URL url, ResourceBundle resourceBundle) {
        dashboard_question.setItems(FXCollections.observableArrayList(questionList));

        SnowEffect snowEffect = new SnowEffect(snowContainer, 10);
        snowEffect.startSnowfall();
        try {
            setAudio();
            userBarChart();
            getAva();
            displayUsername();
            availableBooksShowListData();
            borrowedShowList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
