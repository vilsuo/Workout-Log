module com.mycompany {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens com.mycompany.controllers to javafx.fxml;
    
    exports com.mycompany.application to javafx.graphics;
    exports com.mycompany.controllers to javafx.fxml;
}
