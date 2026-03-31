package zipper;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;


public class Reassemble {


    public static void Reassembler(String CompressedFile, String DecompressedFile, Decompressor decompresser, Statistics stats) throws IOException
    {
        stats.start();
        stats.addOriginalSize(new java.io.File(CompressedFile).length());

        try(FileInputStream fis = new FileInputStream(CompressedFile); 
            GZIPInputStream giz = new GZIPInputStream(fis);
            FileOutputStream fos = decompresser.getDecompresserStream(DecompressedFile))
            {
                byte[] buffer = new byte[8192]; // 8 kilobytes of chunking size 
                int len; 

                while((len = giz.read(buffer)) != -1)
                {
                    stats.RegisterChunks(); 
                    fos.write(buffer, 0, len); 
                    stats.addDecompressedSize(len);
                    stats.ChunkCompleted();
                }
            }catch(IOException e)
            {
                System.out.println("Error during reassembly: ");
                e.printStackTrace();
                stats.ChunkFailed();
                throw e;
            }
        stats.end();
    }
    
}
