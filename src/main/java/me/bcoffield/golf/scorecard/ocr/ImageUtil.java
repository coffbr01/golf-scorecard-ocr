package me.bcoffield.golf.scorecard.ocr;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageUtil {

    /**
     * Converts an InputStream containing image data to a Base64 encoded string.
     *
     * @param inputStream the input stream of the image file
     * @return a Base64 encoded string representation of the image
     * @throws IOException if an I/O error occurs while reading from the stream
     */
    public static String convertImageToBase64(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read from the input stream and write to the output stream
            while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // Convert the binary data to Base64 string
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        }
    }
}