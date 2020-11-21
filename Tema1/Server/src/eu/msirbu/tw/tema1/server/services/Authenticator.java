package eu.msirbu.tw.tema1.server.services;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Authenticator {

    private SecretKey AESKey;
    private byte[] iv;
    private static final int LEN_GCM_IV_BYTES = 12;
    private static final int LEN_GCM_TAG_BITS = 128;
    private static final int LEN_KEY_BITS = 128;

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
     * Generates an user token by encrypting the username using AES-128.
     *
     * @param username The username to generate the authentication token from.
     * @return The generated authentication token.
     * **/
    public String generateUserToken(String username) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec paramSpec = new GCMParameterSpec(LEN_GCM_TAG_BITS, iv);
        cipher.init(Cipher.ENCRYPT_MODE, AESKey, paramSpec);
        byte[] decryptedBytes = username.getBytes(UTF_8);
        byte[] token = new byte[LEN_GCM_IV_BYTES + cipher.getOutputSize(decryptedBytes.length)];
        System.arraycopy(iv, 0, token, 0, LEN_GCM_IV_BYTES);
        cipher.doFinal(decryptedBytes, 0, decryptedBytes.length, token, LEN_GCM_IV_BYTES);
        String encodedToken = Base64.getEncoder().encodeToString(token);
        return encodedToken;
    }

    /**
     *
     * @param encodedToken
     * @return
     */
    public String getUserNameFromToken(String encodedToken) throws GeneralSecurityException, IllegalArgumentException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] token = Base64.getDecoder().decode(encodedToken);
        GCMParameterSpec paramSpec = new GCMParameterSpec(LEN_GCM_TAG_BITS, token, 0, LEN_GCM_IV_BYTES);
        cipher.init(Cipher.DECRYPT_MODE, AESKey, paramSpec);
        byte[] decryptedBytes = cipher.doFinal(token, LEN_GCM_IV_BYTES, token.length - LEN_GCM_IV_BYTES);
        String username = new String(decryptedBytes, UTF_8);
        return username;
    }

}
