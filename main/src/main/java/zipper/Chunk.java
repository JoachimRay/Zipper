package zipper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Chunk {


    public static void Chunking(String CompressedFile, String InputFile, Compressor compressor)throws IOException
    { 
        try( FileInputStream fis = new FileInputStream(InputFile); 
            FileOutputStream fos = new FileOutputStream(CompressedFile);
            OutputStream gzipOS = compressor.getCompressedStream(fos)){ 


            byte[] buffer = new byte[8192]; // 8 kilobytes of chunking size
            int len; 

            while((len = fis.read(buffer)) != -1)
            {
                gzipOS.write(buffer, 0, len); 
            }

        } catch(Exception e)
        { 
            System.out.println("Error during chunking: ");
            e.printStackTrace();
        }

    }
    
}
