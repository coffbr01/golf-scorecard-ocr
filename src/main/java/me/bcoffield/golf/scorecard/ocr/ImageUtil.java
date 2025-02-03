package me.bcoffield.golf.scorecard.ocr;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {
    /**
     * Converts an InputStream containing image data to a byte array
     *
     * @param inputStream the input stream of the image file
     * @return the image bytes
     * @throws IOException if an I/O error occurs while reading from the stream
     */
    public static byte[] convertImageToBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read from the input stream and write to the output stream
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Convert the binary data to Base64 string
            return byteArrayOutputStream.toByteArray();
        }
    }

}