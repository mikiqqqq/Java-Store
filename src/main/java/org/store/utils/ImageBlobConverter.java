package org.store.utils;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import javax.sql.rowset.serial.SerialBlob;

public class ImageBlobConverter {

    // Method to convert an image file to a byte array
    public static byte[] imageToByteArray(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    // Method to convert a byte array to a Blob
    public static Blob byteArrayToBlob(byte[] data) throws SQLException {
        return new SerialBlob(data);
    }

    // Method to convert a Blob to a byte array
    public static byte[] blobToByteArray(Blob blob) throws SQLException, IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(blob.getBytes(1, (int) blob.length()))) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }

    // Method to convert a byte array to an Image
    public static Image byteArrayToImage(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        return new Image(bis);
    }

    // Method to convert a Blob to an Image
    public static Image blobToImage(Blob blob) throws SQLException, IOException {
        return byteArrayToImage(blobToByteArray(blob));
    }
}