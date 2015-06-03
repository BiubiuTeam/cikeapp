package com.yunkairichu.cike.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
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
 * Created by vida2009 on 2015/5/26.
 */
public class ToolShowBigImage {
    private ProgressDialog pd;
    private PhotoView image;
    private int default_res = R.drawable.default_image_gray;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private ProgressBar loadLocalPb;
//    private BigImageOnTouchListener bigImageOnTouchListener = new BigImageOnTouchListener();
    private Activity activity;

    public ToolShowBigImage(Context context){
        activity = (Activity)context;
        image = (PhotoView) activity.findViewById(R.id.image2);
        loadLocalPb = (ProgressBar) activity.findViewById(R.id.pb_load_local);
        //image.setVisibility(View.GONE);
    }

    public void start_show_photo(Uri para_uri, String para_remotepath, String para_secret){
        //default_res = getIntent().getIntExtra("default_image_gray", R.drawable.default_avatar);
        Uri uri = para_uri;
        String remotepath = para_remotepath;
        String secret = para_secret;
        System.err.println("show big image uri:" + uri + " remotepath:" + remotepath);

        //���ش��ڣ�ֱ����ʾ���ص�ͼƬ
        if (uri != null && new File(uri.getPath()).exists()) {
            System.err.println("showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = ImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                LoadLocalBigImgTask task = new LoadLocalBigImgTask(activity, uri.getPath(), image, loadLocalPb, ImageUtils.SCALE_IMAGE_WIDTH,
                        ImageUtils.SCALE_IMAGE_HEIGHT);
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }
            } else {
                image.setImageBitmap(bitmap);
            }
        } else if (remotepath != null) { //ȥ����������ͼƬ
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
//                finish();
                v.setVisibility(View.GONE);
            }
        });

        image.setFocusable(true);
        image.setFocusableInTouchMode(true);
        image.requestFocus();
        image.setVisibility(View.VISIBLE);
//        image.setOnTouchListener(bigImageOnTouchListener);
//        ThreadPointerEvent threadPointerEvent = new ThreadPointerEvent();
//        new Thread(threadPointerEvent).start();
    }

    /**
     * ͨ��Զ��URL��ȷ���±������غ��localurl
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
     * ����ͼƬ
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = activity.getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(activity);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        localFilePath = getLocalFilePath(remoteFilePath);
        final HttpFileManager httpFileMgr = new HttpFileManager(activity, EMChatConfig.getInstance().getStorageUrl());
        final CloudOperationCallback callback = new CloudOperationCallback() {
            public void onSuccess(String resultMsg) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        image.setImageResource(default_res);
                    }
                });
            }

            public void onProgress(final int progress) {
                Log.d("ease", "Progress: " + progress);
                final String str2 = activity.getResources().getString(R.string.Download_the_pictures_new);
                activity.runOnUiThread(new Runnable() {
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

    public void gonePhotoView(){
        image.setVisibility(View.GONE);
    }

//    @Override
//    public void onBackPressed() {
//        if (isDownloaded)
//            setResult(RESULT_OK);
//        finish();
//    }

//    class BigImageOnTouchListener implements View.OnTouchListener {
//        @Override
//        public boolean onTouch(View view, MotionEvent event) {
//            ThreadPointerEvent threadPointerEvent;
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    ToolLog.dbg("hello");
//                    break;
//                case MotionEvent.ACTION_UP:
//                    ToolLog.dbg("hello up");
//                    setResult(RESULT_OK);
//                    finish();
//                    break;
//                case MotionEvent.ACTION_CANCEL:
//                    threadPointerEvent = new ThreadPointerEvent();
//                    new Thread(threadPointerEvent).start();
//                    ToolLog.dbg("hello CAN");
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    ToolLog.dbg("hello move");
//                    break;
//                default:
//                    ToolLog.dbg("hello oth");
//                    break;
//            }
//            return true;
//        }
//    }

//    private class ThreadPointerEvent implements Runnable {
//        public void run() {
//            //ģ�ⰴ���¼�
//            Instrumentation mInst = new Instrumentation();
//            mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
//                    SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 200, 200, 0));
//        }
//    }
}
