package gears.com.lab_project;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import gears.com.lab_project.FileSecure.DataFileListener;
import gears.com.lab_project.FileSecure.FileSecure;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DataFileListener {

    Button EditorBtn,GenQRBtn,UnlockBtn;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        EditorBtn   = findViewById(R.id.btn_editor);
        GenQRBtn    = findViewById(R.id.btn_genQR);
        UnlockBtn   = findViewById(R.id.btn_unlock);
        EditorBtn.setOnClickListener(this);
        GenQRBtn.setOnClickListener(this);
        UnlockBtn.setOnClickListener(this);
        FileSecure.setOnDataFileListener(this);
        FileSecure.set(3,"cipher",this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.clearFiles){
            FileSecure.ClearFiles();
            Toast.makeText(mContext,"Keys cleared!",Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_editor:
                startActivity(new Intent(MainActivity.this,EditorActivity.class));
                break;
            case R.id.btn_genQR:
                startActivity(new Intent(MainActivity.this,GenerateQRActivity.class));
                break;
            case R.id.btn_unlock:
                startActivity(new Intent(MainActivity.this,UnlockActivity.class));
                break;
        }
    }


    @Override
    public void SaveFile(String filepath, String Cipher) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filepath, Context.MODE_PRIVATE);
            outputStream.write(Cipher.getBytes(Charset.forName("UTF-8")));
            Log.d("HELLO","Cipher: " + Cipher);
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String RetrieveFile(String filepath) {
        FileInputStream fileInputStream;
        StringBuilder sb = new StringBuilder();
        try {
            fileInputStream = openFileInput(filepath);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
