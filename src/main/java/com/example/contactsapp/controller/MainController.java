package com.example.contactsapp.controller;

import com.example.contactsapp.Exceptions.ContactException;
import com.example.contactsapp.Exceptions.ErrorCode;
import com.example.contactsapp.datamodel.Contact;
import com.example.contactsapp.datamodel.ContactSingleton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;

public class MainController {
    private ButtonType acceptButton = new ButtonType("Accept",ButtonBar.ButtonData.OK_DONE);
    private ButtonType cancelButton = new ButtonType("Cancel",ButtonBar.ButtonData.CANCEL_CLOSE);
    private ButtonType closeButton = new ButtonType("Close",ButtonBar.ButtonData.CANCEL_CLOSE);


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
    private TableColumn<Contact,String> name;


    @FXML
    public void initialize(){

        //Filtered list
        filteredListContact = new FilteredList<Contact>(
                ContactSingleton.getInstance().getContacts(),
                (contact -> true));

        //Sort
        Comparator<String> columnComparator =
                Comparator.comparing(String::toLowerCase);
        name.setComparator(columnComparator);

        //Context menu
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
            } catch (ContactException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().getStylesheets().add("Style.css");
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(e.showMessage());
                alert.showAndWait();
                return;
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
    public void showDialogNewContact() throws ContactException {
        Dialog<ButtonType> d = new Dialog<>();
        d.initOwner(borderMain.getScene().getWindow());
        d.setResizable(true);
        d.setTitle("New contact");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/contactsapp/contact-dialog.fxml"));
        try {
            d.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            throw new ContactException(ErrorCode.DIALOG_ERROR,"Not new dialog");
        }
        DialogController contactController = loader.getController();

        d.getDialogPane().getButtonTypes().add(acceptButton);
        d.getDialogPane().getButtonTypes().add(cancelButton);

        d.getDialogPane().lookupButton(acceptButton).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> contactController.getName().getText().isEmpty() ||
                                contactController.getSurname().getText().isEmpty() ||
                                contactController.getPhone().getText().isEmpty(),
                        contactController.getName().textProperty(),
                        contactController.getSurname().textProperty(),
                        contactController.getPhone().textProperty()
                ));

        Optional<ButtonType> response = d.showAndWait();
        if (response.isPresent() && response.get() == acceptButton) {
            DialogController controller = loader.getController();
            Contact c = controller.processPressOkButton();
            tableViewContacts.getSelectionModel().select(c);

        }
    }

    @FXML
    public void showDialogEditContact() throws ContactException {
        Contact c = tableViewContacts.getSelectionModel().getSelectedItem();

        if (c == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().getStylesheets().add("Style.css");
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
           throw new ContactException(ErrorCode.DIALOG_ERROR,"Not edit dialog");

        }
        d.getDialogPane().getButtonTypes().add(acceptButton);
        d.getDialogPane().getButtonTypes().add(cancelButton);

        DialogController contactController = loader.getController();
        contactController.loadContactData(c);

        d.getDialogPane().lookupButton(acceptButton).disableProperty()
                .bind(Bindings.createBooleanBinding(
                        () -> contactController.getName().getText().isEmpty() ||
                                contactController.getSurname().getText().isEmpty() ||
                                contactController.getPhone().getText().isEmpty(),
                        contactController.getName().textProperty(),
                        contactController.getSurname().textProperty(),
                        contactController.getPhone().textProperty()
                ));

        Optional<ButtonType> response = d.showAndWait();
        if (response.isPresent() && response.get() == acceptButton) {
            DialogController controller = loader.getController();
            controller.editPressOkButton(c);
            tableViewContacts.refresh();
            tableViewContacts.getSelectionModel().select(c);
            }

        }
    }

    @FXML
    public void onDoubleClickShowMore(MouseEvent event) throws ContactException {

        Contact c = tableViewContacts.getSelectionModel().getSelectedItem();
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount()==2){
                if (c == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.getDialogPane().getStylesheets().add("Style.css");
                    alert.setTitle("No task");
                    alert.setHeaderText("Error");
                    alert.setContentText("No task selected");
                    alert.showAndWait();
                    return;
                }else {
                    Dialog<ButtonType> d = new Dialog<>();
                    d.initOwner(borderMain.getScene().getWindow());
                    d.setResizable(true);
                    d.setTitle("Contact propierties");

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/com/example/contactsapp/contact-dialog-see.fxml"));
                    try {
                        d.getDialogPane().setContent(loader.load());
                    } catch (IOException e) {
                        throw new ContactException(ErrorCode.DIALOG_ERROR,"No information");

                    }
                    d.getDialogPane().getButtonTypes().add(closeButton);

                    DialogController contactController = loader.getController();
                    contactController.loadContactData(c);

                    Optional<ButtonType> response = d.showAndWait();
                    if (response.isPresent() && response.get() == closeButton) {
                        tableViewContacts.getSelectionModel().select(c);
                    }
                }
            }
        }
    }

    @FXML
    public void eraseContact(Contact c){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add("Style.css");
        alert.setTitle("Erase a contact");
        alert.setHeaderText("Delete a contact");
        alert.setContentText("Are you sure?");
        alert.getButtonTypes().set(0,acceptButton);
        alert.getButtonTypes().set(1,cancelButton);


        Optional<ButtonType> response = alert.showAndWait();
        if (response.isPresent() && response.get() == acceptButton){
            ContactSingleton.getInstance().deleteContact(c);
        }
    }

    @FXML
    public void eraseWContact(){
        Contact c = tableViewContacts.getSelectionModel().getSelectedItem();
        eraseContact(c);
    }

    //Método de salida de la app en el menú
    @FXML
    public void exitApp(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getStylesheets().add("Style.css");
        alert.setResizable(true);
        alert.setTitle("Close app");
        alert.setHeaderText("Exit");
        alert.setContentText("Are you sure?");
        alert.getButtonTypes().set(0,acceptButton);
        alert.getButtonTypes().set(1,cancelButton);

        Optional<ButtonType> respose = alert.showAndWait();
        if (respose.isPresent() && respose.get() == acceptButton){
            Platform.exit();
        }
    }

    //5
    @FXML
    public void handleDisplayContactStartWith(){
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

    public void help(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().getStylesheets().add("Style.css");
        alert.setResizable(true);
        alert.setTitle("Help");
        alert.setHeaderText("Exit");
        alert.setContentText("Press F11 for full screen \nPress ESC or SUPPRESS to exit of full screen");
        alert.getButtonTypes().set(0,closeButton);

        Optional<ButtonType> respose = alert.showAndWait();
        if (respose.isPresent() && respose.get() == closeButton){
        }
    }

}