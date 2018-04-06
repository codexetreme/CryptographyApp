package gears.com.lab_project.FileSecure;

import java.io.File;

import gears.com.lab_project.SharedKey;

/**
 * Created by Yashodhan on 01-Apr-18, for project_crypto_app
 */

public interface ShareSelectedFileListener {

    void ShareSelectedFile(File f);
    void UpdateList(SharedKey k);
}
