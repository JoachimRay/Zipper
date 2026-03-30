package zipper;
import java.io.FileOutputStream;
import java.io.IOException;

public interface Decompressor {
    FileOutputStream getDecompresserStream(String outputPath) throws IOException;
}
