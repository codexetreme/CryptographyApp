package gears.com.lab_project;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Yashodhan on 01-Apr-18, for project_crypto_app
 */

public class SharedKey {

    private File f;
    private String filePath;
    private String FileName;
    private Bitmap qrCode;
    public SharedKey(File f, String fileName,Bitmap bitmap) {
        this.f = f;
        FileName = fileName;
        qrCode = bitmap;
    }

    public String getFileName() {
        return FileName;
    }

    public File getFileHandle() {
        return f;
    }

    public String getFilePath() {
        return f.getAbsolutePath();
    }

    public Bitmap getQrCode() {
        return qrCode;
    }
}
