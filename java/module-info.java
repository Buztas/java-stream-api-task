module com.example._9_laboras {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example._9_laboras to javafx.fxml;
    exports com.example._9_laboras;
}