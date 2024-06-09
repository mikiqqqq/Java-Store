module org.store {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires org.slf4j;
    requires ini4j;
    requires java.sql.rowset;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires javafx.swing;
    requires com.mailjet.api;
    requires org.json;
    requires jakarta.xml.bind;
    requires jakarta.xml.ws;

    opens org.oorsprong.websamples to com.sun.xml.ws, com.sun.xml.bind;
    exports org.store.enumeration;
    opens org.store to javafx.fxml;
    exports org.store.model;
    opens org.store.model to javafx.fxml;
    exports org.store;
}