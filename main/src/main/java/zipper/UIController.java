package zipper;

import javafx.fxml.FXML; 
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.File;

import javafx.application.Platform;

public class UIController {

    @FXML 
    private Pane DropFile; 
    @FXML 
    private Label statsLabel; 
    @FXML 
    private ProgressBar progressBar;

    @FXML private Label termTitle; 
    @FXML private Label termCommand;
    @FXML private Label termProgress; 
    @FXML private Label termChunks; 
    @FXML private Label termElapsed; 
    @FXML private Label termSpeed; 
    @FXML private Label termCompressedThread; 
    @FXML private Label termDecompressThread; 
    @FXML private Label termStatus; 
    @FXML private Label termOrigBar; 
    @FXML private Label termCompBar;
    @FXML private Label termSaved;
    @FXML private Label termRatio; 
    @FXML private Label termTime; 
    
    private static final int BAR_WIDTH = 20;


    @FXML
        private void initialize(){ 
     // Set block characters from Java where encoding is guaranteed
    termTitle.setText("zipper \u2014 compression monitor");
    termProgress.setText("Compressing  : [" + emptyBar() + "] 0%");
    termCompressedThread.setText("CompressionThread  : [" + emptyBar() + "] (Idle)");
    termDecompressThread.setText("DecompressionThread: [" + emptyBar() + "] (Idle)");
    termOrigBar.setText("Original  :   [" + emptyBar() + "] 0 KB");
    termCompBar.setText("Compressed:   [" + emptyBar() + "] 0 KB");

    FileReader.setupDragDrop(DropFile, this);
    }

    //called by fileReader when a file is dropped 
    public void onFileDropped(String fileName, boolean isDecompression)
    {
        String op = isDecompression ? "Decompressing" : "Compressing";

        Platform.runLater(() -> { 
            termCommand.setText("$ zipper " + (isDecompression ? "decompress " : "compress ") + fileName);
            termProgress.setText(op + ": [" + emptyBar() + "]");
            termProgress.getStyleClass().setAll("term-yellow"); 
            termChunks.setText("Chunks:       : 0/0");
            termElapsed.setText("Elapsed      : 0.0s");
            termSpeed.setText("Speed          : 0.00 MB/s");
            termStatus.setText("Status        : RUNNING");
            termStatus.getStyleClass().setAll("term-yellow"); 
            

            if(isDecompression)
            {
                termDecompressThread.setText("Decompression Thread: [" + emptyBar() + "] (Working)"); 
                termDecompressThread.getStyleClass().setAll("term-green");
                termCompressedThread.setText("Compression Thread: [" + emptyBar() + "] (Idle)");
                termCompressedThread.getStyleClass().setAll("term-dim");   
            } else 
            { 
                termCompressedThread.setText("Compression Thread: [" + emptyBar() + " ] (Working)");
                termCompressedThread.getStyleClass().setAll("term-green");
                termDecompressThread.setText("Decompression Thread: [" + emptyBar() + "] (Idle)");
                termDecompressThread.getStyleClass().setAll("term-dim");
            }
        });

    }

    public void updateProgress(Statistics stats, int totalChunks, boolean isDecompression)
    {
        Platform.runLater(() -> {
            int completed = stats.getCompletedChunks();
            double pct = totalChunks == 0 ? 0 : (double) completed / totalChunks * 100;
            String bar = buildBar(pct / 100.0);
            String op = isDecompression ? "Decompressing" : "Compressing";
            
            termProgress.setText(op + ": [" + bar + "] " + (int) pct + "%");
            termChunks.setText("Chunks:      : " + completed + "/" + totalChunks);
            termElapsed.setText("Elapsed     : " + String.format("%.1f", stats.elapsedms() / 100.0) + "s"); 
            termSpeed.setText("Speed         : " + String.format("%.2f", stats.getThroughPutMBPS()) + " MB/s");

            String threadBar = "[" + bar + "]";
            if(isDecompression)
            {
                termDecompressThread.setText("Decompression Thread: " + threadBar + " (Working)"); 
            } else 
            { 
                termCompressedThread.setText("Compression Thread: " + threadBar + " (Working)");
            }
        });
    }

    public void onFinished(Statistics stats, File inputFile, File outputFile, boolean isDecompression)
    {
        Platform.runLater(() -> { 
            long origBytes = inputFile.length(); 
            long compBytes = outputFile.length();
            double ratio = origBytes == 0 ? 0 : stats.CompressionRatio();
            double saved = stats.SpaceSaved();

            String op = isDecompression ? "Decompressed" : "Compressed";
            termProgress.setText(op + ": [" + fullBar() + "] 100%");
            termProgress.getStyleClass().setAll("term-green");
            termCompressedThread.setText("CompressionThread  : [" + emptyBar() + "] (Idle)");
            termCompressedThread.getStyleClass().setAll("term-dim");
            termDecompressThread.setText("DecompressionThread: [" + emptyBar() + "] (Idle)");
            termDecompressThread.getStyleClass().setAll("term-dim");

            // Status
            termStatus.setText("Status       : DONE");
            termStatus.getStyleClass().setAll("term-green");

            // Size bars — original is always full, compressed is proportional
            String origBar = fullBar();
            String compBar = buildBar(ratio);
            termOrigBar.setText("Original  :   [" + origBar + "] " + formatSize(origBytes));
            termOrigBar.getStyleClass().setAll("term-blue");
            termCompBar.setText("Compressed:   [" + compBar + "] " + formatSize(compBytes));
            termCompBar.getStyleClass().setAll("term-green");

            // Summary stats
            termSaved.setText("Saved        : " + String.format("%.1f", saved) + "%");
            termSaved.getStyleClass().setAll("term-green");
            termRatio.setText("Ratio        : " + String.format("%.2f", ratio));
            termRatio.getStyleClass().setAll("term-white");
            termTime.setText("Time         : " + String.format("%.1f", stats.elapsedms() / 1000.0) + "s");
            termTime.getStyleClass().setAll("term-white");
        });
    }

    // Called on failure
    public void onFailed(String error) {
        Platform.runLater(() -> {
            termStatus.setText("Status       : FAILED — " + error);
            termStatus.getStyleClass().setAll("term-yellow");
        });
    }

    // Bar builders
    private String buildBar(double fraction) {
        int filled = (int) Math.round(fraction * BAR_WIDTH);
        filled = Math.max(0, Math.min(BAR_WIDTH, filled));
        return "\u2588".repeat(filled) + "\u2591".repeat(BAR_WIDTH - filled);
    }

    private String fullBar()  { return "\u2588".repeat(BAR_WIDTH); }
    private String emptyBar() { return "\u2591".repeat(BAR_WIDTH); }

    private String formatSize(long bytes) {
        if (bytes >= 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        if (bytes >= 1024)        return String.format("%.1f KB", bytes / 1024.0);
        return bytes + " B";
    }

    
}
