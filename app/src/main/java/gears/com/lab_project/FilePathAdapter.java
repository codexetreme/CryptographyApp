package gears.com.lab_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Yashodhan on 01-Apr-18, for project_crypto_app
 */

public class FilePathAdapter extends ArrayAdapter<SharedKey> {
    Context mContext;
    int res;
    public FilePathAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mContext = context;
        res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SharedKey k = getItem(position);
        convertView = LayoutInflater.from(mContext).inflate(res,parent,false);
        TextView tv = convertView.findViewById(R.id.tv_fileName);
        ImageView imageView = convertView.findViewById(R.id.imgView_QR);
        tv.setText(k.getFileName());
        imageView.setImageBitmap(k.getQrCode());
        return convertView;
    }
}
