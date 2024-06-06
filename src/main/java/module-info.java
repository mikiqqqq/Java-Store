module org.store {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.sun.jna;
    requires com.sun.jna.platform;


    opens org.store to javafx.fxml;
    exports org.store;
}