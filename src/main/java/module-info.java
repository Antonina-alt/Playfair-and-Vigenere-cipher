module com.adashkevich.encryption.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;

    opens com.adashkevich.encryption.app to javafx.fxml;
    exports com.adashkevich.encryption.app;
}