package zipper;

import java.io.IOException; 
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode; 
import java.io.File;
import javafx.scene.layout.Pane;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


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
                String input = file.getAbsolutePath();
                Path outdir = Paths.get("C:\\Users\\joach_a5sww\\Downloads\\Files\\Compressed");
                Path outPath = outdir.resolve(Paths.get(input).getFileName().toString() + ".gz");
                System.out.println("Dropped: " + input);
                
                // Run the compression in a separate thread to avoid blocking the UI

                new Thread(() -> { 
                    try { 
                        Files.createDirectories(outdir);
                        Chunk.Chunking(outPath.toString(), input, new GZipCompressor());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "CompressionThread").start();

                System.out.println("Compressed: " + outPath.toString());
                success = true; 
            }

            event.setDropCompleted(success); 
            event.consume();
    });
    }



    
    
}
