package zipper;

import javafx.fxml.FXML; 
import javafx.scene.layout.Pane;

public class UIController {

    @FXML 
    private Pane DropFile; 

    @FXML
        private void initialize(){ 
        System.out.println("UIController initialized, Dropfile = " + DropFile);
        FileReader.setupDragDrop(DropFile);
    }


    
}
