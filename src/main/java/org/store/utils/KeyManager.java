package org.store.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class KeyManager {
    private final String FILE_PATH = "src/main/resources/dat/";
    private final String FILE_NAME = FILE_PATH + "keys.bin";

    // Generate AES Key
    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    // Generate RSA Key Pair
    private KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        return keyPairGen.generateKeyPair();
    }

    // Generate and Save Combined Keys
    public CryptoKey generateAndSaveKeys(int orderId) throws Exception {
        // Generate AES key
        SecretKey aesKey = generateAESKey();

        // Generate RSA key pair
        KeyPair rsaKeyPair = generateRSAKeyPair();
        PublicKey rsaPublicKey = rsaKeyPair.getPublic();
        PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();

        // Create CryptoKey object
        CryptoKey cryptoKey = new CryptoKey(orderId, aesKey, rsaPublicKey, rsaPrivateKey);

        // Save the single key to file
        saveSingleKeyToFile(cryptoKey);

        return cryptoKey;
    }

    // Serialize a single key to a File in the resources/dat directory
    private void saveSingleKeyToFile(CryptoKey cryptoKey) throws IOException, ClassNotFoundException {
        List<CryptoKey> existingKeys = loadKeysFromFile(); // Load existing keys
        existingKeys.add(cryptoKey); // Add the new key

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(existingKeys); // Save all keys back to file
        }
    }

    // Deserialize Keys from a File in the resources/dat directory
    public List<CryptoKey> loadKeysFromFile() throws IOException, ClassNotFoundException {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>(); // Return an empty list if file doesn't exist or is empty
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<CryptoKey>) ois.readObject();
        }
    }

    public CryptoKey getKeyForOrder(int orderId) throws IOException, ClassNotFoundException {
        List<CryptoKey> keys = loadKeysFromFile();
        return keys.stream()
                .filter(key -> key.getOrderId() == orderId)
                .findFirst()  // Get the first matching key
                .orElse(null); // Return null if no matching key is found
    }

    // AES Encryption
    public String encryptWithAES(SecretKey aesKey, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // AES Decryption
    public String decryptWithAES(SecretKey aesKey, String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    // RSA Encryption
    public String encryptWithRSA(PublicKey rsaPublicKey, String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // RSA Decryption
    public String decryptWithRSA(PrivateKey rsaPrivateKey, String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}