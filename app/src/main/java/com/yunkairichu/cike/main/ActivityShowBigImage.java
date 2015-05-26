package com.yunkairichu.cike.main;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.easemob.chat.EMChatConfig;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;
import com.yunkairichu.cike.task.LoadLocalBigImgTask;
import com.yunkairichu.cike.utils.ImageCache;
import com.yunkairichu.cike.widget.PhotoView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vida2009 on 2015/5/21.
 */
public class ActivityShowBigImage extends BaseActivity {
    private ProgressDialog pd;
    private PhotoView image;
    private int default_res = R.drawable.default_image;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private ProgressBar loadLocalPb;
    private BigImageOnTouchListener bigImageOnTouchListener = new BigImageOnTouchListener();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.view_photo);
        super.onCreate(savedInstanceState);

        image = (PhotoView) findViewById(R.id.image);

        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        //default_res = getIntent().getIntExtra("default_image", R.drawable.default_avatar);
        default_res = getIntent().getIntExtra("default_image", R.drawable.default_image);
        Uri uri = getIntent().getParcelableExtra("uri");
        String remotepath = getIntent().getExtras().getString("remotepath");
        String secret = getIntent().getExtras().getString("secret");
        System.err.println("show big image uri:" + uri + " remotepath:" + remotepath);

        //本地存在，直接显示本地的图片
        if (uri != null && new File(uri.getPath()).exists()) {
            System.err.println("showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = ImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                LoadLocalBigImgTask task = new LoadLocalBigImgTask(this, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (remotepath != null) { //去服务器下载图片
            System.err.println("download remote image");
            Map<String, String> maps = new HashMap<String, String>();
            if (!TextUtils.isEmpty(secret)) {
                maps.put("share-secret", secret);
            }
            downloadImage(remotepath, maps);
        } else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image.setFocusable(true);
        image.setFocusableInTouchMode(true);
        image.requestFocus();
        image.setOnTouchListener(bigImageOnTouchListener);
        ThreadPointerEvent threadPointerEvent = new ThreadPointerEvent();
        new Thread(threadPointerEvent).start();
    }

    /**
     * 通过远程URL，确定下本地下载后的localurl
     * @param remoteUrl
     * @return
     */
    public String getLocalFilePath(String remoteUrl){
        String localPath;
        if (remoteUrl.contains("/")){
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
                    + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
        }else{
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
        }
        return localPath;
    }

    /**
     * 下载图片
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        localFilePath = getLocalFilePath(remoteFilePath);
        final HttpFileManager httpFileMgr = new HttpFileManager(this, EMChatConfig.getInstance().getStorageUrl());
        final CloudOperationCallback callback = new CloudOperationCallback() {
            public void onSuccess(String resultMsg) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                        if (bitmap == null) {
                            image.setImageResource(default_res);
                        } else {
                            image.setImageBitmap(bitmap);
                            ImageCache.getInstance().put(localFilePath, bitmap);
                            isDownloaded = true;
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
            }

            public void onError(String msg) {
                Log.e("###", "offline file transfer error:" + msg);
                File file = new File(localFilePath);
                if (file.exists()&&file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        image.setImageResource(default_res);
                    }
                });
            }

            public void onProgress(final int progress) {
                Log.d("ease", "Progress: " + progress);
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                httpFileMgr.downloadFile(remoteFilePath, localFilePath, headers, callback);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }

    class BigImageOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            ThreadPointerEvent threadPointerEvent;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ToolLog.dbg("hello");
                    break;
                case MotionEvent.ACTION_UP:
                    ToolLog.dbg("hello up");
                    setResult(RESULT_OK);
                    finish();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    threadPointerEvent = new ThreadPointerEvent();
                    new Thread(threadPointerEvent).start();
                    ToolLog.dbg("hello CAN");
                    break;
                case MotionEvent.ACTION_MOVE:
                    ToolLog.dbg("hello move");
                    break;
                default:
                    ToolLog.dbg("hello oth");
                    break;
            }
            return true;
        }
    }

    private class ThreadPointerEvent implements Runnable {
        public void run() {
            //模拟按下事件
            Instrumentation mInst = new Instrumentation();
            mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 200, 200, 0));
        }
    }
}
