/*
 * Created on 15.06.2016
 * code found at http://www.journaldev.com/470/generate-qr-code-image-from-java-program
 */
package de.hdm.animation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import sun.misc.BASE64Encoder;

public class GenerateQRCode {
    /**
     * @param args
     * @throws WriterException
     * @throws IOException
     */
    public static void main(String[] args) throws WriterException, IOException {
        String qrCodeText = "http://www.christian-rathke.de";
        String filePath = "C:/users/christian/desktop/QRCode.png";
        int size = 300;
        String fileType = "png";
        File qrFile = new File(filePath);
        writeQRImage(qrFile, qrCodeText, size, fileType);
        System.out.println("DONE");
    }

    public static BufferedImage createQRImage(String qrCodeText, int size) throws WriterException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
    }
    
    private static void writeQRImage(File qrFile, String qrCodeText, int size, String fileType)
            throws WriterException, IOException {
        BufferedImage image = createQRImage(qrCodeText, size);
        ImageIO.write(image, fileType, qrFile);
    }
    
    
}
