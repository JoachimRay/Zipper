package zipper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
public class Chunk {


    public static void Chunking(String CompressedFile, String InputFile, Compressor compressor, Statistics stats, UIController controller)throws IOException
    { 

        stats.start(); 
        File inputFile = new File(InputFile);
        stats.addOriginalSize(inputFile.length());

        int totalChunks = (int) Math.ceil((double) inputFile.length() / 8192);  



        try( FileInputStream fis = new FileInputStream(InputFile); 
            FileOutputStream fos = new FileOutputStream(CompressedFile);
            OutputStream gzipOS = compressor.getCompressedStream(fos)){ 


            byte[] buffer = new byte[8192]; // 8 kilobytes of chunking size
            int len; 

            while((len = fis.read(buffer)) != -1)
            {
                stats.RegisterChunks(); 
                gzipOS.write(buffer, 0, len); 
                stats.addCompressedSize(len);
                stats.ChunkCompleted(); 
                controller.updateProgress(stats, totalChunks, false);
            }

        } catch(Exception e)
        { 
            System.out.println("Error during chunking: ");
            e.printStackTrace();
            stats.ChunkFailed(); 
            throw new IOException(e);
        }
        stats.end();
    }
    
}
