package gears.com.lab_project.FileSecure;



import android.util.Base64;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Yashodhan on 01-Apr-18, for project_crypto_app
 */

public class Encryption {


    private static String password = "test";
    private static String salt;
    //private static int pswdIterations = 65536  ;
    private static int pswdIterations = 1000  ;
    private static int keySize = 256;
    private static int saltlength = keySize / 8;
    private static byte[] ivBytes;

    private static int Length = 0;

    // Methods
    public static String encrypt(String plainText) throws Exception {

        //get salt
        salt = generateSalt();
        byte[] saltBytes = salt.getBytes("UTF-8");

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                keySize
        );

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        //encrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
        byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));

        //String encodedText = Base64.encodeToString(encryptedTextBytes, Base64.DEFAULT);
        String encodedText = Base64.encodeToString(encryptedTextBytes,Base64.DEFAULT);
        String encodedIV = Base64.encodeToString(ivBytes,Base64.DEFAULT);
        String encodedSalt = Base64.encodeToString(saltBytes,Base64.DEFAULT);
        String encodedPackage = encodedSalt + "]" + encodedIV + "]" + encodedText;
        Length = encodedPackage.length();
        return encodedPackage;
    }

    public static String decrypt(String encryptedText) throws Exception {

        String[] fields = encryptedText.split("]");
        byte[] saltBytes = Base64.decode(fields[0],Base64.DEFAULT);
        ivBytes = Base64.decode(fields[1],Base64.DEFAULT);
        byte[] encryptedTextBytes = Base64.decode(fields[2],Base64.DEFAULT);

        // Derive the key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                pswdIterations,
                keySize
        );

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        // Decrypt the message
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));


        byte[] decryptedTextBytes = null;
        try {
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        assert decryptedTextBytes != null;
        return new String(decryptedTextBytes);
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[saltlength];
        random.nextBytes(bytes);
        return new String(bytes);
    }

    public static int getLength() throws Exception{
        if(Length >0){
            return Length;
        }
        else throw new Exception("Error, no encryption done");

    }

    public static void setPassword(String password) {
        Encryption.password = password;
    }

}
