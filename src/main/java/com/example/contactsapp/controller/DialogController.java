package com.example.contactsapp.controller;

import com.example.contactsapp.Exceptions.ContactException;
import com.example.contactsapp.Exceptions.ErrorCode;
import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DialogController {
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField phone;
    @FXML
    private TextArea comment;

    public TextField getName() {
        return name;
    }

    public TextField getSurname() {
        return surname;
    }

    public TextField getPhone() {
        return phone;
    }

    public Contact processPressOkButton() throws ContactException {
        String c = comment.getText();
        Contact contact;
        if (name.getText().isEmpty() || surname.getText().isEmpty() || phone.getText().isEmpty()){
            throw new ContactException(ErrorCode.INFO_REMAINING, "Missing information");
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

    public void editPressOkButton(Contact c) throws ContactException{
        if (name.getText().isEmpty() || surname.getText().isEmpty() || phone.getText().isEmpty()){
            throw new ContactException(ErrorCode.INFO_REMAINING,"Missing information");
        }
        c.setName(name.getText());
        c.setSurname(surname.getText());
        c.setPhone(phone.getText());
        c.setComment(comment.getText());
    }

    public void loadContactData(Contact c){
        name.setText(c.getName());
        surname.setText(c.getSurname());
        phone.setText(c.getPhone());
        comment.setText(c.getComment());
    }

}
