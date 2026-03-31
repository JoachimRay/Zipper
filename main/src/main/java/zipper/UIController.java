package zipper;

import javafx.fxml.FXML; 
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class UIController {

    @FXML 
    private Pane DropFile; 
    @FXML 
    private Label statsLabel; 
    @FXML 
    private ProgressBar progressBar;

    @FXML
        private void initialize(){ 
        System.out.println("UIController initialized, Dropfile = " + DropFile);
        FileReader.setupDragDrop(DropFile, statsLabel, progressBar);
    }


    
}
