package zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class ZipUtil {

    private ZipUtil() {}

    public static void zipDirectory(File sourceDir, File outZipFile) throws IOException {
        Path sourcePath = sourceDir.toPath();
        try (FileOutputStream fos = new FileOutputStream(outZipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            Files.walk(sourcePath)
                 .forEach(path -> {
                     try {
                         Path relPath = sourcePath.relativize(path);
                         String entryName = relPath.toString().replace("\\", "/");
                         if (Files.isDirectory(path)) {
                             if (!entryName.isEmpty()) {
                                 if (!entryName.endsWith("/")) entryName = entryName + "/";
                                 zos.putNextEntry(new ZipEntry(entryName));
                                 zos.closeEntry();
                             }
                         } else {
                             ZipEntry ze = new ZipEntry(entryName);
                             zos.putNextEntry(ze);
                             try (InputStream is = Files.newInputStream(path)) {
                                 byte[] buffer = new byte[8192];
                                 int len;
                                 while ((len = is.read(buffer)) != -1) {
                                     zos.write(buffer, 0, len);
                                 }
                             }
                             zos.closeEntry();
                         }
                     } catch (IOException e) {
                         throw new RuntimeException(e);
                     }
                 });
        }
    }

    public static void unzipToDirectory(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) destDir.mkdirs();
        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File out = new File(destDir, entry.getName());
                if (entry.isDirectory()) {
                    out.mkdirs();
                } else {
                    File parent = out.getParentFile();
                    if (parent != null && !parent.exists()) parent.mkdirs();
                    try (OutputStream os = new FileOutputStream(out)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}
