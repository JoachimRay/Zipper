package zipper;
import java.io.FileOutputStream;
import java.io.IOException;

public class GZipDecompressor implements Decompressor{

    @Override 
    public FileOutputStream getDecompresserStream(String outputPath)throws IOException
    {
        return new FileOutputStream(outputPath);
    }
    
}
