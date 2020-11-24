package eu.msirbu.tw.tema1.server.services;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Authenticator {

    /** The key required for symmetric encryption (AES) **/
    private SecretKey AESKey;
    /** The initialization vector required by the Galois/Counter operation mode (GCM) of AES **/
    private byte[] iv;
    /** The length of the initialization vector. **/
    private static final int LEN_GCM_IV_BYTES = 12;
    /** The length of the authentication tag required by the GCM of AES **/
    private static final int LEN_GCM_TAG_BITS = 128;
    /** The length of the key **/
    private static final int LEN_KEY_BITS = 128;

    /**
     * Constructor
     */
    public Authenticator() {
        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(LEN_KEY_BITS, secureRandom);
            AESKey = gen.generateKey();
            iv = new byte[LEN_GCM_IV_BYTES];
            secureRandom.nextBytes(iv);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Generates an user token by encrypting the user name.
     *
     * @param username The username to generate the authentication token from.
     * @return The generated authentication token, encoded in Base64 and encrypted with AES-128.
     * **/
    public String generateUserToken(String username) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec paramSpec = new GCMParameterSpec(LEN_GCM_TAG_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, AESKey, paramSpec);
        byte[] decryptedBytes = username.getBytes(UTF_8);
        byte[] token = new byte[LEN_GCM_IV_BYTES + cipher.getOutputSize(decryptedBytes.length)];
        System.arraycopy(iv, 0, token, 0, LEN_GCM_IV_BYTES);
        cipher.doFinal(decryptedBytes, 0, decryptedBytes.length, token, LEN_GCM_IV_BYTES);
        return Base64.getEncoder().encodeToString(token);
    }

    /**
     * Decrypts the authentication token and retrieves the user name.
     *
     * @param encodedToken The token, encoded in Base64 and encrypted with AES-128.
     * @return The username itself.
     */
    public String getUserNameFromToken(String encodedToken) throws GeneralSecurityException, IllegalArgumentException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] token = Base64.getDecoder().decode(encodedToken);
        GCMParameterSpec paramSpec = new GCMParameterSpec(LEN_GCM_TAG_BITS, token, 0, LEN_GCM_IV_BYTES);
        cipher.init(Cipher.DECRYPT_MODE, AESKey, paramSpec);
        byte[] decryptedBytes = cipher.doFinal(token, LEN_GCM_IV_BYTES, token.length - LEN_GCM_IV_BYTES);
        return new String(decryptedBytes, UTF_8);
    }

}
