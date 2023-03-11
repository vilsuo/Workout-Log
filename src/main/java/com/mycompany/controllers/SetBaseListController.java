package com.mycompany.controllers;

import com.mycompany.domain.Exercise;
import com.mycompany.domain.SetBase;
import com.mycompany.cells.SetBaseCell;
import java.util.Optional;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SetBaseListController {
    
    @FXML private Label exerciseNameLbl;
    
    @FXML private ListView<SetBase> setBaseList;
    
    @FXML private Button saveButton;
    @FXML private Button editButton;
    @FXML private Button removeButton;
    
    private final ButtonType yesButton = new ButtonType("Yes");
    private final ButtonType noButton = new ButtonType("No");
    private final ButtonType backButton = new ButtonType("Back");
    
    private final ObjectProperty<Exercise> exerciseBase =
        new SimpleObjectProperty<>();
    
    public ObjectProperty<Exercise> exerciseBaseProperty() {
        return exerciseBase;
    }
    
    public final void setExerciseBase(Exercise exerciseBase) {
        exerciseBaseProperty().set(exerciseBase);
    }
    
    private BooleanProperty changeMadeProperty =
        new SimpleBooleanProperty(false);
    
    public void initialize() {
        setBaseList.setCellFactory(param -> new SetBaseCell());
         
        exerciseBase.addListener((obs, oldExerciseBase, newExerciseBase) -> {
            if (newExerciseBase != null) {
                
                // list created so it fires updates if SetBase changes:
                Callback<SetBase, Observable[]> extractor =
                    (SetBase setBase) -> new Observable[] {
                        setBase.workingSetsProperty(),
                        setBase.repetitionsProperty(),
                        setBase.workingWeightProperty()
                    };
                
                ObservableList<SetBase> copiedSetBases =
                    FXCollections.observableArrayList(extractor);
                
                for (SetBase setBase : newExerciseBase.getSets()) {
                    SetBase copiedSetBase = new SetBase(
                        setBase.getWorkingSets(),
                        setBase.getRepetitions(),
                        setBase.getWorkingWeight()
                    );
                    copiedSetBases.add(copiedSetBase);
                }
                
                copiedSetBases.addListener(new ListChangeListener() {

                    @Override
                    public void onChanged(ListChangeListener.Change change) {
                        changeMadeProperty.set(true);
                    }
                });
                
                setBaseList.setItems(copiedSetBases);
                exerciseNameLbl.setText(newExerciseBase.getExerciseName());
            }
        });
        
        // saving is disabled if no changes has been made
        saveButton.disableProperty().bind(Bindings.not(changeMadeProperty));
        
        // editing is disabled if no items are selected
        editButton.disableProperty().bind(
            Bindings.isNull(
                setBaseList.getSelectionModel().selectedItemProperty()
            )
        );
        
        // removing is disabled if no items are selected
        removeButton.disableProperty().bind(
            Bindings.isNull(
                setBaseList.getSelectionModel().selectedItemProperty()
            )
        );
    }
    
    @FXML
    private void save() throws Exception {
        Optional<ButtonType> optional = showSaveAlert();
        if (optional.get() == yesButton) {
            exerciseBase.get().getSets().setAll(setBaseList.getItems());
            close();
            
        } else if (optional.get() == noButton) {
            close();
        }
    }
    
    @FXML
    private void cancel() throws Exception {
        if (changeMadeProperty.get()) {
            Optional<ButtonType> optional = showCancelAlert();
            if (optional.get() == yesButton) {
                close();
            }
            
        } else {
            close();
        }
    }

    @FXML
    private void createNew() throws Exception {
        String resource = "/fxml/SetBaseEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();

        SetBaseEditorController controller = loader.getController();
        
        controller.setBaseProperty().addListener(
            (obs, oldSetBase, newSetBase) -> {
                if (newSetBase != null) {
                    setBaseList.getItems().add(newSetBase);
                }
            }
        );

        showEditorWindow(root);
    }

    @FXML
    private void edit() throws Exception {
        String resource = "/fxml/SetBaseEditor.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
        Parent root = loader.load();

        SetBaseEditorController controller = loader.getController();

        SetBase selectedItem = setBaseList.getSelectionModel()
                                          .getSelectedItem();
        controller.setSetBase(selectedItem);
        
        showEditorWindow(root);
    }
    
    @FXML
    private void remove() {
        final int selectedIndex = setBaseList.getSelectionModel()
                                             .getSelectedIndex();
        int newSelectedIndex =
            (selectedIndex == (setBaseList.getItems().size() - 1))
                ? (selectedIndex - 1)
                : selectedIndex;
        
        setBaseList.getItems().remove(selectedIndex);
        
        if (newSelectedIndex >= 0) {
            setBaseList.getSelectionModel().select(newSelectedIndex);
            
        } else {
            setBaseList.getSelectionModel().clearSelection();
        }
    }
    
    private void close() {
        exerciseNameLbl.getScene().getWindow().hide();
    }

    private void showEditorWindow(Parent root) {
        Stage stage = new Stage();
        
        stage.setTitle(exerciseNameLbl.getText() + " Set Editor");
        
        stage.initOwner(setBaseList.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }
    
    private Optional<ButtonType> showSaveAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(exerciseNameLbl.getText());
        alert.setContentText("Do you want to save the changes?");
        
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton, backButton);
        
        return alert.showAndWait();
    }
    
    private Optional<ButtonType> showCancelAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(exerciseNameLbl.getText());
        alert.setContentText("Do you want to cancel the changes?");
        
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yesButton, noButton);
        
        return alert.showAndWait();
    }
}