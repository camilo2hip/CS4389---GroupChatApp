package RSA;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {
public static PublicKey getPublicKey(String base64PublicKey){
        PublicKey publicKey = null;
        try{
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
        } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        } catch (InvalidKeySpecException e) {
        e.printStackTrace();
        }
        return publicKey;
        }

public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
        keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        }
        try {
        privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
        e.printStackTrace();
        }
        return privateKey;
        }

public static byte[] encrypt(String data, String publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
        }

public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
        }

public static String decrypt(String data, String base64PrivateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
        }

public static void main(String[] args) throws IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, BadPaddingException {
        try {
                String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNxyHpCmgBfxJmeJOnrd8vmfhUo6Y2ulqwyUnIJ8LH/FHS8XiBn0acUUnSpDUmBkuah58eyLIbGVmyteeRHZBLhCU9jC3vybvfALkpmorPAKbza1qpn7MnMn+wUuHXD5LKsxlPPhyB6LSQuWxF2jOAgWvOCi/iuRE0b1rSPeiOMwIDAQAB";
                String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAM3HIekKaAF/EmZ4k6et3y+Z+FSjpja6WrDJScgnwsf8UdLxeIGfRpxRSdKkNSYGS5qHnx7IshsZWbK155EdkEuEJT2MLe/Ju98AuSmais8ApvNrWqmfsycyf7BS4dcPksqzGU8+HIHotJC5bEXaM4CBa84KL+K5ETRvWtI96I4zAgMBAAECgYA348YKDZGCFolg247/E/Jyc1dHZctXQfYv3fv0KRh2SMnQiVU5n5EGE+4BECh/U2ZlakCFk+0L/y6lo1Jpz6XI356VVGMyo2r/wS3gi8fq1p/Me23bT49qkJsVHxJeLnP8d3HH2DufO6kRaj/WtjGPPkHWW76m2BGGt/5+A5fyAQJBAM8x5t6pqSfhpMzdFUQo7ItYuQY40vIwufvftRp85adBS8YTEgmFE9Lkgv0hB6uObo19tTgSGsnNQ9q68OhHbCECQQD+P8eeyCxN5TokeellyxDYyo8lIJdRp8oQOuEvxb6n6cnHMjNljjuzb3LSe1KTUKffqiW96NFjPJf3JxmRJI/TAkA4tF+K8MPqtkZ0Cs2XKdwTBuUcXtDcl5lO5Zqa9TOk4qnqO63kDRevz/pJbJC80u5Oqui9v7a1JAg+BIuKBoIBAkAXBFUhBcQlBSR/Wt4LThfnWGcfGFU6mjMLxxjNx1wcPWj79Ip8niS/eM5vSaTPG1UnRXMHP0V9c2XahRqmbiXrAkEAgyJAiDWmfCOzYpwzm3Z94t/Jt8HDN3VCEQj9yYXE4qvVj62Zlk2FtWwihmHl5uLLZVaECR33vEuquDXIfyq6JA==";

                String encryptedString = Base64.getEncoder().encodeToString(encrypt("Samuel Osezua was here!", publicKey));
        System.out.println(encryptedString);

                String decryptedString = RSAUtil.decrypt(encryptedString, privateKey);
        System.out.println(decryptedString);
        } catch (NoSuchAlgorithmException e) {
        System.err.println(e.getMessage());
        }

        }
        }