package com.yunkairichu.cike.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.easemob.util.ImageUtils;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.utils.ImageCache;
import com.yunkairichu.cike.widget.PhotoView;

/**
 * Created by vida2009 on 2015/5/21.
 */
public class LoadLocalBigImgTask extends AsyncTask<Void, Void, Bitmap> {
    private ProgressBar pb;
    private PhotoView photoView;
    private String path;
    private int width;
    private int height;
    private Context context;

    public LoadLocalBigImgTask(Context context,String path, PhotoView photoView,
                               ProgressBar pb, int width, int height) {
        this.context = context;
        this.path = path;
        this.photoView = photoView;
        this.pb = pb;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        int degree = ImageUtils.readPictureDegree(path);
        if (degree != 0) {
            if(pb!=null)pb.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.INVISIBLE);
        } else {
            if(pb!=null)pb.setVisibility(View.INVISIBLE);
            photoView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = ImageUtils.decodeScaleImage(path, width, height);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if(pb!=null)pb.setVisibility(View.INVISIBLE);
        photoView.setVisibility(View.VISIBLE);
        if (result != null)
            ImageCache.getInstance().put(path, result);
        else
//            result = BitmapFactory.decodeResource(context.getResources(),
//                    R.drawable.signin_local_gallry);
            result = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_image_gray);
        photoView.setImageBitmap(result);
    }
}
