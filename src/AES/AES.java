package AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AES {
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int DATA_LENGTH = 128;
    private static Cipher encryptionCipher;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public SecretKey init() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        key = keyGenerator.generateKey();

        return key;
    }
    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static String encrypt(String data,  SecretKey key) throws Exception {
        //Initialize the cipher for encryption
        encryptionCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        //data to be encrypted
        byte[] text = data.getBytes();
        // Encrypt the data
        byte[] textEncrypted = encryptionCipher.doFinal(text);
        return(new String(textEncrypted, UTF_8));
    }

    public static String decrypt(String encryptedData,  SecretKey key) throws Exception {
        // Initialize the same cipher for decrypting data
        byte[] dataInBytes = decode(encryptedData);
        encryptionCipher.init(Cipher.DECRYPT_MODE, key);
        // Decrypt the data
        byte[] textDecrypted = encryptionCipher.doFinal(dataInBytes);
        return(new String(textDecrypted));
    }
}
