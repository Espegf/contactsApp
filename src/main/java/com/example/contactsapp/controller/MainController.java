package com.example.contactsapp.controller;

import com.example.contactsapp.ContactException;
import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {
    @FXML
    private TableView<Contact> tableViewContacts;
    @FXML
    private BorderPane borderMain;
    @FXML
    private ContextMenu tableViewContactsMenu;
    @FXML
    private ToggleButton buttonStartWith;
    @FXML
    private TextField search;
    @FXML
    private FilteredList<Contact> filteredListContact;


    @FXML
    public void initialize(){

        //Lista filtrada
        filteredListContact = new FilteredList<Contact>(
                ContactSingleton.getInstance().getContacts(),
                (contact -> true));

        //Menu contextual
        tableViewContactsMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            Contact c = tableViewContacts.getSelectionModel().getSelectedItem();
            eraseContact(c);
        });
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setOnAction(event -> {
            Contact c = tableViewContacts.getSelectionModel().getSelectedItem();
            try {
                showDialogEditContact();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        tableViewContactsMenu.getItems().add(deleteMenuItem);
        tableViewContactsMenu.getItems().add(editMenuItem);

        tableViewContacts.setRowFactory(
                new Callback<TableView<Contact>, TableRow<Contact>>() {
                    @Override
                    public TableRow<Contact> call(TableView<Contact> tableView) {
                        final TableRow<Contact> name = new TableRow<>();

                        name.emptyProperty().addListener((obs, notEmpty, wasEmpty) ->{
                            if (wasEmpty){
                                name.setContextMenu(null);
                            }else{
                                name.setContextMenu(tableViewContactsMenu);
                            }
                        });

                        return name;

                        /*
                        name.contextMenuProperty().bind(
                                Bindings.when(name.emptyProperty())
                                        .then((ContextMenu) null)
                                        .otherwise(tableViewContactsMenu));
                        return name;
                        */


                    }
                });

        //Mostrar datos
        tableViewContacts.setItems(ContactSingleton.getInstance().getContacts());
        //Seleccionar primer contacto
        tableViewContacts.getSelectionModel().selectFirst();


    }

    @FXML
    public void showDialogNewContact() throws IOException {
        Dialog<ButtonType> d = new Dialog<>();
        d.initOwner(borderMain.getScene().getWindow());
        d.setResizable(true);
        d.setTitle("New contact");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/contactsapp/contact-dialog.fxml"));
        try {
            d.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        d.getDialogPane().getButtonTypes().add(ButtonType.OK);
        d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Optional<ButtonType> response = d.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            DialogController controller = loader.getController();
            Contact c = controller.processPressOkButton();
            tableViewContacts.getSelectionModel().select(c);

        }
    }


    @FXML
    public void showDialogEditContact() throws IOException {
        Contact c = tableViewContacts.getSelectionModel().getSelectedItem();

        if (c == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No task");
            alert.setHeaderText("Error");
            alert.setContentText("No task selected");
            alert.showAndWait();
            return;
        }else {
        Dialog<ButtonType> d = new Dialog<>();
        d.initOwner(borderMain.getScene().getWindow());
        d.setResizable(true);
        d.setTitle("Edit contact");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/contactsapp/contact-dialog.fxml"));
        try {
            d.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        d.getDialogPane().getButtonTypes().add(ButtonType.OK);
        d.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        DialogController contactController = loader.getController();
        contactController.loadContactData(c);

        Optional<ButtonType> response = d.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK) {
            DialogController controller = loader.getController();
            controller.editPressOkButton(c);
            tableViewContacts.refresh();
            tableViewContacts.getSelectionModel().select(c);
            }

        }
    }

    @FXML
    public void eraseContact(Contact c){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Erase a contact");
        alert.setHeaderText("Delete a contact");
        alert.setContentText("Are you sure?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == ButtonType.OK){
            ContactSingleton.getInstance().deleteContact(c);
        }
    }

    //Método de salida de la app en el menú
    @FXML
    public void exitApp(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setResizable(true);
        alert.setTitle("Close app");
        alert.setHeaderText("Exit");
        alert.setContentText("Are you sure?");
        Optional<ButtonType> respose = alert.showAndWait();
        if (respose.isPresent() && respose.get() == ButtonType.OK){
            Platform.exit();
        }
    }

    //5
    @FXML
    public void handledisplayContactStartWith(){
        Contact c = tableViewContacts.getSelectionModel().getSelectedItem();
        String s = search.getText();

            if (buttonStartWith.isSelected()){
                filteredListContact.setPredicate(contact -> contact.getName().toLowerCase().startsWith(s.toLowerCase()));
                if (filteredListContact.isEmpty()){
                    tableViewContacts.setItems(null);
                }else {
                    tableViewContacts.setItems(filteredListContact);
                    tableViewContacts.getSelectionModel().selectFirst();
                }
            }else {
                if (c == null) {
                    tableViewContacts.getSelectionModel().selectFirst();
                }
                filteredListContact.setPredicate(contact -> true);
                tableViewContacts.getSelectionModel().select(c);
            }

    }

}