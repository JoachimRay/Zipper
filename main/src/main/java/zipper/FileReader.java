package zipper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class FileReader {




    public static void setupDragDrop(Pane targetNode,UIController controller)
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
    
            
                for(File file : db.getFiles())
                {
                     String input = file.getAbsolutePath();
                     Statistics stats = new Statistics(); 


                    if(file.getName().toLowerCase().endsWith(".gz")) // Compressed Files
                    {
                        Path outdirUncompressed = Paths.get("C:\\Users\\joach_a5sww\\Downloads\\Files\\Uncompressed");
                        Path outPathUncompressed = outdirUncompressed.resolve(Paths.get(input).getFileName().toString().replaceFirst("[.][^.]+$", ""));
                        System.out.println("Dropped: " + input);
                   
                        controller.onFileDropped(file.getName(), false);

                        new Thread(() -> { 
                            try { 
                                Files.createDirectories(outdirUncompressed);
                                Reassemble.Reassembler(input, outPathUncompressed.toString(), new GZipDecompressor(), stats, controller);
                                controller.onFinished(stats, file, outPathUncompressed.toFile(), true);           
                            
                            
                            } catch (IOException e) {
                                controller.onFailed(e.getMessage());
                                e.printStackTrace();
                            }
                        }, "UncompressThread").start();

                        System.out.println("Decompressed: " + outPathUncompressed.toString());
                        success = true;
                        

                    } else // Uncompressed Files
                    {
                        Path outdircompressed = Paths.get("C:\\Users\\joach_a5sww\\Downloads\\Files\\Compressed");
                        Path outPath = outdircompressed.resolve(Paths.get(input).getFileName().toString() + ".gz");
                        System.out.println("Dropped: " + input);
                        

                        controller.onFileDropped(file.getName(), true);

                        // Run the compression in a separate thread to avoid blocking the UI
                        new Thread(() -> { 
                            try { 
                                Files.createDirectories(outdircompressed);
                                Chunk.Chunking(outPath.toString(), input, new GZipCompressor(), stats, controller);

                                controller.onFinished(stats, file, outPath.toFile(), false);
                            
                            } catch (IOException e) {
                                controller.onFailed(e.getMessage());
                                e.printStackTrace();
                            }
                        }, "CompressionThread").start();

                        System.out.println("Compressed: " + outPath.toString());
                        success = true; 
                    }
                }
            }

            event.setDropCompleted(success); 
            event.consume();
    });
    }



    
    
}
