package gears.com.lab_project;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import gears.com.lab_project.FileSecure.FileSecure;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeBitmapScanner;
import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class UnlockActivity extends AppCompatActivity implements BarcodeRetriever, View.OnClickListener{

    TextView unlockTV;
    BarcodeCapture barcodeCapture;
    FrameLayout frameLayout, frameLayout_img;
    ProgressBar progressBar;
    Button scanCameraBtn,selectImageBtn,ScanImageBtn,UnlockBtn;
    ImageView imageView;
    int counter = 0;
    private Context mContext;

    private boolean selectedImage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        mContext = this;

        unlockTV = findViewById(R.id.unlock_tv);
        frameLayout = findViewById(R.id.frame_layout);
        frameLayout_img = findViewById(R.id.frame_layout_image);
        imageView = findViewById(R.id.qr_img);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setMax(FileSecure.NumAuthors);
        progressBar.setIndeterminate(false);
        scanCameraBtn = findViewById(R.id.scan_btn);
        selectImageBtn = findViewById(R.id.select_img_btn);
        ScanImageBtn = findViewById(R.id.scan_img_btn);
        UnlockBtn = findViewById(R.id.btn_unlock);
        UnlockBtn.setOnClickListener(this);
        scanCameraBtn.setOnClickListener(this);
        ScanImageBtn.setOnClickListener(this);
        selectImageBtn.setOnClickListener(this);
        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
        barcodeCapture.setRetrieval(this);
        barcodeCapture.setShowDrawRect(true);
    }

    void CheckAndAdd(String data){

        byte[] temp = Base64.decode(data,Base64.DEFAULT);
        if(!FileSecure.contains(FileSecure.collectedKeys,temp)){
            progressBar.setProgress(++counter);

            FileSecure.collectedKeys.add(temp);
        }

    }

    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d("HELLO", "Barcode read: " + barcode.displayValue);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(UnlockActivity.this)
                        .setTitle("code retrieved")
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                frameLayout.setVisibility(View.GONE);
                                CheckAndAdd(barcode.displayValue);
                            }
                        })
                        .setMessage(barcode.displayValue);
                builder.show();
        //        barcodeCapture.stopScanning();
            }
        });
    }

    @Override
    public void onRetrievedMultiple(Barcode barcode, List<BarcodeGraphic> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        Barcode barcode = null;
        for (int i = 0; i < sparseArray.size(); i++) {
            barcode = sparseArray.valueAt(i);
            if(barcode != null)
                Log.d("HELLO","Barcode Read : " + barcode.displayValue);
        }
        if (barcode != null) {
            CheckAndAdd(barcode.displayValue);
        }
    }

    @Override
    public void onRetrievedFailed(String s) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }

    final Bitmap[] bitmap = new Bitmap[1];

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.scan_btn){

            frameLayout.setVisibility(View.VISIBLE);
        }

        else if(view.getId() == R.id.select_img_btn){
            final String[] files = FileSecure.GetFiles();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Choose QR File");
            builder.setItems(files, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (files != null) {
                        bitmap[0] = BitmapFactory.decodeFile(files[which]);
                        imageView.setImageBitmap(bitmap[0]);
                        selectedImage = true;
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if(view.getId() == R.id.scan_img_btn){
            if(selectedImage){
                BarcodeBitmapScanner.scanBitmap(mContext, bitmap[0],Barcode.ALL_FORMATS,this);
            }
        }
        else if(view.getId()==R.id.btn_unlock){
            try {
                String message = FileSecure.Decrypt();
                unlockTV.setText("Deciphered Text : " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
