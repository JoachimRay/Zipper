package zipper;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {

    private final AtomicLong originalSize = new AtomicLong(0);
    private final AtomicLong compressedSize = new AtomicLong(0);
    private final AtomicLong startTime = new AtomicLong(0); 
    private final AtomicLong endTime = new AtomicLong(0);

    private final AtomicInteger totalChunks = new AtomicInteger(0);
    private final AtomicInteger completedChunks = new AtomicInteger(0);
    private final AtomicInteger failedChunks = new AtomicInteger(0);

    public void start(){
        startTime.set(System.nanoTime());
    }

    public void end(){ 
        endTime.set(System.nanoTime());
    }

    public void addOriginalSize(long size)
    {
        originalSize.addAndGet(size);
    }

    public void addCompressedSize(long size)
    { 
        compressedSize.addAndGet(size);
    }

    public void addDecompressedSize(long size)
    {
        originalSize.addAndGet(size);
    }


    public void RegisterChunks()
    {
        totalChunks.incrementAndGet(); 
    }

    public void ChunkCompleted() 
    {
        completedChunks.incrementAndGet(); 
    }

    public int getCompletedChunks() {
        return completedChunks.get();
    }

    public int getFailedChunks() {
        return failedChunks.get();
    }

    public void ChunkFailed() 
    {
        failedChunks.incrementAndGet();
    }

    public double CompressionRatio() 
    { 
        long original = originalSize.get(); 
    
        if(original == 0 )
        { return 0;}

        return(double) compressedSize.get() / original; 
    }

    public double SpaceSaved() 
    { 
        return (1 - CompressionRatio()) * 100; 
    }

    public long elapsedms() 
    { 
        long end = endTime.get() == 0 ? System.nanoTime() : endTime.get(); 
        return(end - startTime.get()) / 1_000_000;
    }

    public double getThroughPutMBPS() 
     { 
        long ms = elapsedms(); 
        if(ms == 0) return 0; 
        return(originalSize.get() / (1024.0 * 1024.0)) / (ms / 1000.0);
     }







     
}
