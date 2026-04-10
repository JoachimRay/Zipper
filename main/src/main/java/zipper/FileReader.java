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

    private static Path getWritableOutputDir() throws IOException {
        Path[] candidates = new Path[] {
            Paths.get("C:\\Users\\joach\\Downloads\\compresse"),
            Paths.get(System.getProperty("user.home"), "Downloads", "compresse"),
            Paths.get(System.getProperty("java.io.tmpdir"), "zipper-compresse")
        };

        for (Path p : candidates) {
            try {
                Files.createDirectories(p);
                Path test = p.resolve(".perm_test_" + System.nanoTime());
                Files.createFile(test);
                Files.deleteIfExists(test);
                return p;
            } catch (IOException ex) {
                // try next candidate
            }
        }

        throw new IOException("none of the candidate directories are writable");
    }

    public static void setupDragDrop(Pane targetNode, UIController controller) {

        targetNode.setOnDragOver(event -> {
            if (event.getGestureSource() != targetNode && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        targetNode.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                for (File file : db.getFiles()) {
                    String input = file.getAbsolutePath();
                    Statistics stats = new Statistics();

                    // Directory compression: produce a .zip
                    if (file.isDirectory()) {
                        Path outdirCompressed;
                        try {
                            outdirCompressed = getWritableOutputDir();
                        } catch (IOException ioe) {
                            controller.onFailed("No writable output directory found: " + ioe.getMessage());
                            ioe.printStackTrace();
                            continue;
                        }
                        Path outPath = outdirCompressed.resolve(Paths.get(input).getFileName().toString() + ".zip");

                        controller.onFileDropped(file.getName(), true);
                        new Thread(() -> {
                            try {
                                Files.createDirectories(outdirCompressed);
                                ZipUtil.zipDirectory(file, outPath.toFile());
                                controller.onFinished(stats, file, outPath.toFile(), false);
                            } catch (IOException e) {
                                controller.onFailed("Failed to write output: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }, "ZipCompressionThread").start();

                        System.out.println("Zipped directory: " + outPath.toString());
                        success = true;

                    } else if (file.getName().toLowerCase().endsWith(".gz")) { // Compressed Files (gzip)
                        Path outdirUncompressed;
                        try {
                            outdirUncompressed = getWritableOutputDir();
                        } catch (IOException ioe) {
                            controller.onFailed("No writable output directory found: " + ioe.getMessage());
                            ioe.printStackTrace();
                            continue;
                        }
                        Path outPathUncompressed = outdirUncompressed.resolve(Paths.get(input).getFileName().toString().replaceFirst("[.][^.]+$", ""));
                        System.out.println("Dropped: " + input);

                        controller.onFileDropped(file.getName(), false);

                        new Thread(() -> {
                            try {
                                Files.createDirectories(outdirUncompressed);
                                Reassemble.Reassembler(input, outPathUncompressed.toString(), new GZipDecompressor(), stats, controller);
                                controller.onFinished(stats, file, outPathUncompressed.toFile(), true);
                            } catch (IOException e) {
                                controller.onFailed("Failed to write output: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }, "UncompressThread").start();

                        System.out.println("Decompressed: " + outPathUncompressed.toString());
                        success = true;

                    } else if (file.getName().toLowerCase().endsWith(".zip")) { // Compressed folder archive
                        Path outdirUncompressed;
                        try {
                            outdirUncompressed = getWritableOutputDir();
                        } catch (IOException ioe) {
                            controller.onFailed("No writable output directory found: " + ioe.getMessage());
                            ioe.printStackTrace();
                            continue;
                        }
                        Path outPathUncompressed = outdirUncompressed.resolve(Paths.get(input).getFileName().toString().replaceFirst("[.][^.]+$", ""));
                        System.out.println("Dropped: " + input);

                        controller.onFileDropped(file.getName(), false);
                        new Thread(() -> {
                            try {
                                Files.createDirectories(outdirUncompressed);
                                ZipUtil.unzipToDirectory(file, outPathUncompressed.toFile());
                                controller.onFinished(stats, file, outPathUncompressed.toFile(), true);
                            } catch (IOException e) {
                                controller.onFailed("Failed to write output: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }, "UnzipThread").start();

                        System.out.println("Unzipped: " + outPathUncompressed.toString());
                        success = true;

                    } else { // Regular file -> gzip
                        Path outdircompressed;
                        try {
                            outdircompressed = getWritableOutputDir();
                        } catch (IOException ioe) {
                            controller.onFailed("No writable output directory found: " + ioe.getMessage());
                            ioe.printStackTrace();
                            continue;
                        }
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
                                controller.onFailed("Failed to write output: " + e.getMessage());
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
