package com.example.contactsapp;

public class ContactException extends Exception{

    public ContactException(String message){
    }

    public static void showMessage(ErrorCode error, String message) {
        System.out.println(error + " " + message);
    }
}
