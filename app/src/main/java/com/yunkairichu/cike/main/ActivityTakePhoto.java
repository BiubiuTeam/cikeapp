package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.easemob.util.PathUtil;
import com.jaf.jcore.Application;
import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.jaf.jcore.ToolGetLocationInfo;
import com.umeng.analytics.MobclickAgent;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponsePublishTitle;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vida2009 on 2015/5/27.
 */
public class ActivityTakePhoto extends Activity implements SurfaceHolder.Callback{
    //常量
    public static final int TITLE_TYPE_PICTURE = 3;

    public static final int REQUEST_CODE_LOCAL = 19;

    public static final int RESULT_FORCE_REFLASH = 101;

    //变量
    private ImageView back, chanCam;//返回和切换前后置摄像头
    private SurfaceView surface;
    private ImageView shutter;//快门
    private SurfaceHolder holder;
    private EditText picText;
    private TextView picLastText;
    private ImageView fromFile;
    private ImageView photoFromFile;
    private Camera camera;//声明相机
    private String filepath = "";//照片保存路径
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    private int msgTag = -1;
    private ResponsePublishTitle responsePublishTitle;
    private String sendPicFromFilePath = "";
    private int captureOrFromFileFlag = 0;
    private Bitmap fromFileBitmap = null;
    private Layout_take_photo layout_take_photo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        Bundle bundle = this.getIntent().getExtras();
        msgTag = bundle.getInt("msgTag", 0);

        if(msgTag==9) cameraPosition = 0;

        requestWindowFeature(Window.FEATURE_NO_TITLE);//没有标题
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        //设置手机屏幕朝向，一共有7种
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //SCREEN_ORIENTATION_BEHIND： 继承Activity堆栈中当前Activity下面的那个Activity的方向
        //SCREEN_ORIENTATION_LANDSCAPE： 横屏(风景照) ，显示时宽度大于高度
        //SCREEN_ORIENTATION_PORTRAIT： 竖屏 (肖像照) ， 显示时高度大于宽度
        //SCREEN_ORIENTATION_SENSOR  由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化
        //SCREEN_ORIENTATION_NOSENSOR： 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)
        //SCREEN_ORIENTATION_UNSPECIFIED： 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择
        //SCREEN_ORIENTATION_USER： 用户当前的首选方向

        setContentView(R.layout.activity_take_photo);

        back = (ImageView) findViewById(R.id.take_photo_cancel_iv);
        chanCam = (ImageView) findViewById(R.id.take_photo_change_canmera_iv);
        surface = (SurfaceView) findViewById(R.id.take_photo_sv);
        shutter = (ImageView) findViewById(R.id.take_photo_iv);
        picText = (EditText) findViewById(R.id.take_photo_text_tv);
        picLastText = (TextView) findViewById(R.id.take_photo_last_text_tv);
        fromFile = (ImageView) findViewById(R.id.take_photo_from_file_iv);
        photoFromFile = (ImageView) findViewById(R.id.take_photo_from_file_show_iv);
        holder = surface.getHolder();//获得句柄
        holder.addCallback(this);//添加回调
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        layout_take_photo = (Layout_take_photo) findViewById(R.id.take_photo_layout);
        //设置监听
        back.setOnClickListener(listener);
        chanCam.setOnClickListener(listener);
        shutter.setOnClickListener(listener);
        fromFile.setOnClickListener(listener);
        surface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(cameraPosition==1) {
//                    calculateTapArea(motionEvent.getX(), motionEvent.getY(), 1);
//                    camera.cancelAutoFocus();
//                    Camera.Parameters parameters = camera.getParameters();
//                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//                    camera.setParameters(parameters);
                    camera.autoFocus(null);
//                }
                return false;
            }
        });
        picText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                picLastText.setText(String.valueOf(picText.getText().length()) + "/30");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //响应点击事件
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.take_photo_cancel_iv:
                    //返回
                    if(fromFileBitmap!=null && !fromFileBitmap.isRecycled()){
                        fromFileBitmap.recycle();
                        fromFileBitmap = null;
                    }
                    ActivityTakePhoto.this.setResult(RESULT_OK);
                    ActivityTakePhoto.this.finish();
                    ActivityTakePhoto.this.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    break;

                case R.id.take_photo_change_canmera_iv:
                    captureOrFromFileFlag = 0;
                    if(surface != null)surface.setVisibility(View.VISIBLE);
                    if(photoFromFile != null)photoFromFile.setVisibility(View.GONE);
                    //切换前后摄像头
                    int cameraCount = 0;
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

                    for(int i = 0; i < cameraCount; i++) {
                        Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                        if(cameraPosition == 1) {
                            //现在是后置，变更为前置
                            if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                                if(camera != null) {
                                    camera.stopPreview();//停掉原来摄像头的预览
                                    camera.release();//释放资源
                                    camera = null;//取消原来摄像头
                                }
                                camera = Camera.open(i);//打开当前选中的摄像头
                                try {
                                    camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                camera.setDisplayOrientation(90);
                                camera.startPreview();//开始预览
                                cameraPosition = 0;
                                break;
                            }
                        } else {
                            //现在是前置， 变更为后置
                            if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                                if(camera != null) {
                                    camera.stopPreview();//停掉原来摄像头的预览
                                    camera.release();//释放资源
                                    camera = null;//取消原来摄像头
                                }
                                camera = Camera.open(i);//打开当前选中的摄像头
                                try {
                                    camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                camera.setDisplayOrientation(90);
                                camera.startPreview();//开始预览
                                cameraPosition = 1;
                                break;
                            }
                        }

                    }
                    break;

                case R.id.take_photo_iv:
                    if(captureOrFromFileFlag == 0) {
                        //设置参数，并拍照
                        Camera.Parameters params = camera.getParameters();

                        List<Camera.Size> supportedPictureSizes
                                = SupportedSizesReflect.getSupportedPictureSizes(params);

                        if ( supportedPictureSizes != null &&
                                supportedPictureSizes.size() > 0) {

                            //2.x
                            Camera.Size pictureSize = supportedPictureSizes.get(0);

                            int maxSize = 800;
                            int diff = 99999;
                            for (Camera.Size size : supportedPictureSizes) {
                                ToolLog.dbg("height1:" + String.valueOf(size.height) + " width:" + String.valueOf(size.width));
                            }
                            for (Camera.Size size : supportedPictureSizes) {
                                if (maxSize <= size.height) {
                                    if(diff > size.height-maxSize) {
                                        pictureSize = size;
                                        diff = size.height-maxSize;
                                    }
                                }
                            }

                            params.setPictureFormat(PixelFormat.JPEG);//图片格式
//                                    params.setPreviewSize(800, 480);//图片大小
                            ToolLog.dbg("height:" + String.valueOf(pictureSize.height) + " width:" + String.valueOf(pictureSize.width));
                            params.setPictureSize(pictureSize.width, pictureSize.height);
                            if (cameraPosition != 0) {
                                params.setRotation(90);
                            } else {
                                params.setRotation(270);
                            }
                            camera.setParameters(params);//将参数设置到我的camera
                            camera.takePicture(null, null, jpeg);//将拍摄到的照片给自定义的对象
                        }
                    }
                    else if(captureOrFromFileFlag == 1){
                        sendPicFromFile();
                    }
                    break;

                case R.id.take_photo_from_file_iv:
                    //返回
                    Intent intent;
                    if (Build.VERSION.SDK_INT < 19) {
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");

                    } else {
                        intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    }
                    startActivityForResult(intent, REQUEST_CODE_LOCAL);
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                    break;
            }
        }
    };

    ////////////////////////事件响应/////////////////////////////////
    private void calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        Camera.Parameters parameters = camera.getParameters();
        int mm = parameters.getMaxNumFocusAreas();

        int centerX = (int) (x / getResolution().width - 1000);
        int centerY = (int) (y / getResolution().height - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);

        if (mm > 0) {
            ArrayList<Camera.Area> al = new ArrayList<Camera.Area>();
            Camera.Area a = new Camera.Area(new Rect(Math.round(rectF.left), Math.round(rectF.top),
                    Math.round(rectF.right), Math.round(rectF.bottom)), 1000);
            al.add(a);
            try
            {
                parameters.setFocusAreas(al);
                camera.setParameters(parameters);

            }catch (Throwable e)
            {

            }
            //
            //
        }

        return;
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public Camera.Size getResolution() {
        Camera.Parameters params = camera.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }

    /**
     * onActivityResult ACT结果回传
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // �����Ϣ
            if (requestCode == REQUEST_CODE_LOCAL) { // ������Ƭ
                if (data != null) {
                    captureOrFromFileFlag = 1;
                    cameraPosition = 0;//把摄像头设置为前置，方便一会还原回后置
                    if(photoFromFile != null) photoFromFile.setVisibility(View.VISIBLE);
                    photoFromFile.setBackgroundColor(0xFFFFD600);

                    Uri selectedImage = data.getData();
                    if (selectedImage != null) {
                        showPicByUri(selectedImage);
                    }
                }
            }
        }
    }

    /*surfaceHolder他是系统提供的一个用来设置surfaceView的一个对象，而它通过surfaceView.getHolder()这个方法来获得。
     Camera提供一个setPreviewDisplay(SurfaceHolder)的方法来连接*/

    //SurfaceHolder.Callback,这是个holder用来显示surfaceView 数据的接口,他必须实现以下3个方法
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //当surfaceview创建时开启相机
        if(camera == null) {
            if(msgTag==9){
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数

                for(int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                    //现在是后置，变更为前置
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) camera = Camera.open(i);
                }
            }
            else{
                camera = Camera.open();
            }
            try {
                ToolLog.dbg("here");
                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                camera.setDisplayOrientation(90);
                camera.startPreview();//开始预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //当surfaceview关闭时，关闭预览并释放资源
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if(fromFileBitmap!=null && !fromFileBitmap.isRecycled()){
            fromFileBitmap.recycle();
            fromFileBitmap = null;
        }
        holder = null;
        if(surface != null) surface = null;
    }

    //创建jpeg图片回调数据对象
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            try {
                int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
                int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
                ToolLog.dbg("T0");
                ToolLog.dbg("Max memory is " + maxMemory + "KB");
                ToolLog.dbg("Free memory is " + freeMemory + "KB");
                ToolLog.dbg("Total memory is " + totalMemory + "KB");
                //Bitmap bitmapPre = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap bitmapPre = scaleCompress(data);
                maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
                freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
                totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
                ToolLog.dbg("T0.1");
                ToolLog.dbg("Max memory is " + maxMemory + "KB");
                ToolLog.dbg("Free memory is " + freeMemory + "KB");
                ToolLog.dbg("Total memory is " + totalMemory + "KB");

                //ToolLog.dbg("data length:"+String.valueOf(data.length));
                //自定义文件保存路径  以拍摄时间区分命名
                File file = new File(PathUtil.getInstance().getImagePath(), Application.getInstance().getUserName()
                    + System.currentTimeMillis() + ".jpg");
                file.getParentFile().mkdirs();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                int options = massCompress(bitmapPre);
                bitmapPre.compress(Bitmap.CompressFormat.JPEG, options, bos);//将图片压缩的流里面
                ToolLog.dbg("bos length2:" + String.valueOf(file.length()));
                bos.flush();// 刷新此缓冲区的输出流
                bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
                camera.stopPreview();//关闭预览 处理数据
                camera.setDisplayOrientation(90);
                camera.startPreview();//数据处理完后继续开始预览
                bitmapPre.recycle();//回收bitmap空间
                bitmapPre = null;
                sendPicRes(file);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

///////////////////////////////////////////////从文件发照片逻辑///////////////////////////////////
    /**
     * 根据图库图片uri显示图片
     *
     * @param selectedImage
     */
    private void showPicByUri(Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);

        String st8 = getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            sendPicFromFilePath = picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }

            sendPicFromFilePath = file.getAbsolutePath();
        }

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("T0");
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
//        try {
//            bm = BitmapFactory.decodeFile(sendPicFromFilePath,);        //显得到bitmap图片
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        bm = scaleCompress(sendPicFromFilePath);
        if(bm != null) {
            maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
            totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
            ToolLog.dbg("T0.1");
            ToolLog.dbg("Max memory is " + maxMemory + "KB");
            ToolLog.dbg("Free memory is " + freeMemory + "KB");
            ToolLog.dbg("Total memory is " + totalMemory + "KB");
            photoFromFile.setVisibility(View.VISIBLE);
            photoFromFile.setImageBitmap(bm);
            fromFileBitmap = bm;
        }
    }

    /**
     * 根据图库图片uri发送压缩后的图片
     */
    private void sendPicFromFile() {
        try {
            //自定义文件保存路径  以拍摄时间区分命名
            File file = new File(PathUtil.getInstance().getImagePath(), Application.getInstance().getUserName()
                    + System.currentTimeMillis() + ".jpg");
            file.getParentFile().mkdirs();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            if (fromFileBitmap == null) return;
            int options = massCompress(fromFileBitmap);
            fromFileBitmap.compress(Bitmap.CompressFormat.JPEG, options, bos);//将图片压缩的流里面
            ToolLog.dbg("bos length2:" + String.valueOf(file.length()));
            bos.flush();// 刷新此缓冲区的输出流
            bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
            fromFileBitmap.recycle();//回收bitmap空间
            fromFileBitmap = null;
            sendPicFromFilePath = "";

            ToolLog.dbg("fileCnt:" + String.valueOf(getFileSize(file)));
            sendPicRes(file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////Title发布逻辑////////////////////////////////////////
    public void sendPicRes(final File mImageFile){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("file", mImageFile);
        params.put("name", mImageFile.getName());

        if(msgTag <= 0){
            ToolLog.dbg("sendMsgTag err: " + String.valueOf(msgTag));
            msgTag = 0;
        }

        AQuery aq = new AQuery(getApplicationContext());
        aq.ajax(Constant.UPLOADRESURL, params, String.class,
                new AjaxCallback<String>() {
                    @Override
                    public void callback(String url, String object,
                                         AjaxStatus status) {
                        super.callback(url, object, status);
                        ToolLog.dbg("SendPicFinish");
                        if (status.getCode() == 200) {
                            try {
                                sendTitle(msgTag,(int)getFileSize(mImageFile), object);
                                msgTag = -1;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.network_err, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        setResult(RESULT_FORCE_REFLASH);
    }

    private void sendTitle(int msgTag, int picSize, String titleCont) {
        Http http = new Http();
        String Text = picText.getText().toString();
        ToolLog.dbg("Text:" + Text);
        if(System.currentTimeMillis()- ToolGetLocationInfo.getInstance().getLastRecTime()>300000){
            ToolGetLocationInfo.getInstance().startLocation();
        }
        if(ToolGetLocationInfo.getInstance().getFailFlag()==1){
            Toast.makeText(ActivityTakePhoto.this, "网络不太好，请稍后再试", Toast.LENGTH_SHORT);
            return;
        }
        JSONObject jo = JsonPack.buildPublishTitle(msgTag, TITLE_TYPE_PICTURE, titleCont, 1, picSize, Text,
                ToolGetLocationInfo.getInstance().getLastLatitude(),ToolGetLocationInfo.getInstance().getLastLongitude(),ToolGetLocationInfo.getInstance().getLastCity());//组包
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponsePublishTitle(JacksonWrapper.json2Bean(response, ResponsePublishTitle.class));
                ToolLog.dbg("Send Finish");
                if (responsePublishTitle.getStatusCode() == 0) {
                    if (fromFileBitmap != null && !fromFileBitmap.isRecycled()) {
                        fromFileBitmap.recycle();
                        fromFileBitmap = null;
                    }
                    setResult(RESULT_FORCE_REFLASH);
                    finish();
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.network_err, Toast.LENGTH_SHORT)
                            .show();
                    if (fromFileBitmap != null && !fromFileBitmap.isRecycled()) {
                        fromFileBitmap.recycle();
                        fromFileBitmap = null;
                    }
                    setResult(RESULT_FORCE_REFLASH);
                    finish();
                    overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                }
            }
        });
    }

    /**
     * 获取文件大小
     *
     * @para f
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            ToolLog.dbg("文件不存在!");
        }
        return size;
    }

    //像素缩放
    private Bitmap scaleCompress(String bitmapPath){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("T1");
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("T2");
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        return BitmapFactory.decodeFile(bitmapPath, newOpts);
    }

    //像素缩放2
    private Bitmap scaleCompress(byte[] data){
        ToolLog.dbg("dataLength is " + String.valueOf(data.length));

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        be += 1;
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        ToolLog.dbg("be:"+String.valueOf(be));
        return BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
    }

    //质量压缩
    private int massCompress(Bitmap bitmap){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("T7");
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        ToolLog.dbg("baos length first:" + String.valueOf(baos.toByteArray().length));
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("T8");
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        int options = 50;
        while ( baos.toByteArray().length / 1024>25) {  //循环判断如果压缩后图片是否大于25kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 5;
            if(options <= 20){
                break;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
            totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
            ToolLog.dbg("T9");
            ToolLog.dbg("Max memory is " + maxMemory + "KB");
            ToolLog.dbg("Free memory is " + freeMemory + "KB");
            ToolLog.dbg("Total memory is " + totalMemory + "KB");
        }
        ToolLog.dbg("options:" + String.valueOf(options));
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        baos.reset();
        maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("T10");
        ToolLog.dbg("Max memory is " + maxMemory + "KB");
        ToolLog.dbg("Free memory is " + freeMemory + "KB");
        ToolLog.dbg("Total memory is " + totalMemory + "KB");
        ToolLog.dbg("compressed size" + String.valueOf(baos.toByteArray().length));

        return options;
    }

    ///////////////////////////////////////////////get set 类/////////////////////////////////////
    public ResponsePublishTitle getResponsePublishTitle(){return responsePublishTitle;}
    public void setResponsePublishTitle(ResponsePublishTitle responsePublishTitle){this.responsePublishTitle = responsePublishTitle;}

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
        MobclickAgent.onPause(this);
    }

    private class ThreadSendPhoto implements Runnable {
        private File file;

        public ThreadSendPhoto(File file) {
            this.file = file;
        }

        public void run() {
            sendPicRes(file);
        }
    }

    public static class SupportedSizesReflect {
        private Method Parameters_getSupportedPreviewSizes = null;
        private static Method Parameters_getSupportedPictureSizes = null;

        static {
            initCompatibility();
        }

        private static void initCompatibility() {
            try {
                Parameters_getSupportedPictureSizes = Camera.Parameters.class.getMethod(
                        "getSupportedPictureSizes", new Class[] {});

            } catch (NoSuchMethodException nsme) {
                nsme.printStackTrace();
                Parameters_getSupportedPictureSizes = null;
            }
        }

        /**
         * Android 2.1之后有效
         * @param p
         * @return Android1.x返回null
         */
        public List<Camera.Size> getSupportedPreviewSizes(Camera.Parameters p) {
            return getSupportedSizes(p, Parameters_getSupportedPreviewSizes);
        }

        public static List<Camera.Size> getSupportedPictureSizes(Camera.Parameters p){
            return getSupportedSizes(p, Parameters_getSupportedPictureSizes);
        }

        @SuppressWarnings("unchecked")
        private static List<Camera.Size> getSupportedSizes(Camera.Parameters p, Method method){
            try {
                if (method != null) {
                    return (List<Camera.Size>) method.invoke(p);
                } else {
                    return null;
                }
            } catch (InvocationTargetException ite) {
                Throwable cause = ite.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new RuntimeException(ite);
                }
            } catch (IllegalAccessException ie) {
                return null;
            }
        }
    }
}
