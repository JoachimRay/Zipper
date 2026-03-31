package zipper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.application.Platform;

public class FileReader {




    public static void setupDragDrop(Pane targetNode, Label statsLabel, ProgressBar progressBar)
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
                        
                        Platform.runLater(() -> { 
                            statsLabel.setText("Decompressing: " + file.getName() + "...");
                            progressBar.setProgress(-1);
                        });

                        new Thread(() -> { 
                            try { 
                                Files.createDirectories(outdirUncompressed);
                                Reassemble.Reassembler(input, outPathUncompressed.toString(), new GZipDecompressor(), stats);
                                
                            Platform.runLater(() -> { 
                                File inFile = new File(input); 
                                File outFile = outPathUncompressed.toFile(); 

                                statsLabel.setText(String.format(  "%.2f MB → %.2f MB | %d ms | %.2f MB/s",
                                        inFile.length()  / (1024.0 * 1024.0),
                                        outFile.length() / (1024.0 * 1024.0),
                                        stats.elapsedms(),
                                        stats.getThroughPutMBPS()
                                    ));
                                progressBar.setProgress(1.0); 
                                StatsDialog.show(stats, inFile, outFile, "Decompression"); 
                            });
                            
                            
                            
                            } catch (IOException e) {
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
                        

                           Platform.runLater(() -> { 
                                statsLabel.setText("Compressed: " + file.getName() + "...");
                                progressBar.setProgress(-1);
                            });

                        // Run the compression in a separate thread to avoid blocking the UI
                        new Thread(() -> { 
                            try { 
                                Files.createDirectories(outdircompressed);
                                Chunk.Chunking(outPath.toString(), input, new GZipCompressor(), stats);

                            Platform.runLater(() -> { 
                                File inFile = new File(input); 
                                File outFile = outPath.toFile(); 

                                statsLabel.setText(String.format(  "%.2f MB → %.2f MB | %d ms | %.2f MB/s",
                                        inFile.length()  / (1024.0 * 1024.0),
                                        outFile.length() / (1024.0 * 1024.0),
                                        stats.elapsedms(),
                                        stats.getThroughPutMBPS()
                                    ));
                                progressBar.setProgress(1.0); 
                                StatsDialog.show(stats, inFile, outFile, "Compression"); 
                            });
                            

                            } catch (IOException e) {
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
