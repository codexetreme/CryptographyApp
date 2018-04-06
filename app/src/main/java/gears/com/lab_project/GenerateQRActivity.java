package gears.com.lab_project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import gears.com.lab_project.FileSecure.FileSecure;
import gears.com.lab_project.FileSecure.ShareSelectedFileListener;
import gears.com.lab_project.FileSecure.Sharing;

public class GenerateQRActivity extends AppCompatActivity implements ShareSelectedFileListener {

    ListView listView;
    private FilePathAdapter adapter;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        listView = findViewById(R.id.list_filenames);
        adapter = new FilePathAdapter(this,R.layout.list_item);
        listView.setAdapter(adapter);
        mContext = this;
        final Sharing sharing = new Sharing(this);
        FileSecure.QRFiles.clear();
        FileSecure.GenerateQR();
        adapter.addAll(FileSecure.QRFiles);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                sharing.Share(FileSecure.QRFiles.get(position).getFileHandle());
                Toast.makeText(mContext,"WARNING!, file will be deleted in 20 seconds!",Toast.LENGTH_LONG).show();
                sharing.TriggerDelete(FileSecure.QRFiles.get(position));
            }
        });
    }

    @Override
    public void ShareSelectedFile(File f) {

        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("image/jpg")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));
        intent.putExtra(Intent.EXTRA_SUBJECT,"Sending Key");
        startActivity(Intent.createChooser(intent,"Share File"));
    }

    @Override
    public void UpdateList(SharedKey k) {
        adapter.clear();
        FileSecure.QRFiles.remove(k);
        adapter.addAll(FileSecure.QRFiles);
        adapter.notifyDataSetChanged();
    }
}