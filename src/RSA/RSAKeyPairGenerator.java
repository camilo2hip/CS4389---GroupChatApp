package RSA;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import static AES.AESUtil.convertSecretKeyToString;
import static AES.AESUtil.getKeyFromPassword;

public class RSAKeyPairGenerator {

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSAKeyPairGenerator() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }


    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {

        //Write the AES key to a file encoded in Base 64
        byte[] encodedBytes = convertSecretKeyToString(getKeyFromPassword("KEY", "pseudo")).getBytes();
        System.out.println("encodedBytes " + new String(encodedBytes));
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        keyPairGenerator.writeToFile("AES/key",encodedBytes);





//        keyPairGenerator.writeToFile("RSA/publicKey", keyPairGenerator.getPublicKey().getEncoded());
//        keyPairGenerator.writeToFile("RSA/privateKey", keyPairGenerator.getPrivateKey().getEncoded());
//        System.out.println("PUBLIC KEY: "+ Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded()));
//        System.out.println("PRIVATE KEY: "+ Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded()));
    }
}