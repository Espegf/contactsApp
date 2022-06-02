package com.example.contactsapp.Exceptions;

public class ContactException extends Exception{
    private ErrorCode error;

    public ContactException(ErrorCode error, String message){
        super(message);
        this.error = error;
    }

    public String showMessage() {
        String m = error + " " + super.getMessage();
        return m;
    }
}
