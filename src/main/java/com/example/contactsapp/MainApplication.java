package com.example.contactsapp;

import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
       FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("contact-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Contact List");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, (evento) -> {
            switch (evento.getCode().getCode()) {
                case 122: { // 122 = f11
                    stage.setFullScreen(true);
                    break;
                }
                case 127: { // 127 = suprimir
                    stage.setFullScreen(false);
                    break;
                }
                default: {
                    System.out.println("Unrecognized key");
                }
            }
        });
        stage.show();
    }

    @Override
    public void init() throws Exception {
        try{
            ContactSingleton.getInstance().loadContacts();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        try{
            ContactSingleton.getInstance().saveContacts();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}