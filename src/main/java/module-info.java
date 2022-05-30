module com.example.contactsapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.contactsapp to javafx.fxml;
    exports com.example.contactsapp;

    exports com.example.contactsapp.datamodel;
    opens com.example.contactsapp.datamodel to javafx.fxml;

    exports com.example.contactsapp.controller;
    opens com.example.contactsapp.controller to javafx.fxml;
}