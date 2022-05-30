package com.example.contactsapp.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.IllegalFormatCodePointException;
import java.util.Iterator;
import java.util.List;

public class ContactSingleton {
    private static ContactSingleton instance = new ContactSingleton();
    private static String fileName ="Contact.txt";
    private ObservableList<Contact> contacts;

    private ContactSingleton(){}

    public static ContactSingleton getInstance() {
        return instance;
    }

    public ObservableList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ObservableList<Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact c){
        contacts.add(c);
    }

    public void deleteContact(Contact c){
        contacts.remove(c);
    }

    public void editContact(Contact old, Contact n){
        old.setName(n.getName());
        old.setSurname(n.getSurname());
        old.setPhone(n.getPhone());
        old.setComment(n.getComment());
    }

    public void saveContacts() throws IOException {
        Path path = Paths.get(fileName);
        BufferedWriter br = Files.newBufferedWriter(path);

        try {
            Iterator<Contact> it = contacts.iterator();
            while (it.hasNext()) {
                Contact c = it.next();

                StringBuilder sb = new StringBuilder(c.getName());
                sb.append("\t");
                sb.append(c.getSurname());
                sb.append("\t");
                sb.append(c.getPhone());
                sb.append("\t");
                if (c.getComment().isEmpty()){
                    sb.append(" ");
                }else{
                    sb.append(c.getComment());
                }
                br.write(sb.toString());
                br.newLine();
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void loadContacts() throws IOException {
        contacts = FXCollections.observableArrayList();

        Path path = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(path);

        String line;
        try {
            line = br.readLine();
            while (line != null) {
                String[] contactPiece = line.split("\t");
                String name = contactPiece[0];
                String surname = contactPiece[1];
                String phone = contactPiece[2];
                String comment = contactPiece[3];
                Contact c = new Contact(name, surname, phone, comment);
                contacts.add(c);
                line = br.readLine();
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

}
