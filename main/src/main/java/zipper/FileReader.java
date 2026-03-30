package zipper;

import java.io.BufferedReader; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.nio.file.Files; 
import java.nio.file.Path;
import java.nio.file.Paths; 
import java.util.List;
import javafx.scene.Node; 
import javafx.scene.input.DragEvent; 
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode; 
import java.io.File;
import javafx.scene.layout.Pane;




public class FileReader {




    public static void setupDragDrop(Pane targetNode)
    {
        targetNode.setOnDragOver(event -> { 
           if(event.getGestureSource() != targetNode && event.getDragboard().hasFiles()){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

            targetNode.setOnDragDropped(event -> { 
                 Dragboard db = event.getDragboard(); 
                 boolean success = false; 

            if(db.hasFiles()){
                File file = db.getFiles().get(0);
                System.out.println("Dropped: " + file.getAbsolutePath());
                success = true; 
            }

            event.setDropCompleted(success); 
            event.consume();
    });
    }


    
    
}
