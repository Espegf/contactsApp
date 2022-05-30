package com.example.contactsapp.datamodel;

public class Contact {
    private String name;
    private String surname;
    private String phone;
    private String comment;

    public Contact(String name, String surname, String phone, String comment) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.comment = comment;
    }

    public Contact(String name, String surname, String phone) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        comment = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
