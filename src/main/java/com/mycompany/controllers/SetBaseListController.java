package com.mycompany.controllers;

import com.mycompany.domain.SetBase;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SetBaseListController {
    
    @FXML private ListView<SetBase> setBaseList;
    @FXML private Button editButton;
    @FXML private Button removeButton;

    public void initialize() {
        // element to Observable[] convertor. Observable objects are listened
        // for changes on the element
        Callback<SetBase, Observable[]> extractor = (SetBase setBase) -> new Observable[] {
            setBase.workingSetsProperty(),
            setBase.repetitionsProperty(),
            setBase.workingWeightProperty()
        };
        
        // list created so it fires updates if workingSets, workingRepetitions
        // or workingWeight changes:
        ObservableList<SetBase> setBases = FXCollections.observableArrayList(extractor);

        setBases.addAll(
                new SetBase(1, 15, 20),
                new SetBase(1, 10, 40),
                new SetBase(1, 8, 60)
        );
        
        // ObservableList is automatically observed by the ListView, such
        // that any changes that occur inside the ObservableList will be
        // automatically shown in the ListView itself
        setBaseList.setItems(setBases);

        // edit and remove buttons are disabled if an item is not selected in the listview
        editButton.disableProperty().bind(
                Bindings.isNull(setBaseList.getSelectionModel().selectedItemProperty()));
        
        removeButton.disableProperty().bind(
                Bindings.isNull(setBaseList.getSelectionModel().selectedItemProperty()));
    }

    @FXML
    private void newSetBase() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SetBaseEditor.fxml"));
        Parent root = loader.load();

        SetBaseEditorController controller = loader.getController();
        
        controller.setBaseProperty().addListener((obs, oldSetBase, newSetBase) -> {
            if (newSetBase != null) {
                setBaseList.getItems().add(newSetBase);
            }
        });

        showEditorWindow(root);
    }


    @FXML
    private void editSetBase() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SetBaseEditor.fxml"));
        Parent root = loader.load();

        SetBaseEditorController controller = loader.getController();

        SetBase selectedItem = setBaseList.getSelectionModel().getSelectedItem();
        controller.setSetBase(selectedItem);

        showEditorWindow(root);
    }
    
    @FXML
    private void removeSetBase() {
        // remove button is active only if selected index is valid
        final int selectedIdx = setBaseList.getSelectionModel().getSelectedIndex();
        
        int newSelectedIndex =
            (selectedIdx == setBaseList.getItems().size() - 1)
                ? selectedIdx - 1
                : selectedIdx;
        
        setBaseList.getItems().remove(selectedIdx);
        
        if (newSelectedIndex >= 0) {
            setBaseList.getSelectionModel().select(newSelectedIndex);
        } else {
            setBaseList.getSelectionModel().clearSelection();
        }
    }

    private void showEditorWindow(Parent root) {
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initOwner(setBaseList.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.setScene(scene);
        stage.showAndWait();
    }

}