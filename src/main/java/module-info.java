module org.store {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.store to javafx.fxml;
    exports org.store;
}