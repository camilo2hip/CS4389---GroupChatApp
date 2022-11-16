package AES;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class AES {
    private SecretKey key;

    public SecretKey init(String secretKey, String saltValue) throws InvalidKeySpecException, NoSuchAlgorithmException {
        try {
            /* Create factory for secret keys. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            /* PBEKeySpec class implements KeySpec interface. */
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), saltValue.getBytes(), 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);

            return tmp;
        }
    catch (InvalidKeySpecException | NoSuchAlgorithmException e)
    {
        e.printStackTrace();
        return null;
    }
    }


    /* Encryption Method */
    public static String encrypt(String strToEncrypt, SecretKey key) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        try {
            /* Declare a byte array. */
            byte[] iv = {0, 1, 3, 7, 9, 2, 5, 3, 2, 7, 1, 6, 0, 2, 2, 1};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = new SecretKeySpec(key.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            /* Returns encrypted value. */
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println("Error occurred during encryption: " + e);
        }
        return null;
    }

    /* Decryption Method */
    public static String decrypt(String strToDecrypt, SecretKey key) {
        try {
            /* Declare a byte array. */
            byte[] iv = {0, 1, 3, 7, 9, 2, 5, 3, 2, 7, 1, 6, 0, 2, 2, 1};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = new SecretKeySpec(key.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            /* Retruns decrypted value. */
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            System.out.println("Error occurred during decryption: " + e);
        }
        return null;
    }

    /* Driver Code */
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        //Main Function to test AES


        String key = "CS 4389";
        String salt = "Professor";
        AES aes = new AES();
        SecretKey key1 = aes.init(key,salt);

        String secretKeyString = AESUtil.convertSecretKeyToString(key1);
        SecretKey key2 = AESUtil.convertStringToSecretKeyto(secretKeyString);

        System.out.println("This is the Secretkey: " + key1);


        /* Message to be encrypted. */
        String originalValue = "Code testing";

        /* Call the encrypt() method and store result of encryption. */
        String encryptedValue = encrypt(originalValue, key1);

        /* Call the decrypt() method and store result of decryption. */
        String decryptedValue = decrypt(encryptedValue, key1);
        String dval = decrypt(encryptedValue, key2);

        /* Display the original message, encrypted message and decrypted message on the console. */
        System.out.println("Original value: " + originalValue);
        System.out.println("Encrypted value: " + encryptedValue);
        System.out.println("Decrypted value: " + decryptedValue);
        System.out.println("Decrypted value with String to Key: " + dval);
    }
}
