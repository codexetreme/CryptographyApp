package gears.com.lab_project.FileSecure;

/**
 * Created by Yashodhan on 01-Apr-18, for project_crypto_app
 */

public interface DataFileListener {
    void SaveFile(String filepath, String mainKey);
    String RetrieveFile(String filepath);
}
