module org.store {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.slf4j;


    opens org.store to javafx.fxml;
    exports org.store;
    exports org.store.model;
    opens org.store.model to javafx.fxml;
}