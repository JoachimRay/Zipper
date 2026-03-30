package zipper;
import java.io.IOException;
import java.io.OutputStream;

public class GZipCompressor implements Compressor{ 
    @Override 
    public OutputStream getCompressedStream(OutputStream os) throws IOException
    { 
        return new java.util.zip.GZIPOutputStream(os);
    }
    
}
