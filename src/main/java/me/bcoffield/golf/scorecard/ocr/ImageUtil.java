package me.bcoffield.golf.scorecard.ocr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

    private static BufferedImage resizeImage(BufferedImage originalImage) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        // We want the new one to be 1000 wide.
        double scaleFactor = (double) 1000 / w;
        int targetWidth = (int) (w * scaleFactor);
        int targetHeight = (int) (h * scaleFactor);
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    private static byte[] convertImageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] resizeAndGetBytes(InputStream imageIS) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageIS);
        BufferedImage scaledImage = ImageUtil.resizeImage(bufferedImage);
        return convertImageToByteArray(scaledImage);
    }
}