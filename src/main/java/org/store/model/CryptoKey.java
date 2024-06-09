package org.store.model;

import javax.crypto.SecretKey;
import java.io.Serial;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CryptoKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int userId;
    private final SecretKey aesKey;
    private final PublicKey rsaPublicKey;
    private final PrivateKey rsaPrivateKey;

    // Constructors, getters, and setters
    public CryptoKey(int userId, SecretKey aesKey, PublicKey rsaPublicKey, PrivateKey rsaPrivateKey) {
        this.userId = userId;
        this.aesKey = aesKey;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public int getUserId() {
        return userId;
    }

    public SecretKey getAesKey() {
        return aesKey;
    }

    public PublicKey getRsaPublicKey() {
        return rsaPublicKey;
    }

    public PrivateKey getRsaPrivateKey() {
        return rsaPrivateKey;
    }
}