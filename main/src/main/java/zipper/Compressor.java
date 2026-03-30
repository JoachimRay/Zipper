package zipper;
import java.io.IOException;
import java.io.OutputStream;

public interface Compressor {
    OutputStream getCompressedStream(OutputStream os) throws IOException;
}
