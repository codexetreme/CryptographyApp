package gears.com.lab_project.FileSecure;

import android.app.LoaderManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import gears.com.lab_project.SharedKey;

/**
 * Created by Yashodhan on 01-Apr-18, for project_crypto_app
 */

public class Sharing {

    ShareSelectedFileListener listener;
    public Sharing(ShareSelectedFileListener listener){
        this.listener = listener;
    }
    public void Share(File f){
        if (listener != null) {
            listener.ShareSelectedFile(f);
        }
    }
    public void TriggerDelete(final SharedKey k){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                k.getFileHandle().delete();
                listener.UpdateList(k);
                Log.d("HELLO","Gone");
            }
        }, 1000*20);
    }

}
