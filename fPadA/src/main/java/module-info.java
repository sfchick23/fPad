module com.example.fpada {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;


    opens com.example.fpada to javafx.fxml;
    exports com.example.fpada;
}