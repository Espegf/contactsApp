package com.example.contactsapp.controller;

import com.example.contactsapp.ContactException;
import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class DialogController {
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField phone;
    @FXML
    private TextArea comment;

    public Contact processPressOkButton() throws IOException {
        String c = comment.getText();
        Contact contact;
        if (name.getText().isEmpty() || surname.getText().isEmpty() || phone.getText().isEmpty()){
            throw new IOException();
        }
        if (c.isEmpty()){
            contact = new Contact(name.getText(),surname.getText(),
                        phone.getText());

        }else{
            contact = new Contact(name.getText(),surname.getText(),
                    phone.getText(), comment.getText());

        }
        ContactSingleton.getInstance().addContact(contact);
        return contact;
    }

    public Contact editPressOkButton(Contact c) throws IOException {
        if (name.getText().isEmpty() || surname.getText().isEmpty() || phone.getText().isEmpty()){
            throw new IOException("Faltan datos");
        }
        Contact contact = new Contact(name.getText(), surname.getText(), phone.getText(), comment.getText());
        ContactSingleton.getInstance().editContact(c, contact);
        return c;
    }

    public void loadContactData(Contact c){
        name.setText(c.getName());
        surname.setText(c.getSurname());
        phone.setText(c.getPhone());
        comment.setText(c.getComment());
    }

}
