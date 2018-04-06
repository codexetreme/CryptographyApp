package gears.com.lab_project.FileSecure;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import gears.com.lab_project.SharedKey;

import static android.content.ContentValues.TAG;


/**
 * Created by Yashodhan on 31-Mar-18, for project_crypto_app
 */

public class FileSecure {

    public static int NumAuthors;
    private static String FileStorageLocation;
    private static byte[] MainKey;
    private static String Cipher;
    private static int keyLength;
    private static List<byte[]> distributableKeys;
    public static List<SharedKey> QRFiles;
    private static DataFileListener listener;
    private static Context mContext;

    public static List<byte[]> collectedKeys = new ArrayList<>();

    public static void set(int numAuthors, String fileStorageLocation, Context context){
        NumAuthors = numAuthors;
        FileStorageLocation = fileStorageLocation;
        QRFiles = new ArrayList<>();
        mContext = context;
    }

    public static void setOnDataFileListener(DataFileListener listener){
        FileSecure.listener = listener;
    }
    public static void Encrypt(String data){

        RandomString gen = new RandomString(8);
        String password = gen.nextString();

        Encryption.setPassword(password);
        MainKey = password.getBytes(Charset.forName("UTF-8"));
        if(NumAuthors%2==0){
            NumAuthors +=1;
        }

        try {
            Cipher = Encryption.encrypt(data);
            keyLength = password.length();
            listener.SaveFile(FileStorageLocation,Cipher);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void GenerateQR(){
        GenerateUserDistributionKeys();
        for (int i = 0; i < distributableKeys.size(); i++) {
            String fileName = "key_part_" + String.valueOf(i)+"_";
            String qr_data = Base64.encodeToString(distributableKeys.get(i),Base64.DEFAULT);
            Log.d("HELLO","QR_String : " + String.valueOf(qr_data.length()));
            Bitmap qr = QRCode.from(qr_data).withSize(300,300).bitmap();
            File file = storeImage(qr, fileName);
            SharedKey k = new SharedKey(file,fileName,qr);
            QRFiles.add(k);
        }
    }

    private static File storeImage(Bitmap image, String fileName) {
        File pictureFile = getOutputMediaFile(fileName);
        if (pictureFile == null) {
            Log.d("HELLO",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return null;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        return pictureFile;
    }

    private static File getOutputMediaFile(String fileName){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }

    public static boolean ClearFiles() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName()
                + "/Files");
        if (!mediaStorageDir.exists()) {
            return false;
        }
        File[] files = mediaStorageDir.listFiles();
        String[] f = new String[files.length];
        boolean k = false;
        for (int i = 0; i < files.length; i++) {
            k = files[i].delete();
        }
        return k;
    }

    public static String[] GetFiles(){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName()
                + "/Files");
        if (! mediaStorageDir.exists()){
            return null;
        }
        File[] files = mediaStorageDir.listFiles();
        String[] f = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            f[i] = files[i].getAbsolutePath();
        }
        return f;
    }

    private static byte[] makeKey(int length) {

        byte[] bytes = new byte[length];

        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    private static void GenerateUserDistributionKeys(){
        List<byte[]> keys = new ArrayList<>();
        for(int i=0; i<NumAuthors; i++){
            keys.add(i, makeKey(keyLength));
        }
        distributableKeys = new ArrayList<>();
        for (int i = 0; i < NumAuthors; i++) {
            byte[] temp = XOR(keys.get(i), keys.get((i + 1) % NumAuthors));
            distributableKeys.add(i,XOR(temp,MainKey));
        }
    }

    private static byte[] XOR(byte[] k1, byte[] k2){
        byte[] bytes = new byte[k1.length];
        for (int i = 0; i < k1.length; i++) {
            int xored = k1[i] ^ k2[i];
            bytes[i] = (byte) xored;
        }
        return bytes;
    }


    public static boolean contains(List<byte[]> arrays, byte[] other) {
        for (byte[] b : arrays)
            if (Arrays.equals(b, other)) return true;
        return false;
    }

    public static String Decrypt() throws Exception{
        String cipher = listener.RetrieveFile(FileStorageLocation);
        if(collectedKeys.size()==NumAuthors){
            byte[] temp;
            temp = XOR(collectedKeys.get(0),collectedKeys.get(1));
            for (int i = 2; i < collectedKeys.size(); i++) {
                temp = XOR(temp,collectedKeys.get(i));
            }
            String password = new String(temp,Charset.forName("UTF-8"));
            Encryption.setPassword(password);
            String message = Encryption.decrypt(cipher);
            Log.d("HELLO",message);
            return message;

        }
        else{
            throw new Exception("Not enough keys to decrypt Cipher!");
        }
    }
}
