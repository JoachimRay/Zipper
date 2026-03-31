package zipper;

import java.io.File;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class StatsDialog {

    public static void show(Statistics stats, File input, File output, String operation)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("✔ " + input.getName()); 

        alert.setContentText(String.format( 
                "Input size       : %.2f MB\n" +
                "Output size      : %.2f MB\n" +
                "Space saved      : %.1f%%\n" +
                "Time taken       : %d ms\n" +
                "Throughput       : %.2f MB/s\n" +
                "Chunks completed : %d\n" +
                "Chunks failed    : %d\n" +
                "Saved to         : %s",
                input.length()  / (1024.0 * 1024.0),
                output.length() / (1024.0 * 1024.0),
                stats.SpaceSaved(),
                stats.elapsedms(),
                stats.getThroughPutMBPS(),
                stats.completedChunksGetter(),
                stats.failedChunksGetter(),
                output.getAbsolutePath()
                ));
        alert.showAndWait();
    }
    
}
