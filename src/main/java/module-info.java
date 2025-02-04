module com.example.librarymanagementsystem2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires transitive fontawesomefx;
    requires java.desktop;
    requires com.google.gson;
    requires java.sql;
    requires java.net.http;
    requires org.json;
    requires okhttp3;
    requires org.apache.httpcomponents.httpcore;

    opens com.example.librarymanagementsystem2 to javafx.fxml;
    exports com.example.librarymanagementsystem2;
}