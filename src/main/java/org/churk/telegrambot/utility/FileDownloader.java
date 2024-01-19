package org.churk.telegrambot.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Iterator;

@Slf4j
@Component
public class FileDownloader {
    private static final int timeOutSeconds = 30;

    public static String waitForDownload(String downloadDirectory, String fileName, String extension) {
        String filePath = downloadDirectory + fileName + "_compressed" + extension;
        long startTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - startTime > timeOutSeconds * 1000) {
                log.error("Timeout while waiting for meme download");
                return null;
            }
            if (isDownloaded(filePath)) {
                log.info("File downloaded successfully: {}", filePath);
                return filePath;
            }
        }
    }

    private static boolean isDownloaded(String filePath) {
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    public static void downloadFileFromUrl(String apiUrl, String downloadDirectory, String fileName, String extension) {
        String filePath = downloadDirectory + fileName + extension;
        String compressedFilePath = downloadDirectory + fileName + "_compressed" + extension;
        log.info("Downloading file from {}", apiUrl);

        int bufferSize = 1024 * 1024;

        try (InputStream in = new BufferedInputStream(new URL(apiUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
            byte[] dataBuffer = new byte[bufferSize];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, bufferSize)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            compressFile(extension, filePath, compressedFilePath);
        } catch (IOException e) {
            log.error("Error while downloading or compressing file", e);
        }
    }

    private static void compressFile(String extension, String filePath, String compressedFilePath) throws IOException {
        if (extension.equals(".gif")) {
            compressGif(filePath, compressedFilePath);
        } else {
            compressImage(extension, filePath, compressedFilePath);
        }
    }

    private static void compressImage(String extension, String filePath, String compressedFilePath) {
        try {
            // Read the downloaded image
            BufferedImage image = ImageIO.read(new File(filePath));

            // Compress the image
            File compressedImageFile = new File(compressedFilePath);
            OutputStream os = new FileOutputStream(compressedImageFile);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(getValidFormat(extension));
            if (!writers.hasNext()) {
                throw new IOException("No suitable ImageWriter found for extension: " + extension);
            }
            ImageWriter writer = writers.next();

            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.05f);
            }
            writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

            os.close();
            ios.close();
            writer.dispose();

            log.info("File compressed and saved as: {}", compressedFilePath);
        } catch (IOException e) {
            log.error("Error while compressing file", e);
        }
    }

    private static void compressGif(String filePath, String compressedFilePath) {
        try {
            // Read the downloaded image
            BufferedImage image = ImageIO.read(new File(filePath));

            // Compress the image
            File compressedImageFile = new File(compressedFilePath);
            OutputStream os = new FileOutputStream(compressedImageFile);

            ImageOutputStream ios = ImageIO.createImageOutputStream(os);
            ImageIO.write(image, "gif", ios);

            os.close();
            ios.close();

            log.info("File compressed and saved as: {}", compressedFilePath);
        } catch (IOException e) {
            log.error("Error while compressing file", e);
        }
    }

    private static String getValidFormat(String extension) {
        return switch (extension.toLowerCase()) {
            case ".jpg", ".jpeg" -> "jpeg";
            case ".png" -> "png";
            case ".gif" -> "gif";
            default -> extension;
        };
    }
}
