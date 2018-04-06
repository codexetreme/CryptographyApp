package gears.com.lab_project;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import gears.com.lab_project.FileSecure.FileSecure;
import gears.com.lab_project.FileSecure.DataFileListener;

public class EditorActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    Context mContext;
    NumberPicker numberPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editText = findViewById(R.id.editxt_data);
        button = findViewById(R.id.btn_encrypt);
        numberPicker = findViewById(R.id.numPick);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(3);
        numberPicker.setValue(3);
        numberPicker.setWrapSelectorWheel(false);
        mContext = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editText.getText().toString())){
                    FileSecure.NumAuthors = numberPicker.getValue();
                    FileSecure.Encrypt(editText.getText().toString());
                    Toast.makeText(mContext,"Done!",Toast.LENGTH_SHORT).show();
                    FileSecure.ClearFiles();
                    finish();
                }
            }
        });
    }


}
