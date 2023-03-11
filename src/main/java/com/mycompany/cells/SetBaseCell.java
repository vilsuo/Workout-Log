
package com.mycompany.cells;

import com.mycompany.domain.SetBase;
import com.mycompany.utilities.LocalDragboard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Supports drag and drop -feature
 * 
 * onDragDropped inefficient: whole list is replaced
 */
public class SetBaseCell extends ListCell<SetBase> {
    
    public SetBaseCell() {
        ListCell thisCell = this;
        
        // https://docs.oracle.com/javafx/2/api/javafx/scene/input/DragEvent.html
        
        /*
        Drag and drop gesture can be started by calling startDragAndDrop()
        (on a node or scene) inside of a DRAG_DETECTED event handler.
        The data to be transfered to drop target are placed to a
        dragBoard at this moment.
        
        Inside a DRAG_DETECTED event handler, if the startDragAndDrop()
        method is called on a node or scene and a dragged data is made
        available to the returned Dragboard, the object on which
        startDragAndDrop() has been called is considred a gesture source
        and the drag and drop gesture is started. The Dragboard has
        system clipboard functionality but is specifically used for drag
        and drop data transfer.
        
        After the drag and drop gesture has been started, any object
        (Node, Scene) over which the mouse is dragged is a potential
        drop target.
        */
        setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }
            
            // 1. Set up dummy data on the dragboard else drag and drop won't
            // be initiated
            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(getItem().toString());
            db.setContent(content);
            
            // 2. Put the real data in a custom dragboard, since the dragboard
            // does not support custum classes
            LocalDragboard.getInstance().putValue(SetBase.class, getItem());
            
            event.consume();
        });
        
        /*
        In DRAG_OVER event handler a potential drop target has the ability
        to make it known that it is an actual target. This is done by calling
        acceptTransferModes(TransferMode...) on the event, passing transfer
        modes it is willing to accept. If it is not called during the event
        delivery or if none of the passed transfer modes is supported by
        gesture source, then the potential drop target is not considered to
        be an actual drop target.
             
        A potential drop target can decide to change its appearance to let
        the user know that the dragged data can be dropped on it. This can
        be done in a DRAG_OVER event handler, based on the position of the
        mouse. Another option is to change the potential target's appearance
        in a DRAG_ENTERED and DRAG_EXITED handlers.
        */
        setOnDragOver(event -> {
            /*
            When deciding weather to accept the event by calling
            acceptTransferModes(TransferMode...), the type of data available
            on the Dragboard should be considered. Access to the Dragboard
            is provided by the getDragboard() method.
            */
            if ((event.getGestureSource() != thisCell) &&
                    LocalDragboard.getInstance().hasType(SetBase.class))
            {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });
        
        /*
        When the mouse is dragged into the boundaries of potential drop
        target, the potential target gets a DRAG_ENTERED event
        */
        setOnDragEntered(event -> {
            if ((event.getGestureSource() != thisCell) &&
                    LocalDragboard.getInstance().hasType(SetBase.class))
            {
                setOpacity(0.3);
            }
        });
        
        /*
        When the mouse is dragged outside of the potential target's bounds,
        it gets a DRAG_EXITED event
        */
        setOnDragExited(event -> {
            if ((event.getGestureSource() != thisCell) &&
                    LocalDragboard.getInstance().hasType(SetBase.class))
            {
                setOpacity(1);
            }
        });
        
        /*
        A drag and drop gesture ends when the mouse button is released. If
        this happens over a gesture target that accepted previous DRAG_OVER
        events with a transfer mode supported by gesture source, a
        DRAG_DROPPED event is sent to the gesture target. In its handler,
        the gesture target can access the data on the dragboard. After data
        has been transferred (or decided not to transfer), the gesture needs
        to be completed by calling setDropCompleted(Boolean) on the event.
        The Boolean argument indicates if the data has been transferred
        successfully or not. If it is not called, the gesture is considered
        unsuccessful.
        */
        setOnDragDropped(event -> {
            if (getItem() == null) {
                return;
            }
            
            boolean success = false;

            LocalDragboard ldb = LocalDragboard.getInstance();
            if (ldb.hasType(SetBase.class)) {
                ObservableList<SetBase> items = getListView().getItems();
                
                SetBase sourceSetBase = ldb.getValue(SetBase.class);
                SetBase targetSetBase = getItem();
                
                int sourceIndex = items.indexOf(sourceSetBase);
                int targetIndex = items.indexOf(targetSetBase);
                
                int min = Math.min(sourceIndex, targetIndex);
                int max = Math.max(sourceIndex, targetIndex);
                        
                ObservableList<SetBase> temp = FXCollections.observableArrayList();
                for (int i = 0; i < items.size(); ++i) {
                    if ((i < min) || (i > max)) {
                        temp.add(items.get(i));
                        
                    } else if (i == targetIndex) {
                        temp.add(sourceSetBase);
                        
                    } else {
                        if (sourceIndex < targetIndex) {
                            temp.add(items.get(i + 1));
                            
                        } else {
                            temp.add(items.get(i - 1));
                        }
                    }
                }
                
                items.setAll(temp);
                success = true;
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
        
        /*
        After the gesture has been finished, whether by successful or
        unsuccessful data transfer or being canceled, the DRAG_DONE event is
        sent to the gesture source. The getTransferMode() method of the event
        indicates to the gesture source how the transfer of data was completed.
        If the transfer mode has the value MOVE, then this allows the source to
        clear out its data. Clearing the source's data gives the appropriate
        appearance to a user that the data has been moved by the drag and drop
        gesture. If it has the value null, then the drag and drop gesture ended
        without any data being transferred. This could happen as a result of a
        mouse release event over a node that is not a drop target, or the user
        pressing the ESC key to cancel the drag and drop gesture, or by the
        gesture target reporting an unsuccessful data transfer
        */
        setOnDragDone(DragEvent::consume);
    }
    
    @Override
    protected void updateItem(SetBase item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            
        } else {
            setText(item.toString());
        }
    }
}
