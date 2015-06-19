package com.yunkairichu.cike.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yunkairichu.cike.utils.CommonUtils;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by vida2009 on 2015/6/14.
 */
public class ToolFileRW {
    private int delImageFlag = 0;

    /**
     * 内部类实现单例模式
     * 延迟加载，减少内存开销
     *
     * @author xuzhaohu
     *
     */
    private static class ToolFileRWHolder {
        private static ToolFileRW instance = new ToolFileRW();
    }

    /**
     * 私有的构造函数
     */
    private ToolFileRW() {

    }

    public static ToolFileRW getInstance() {
        return ToolFileRWHolder.instance;
    }

    protected void method() {
        System.out.println("SingletonInner");
    }

    public void initTitleNewMsg(){
        delImageFlag = 0;
    }

    //读文件
    public String readSDFile(String fileName) throws IOException {

        File file = new File(fileName);

        FileInputStream fis = new FileInputStream(file);

        int length = fis.available();

        byte [] buffer = new byte[length];
        fis.read(buffer);

        String res = EncodingUtils.getString(buffer, "UTF-8");

        fis.close();
        return res;
    }

    //写文件
    public void writeSDFile(String fileName, String write_str) throws IOException{

        File file = new File(fileName);

        FileOutputStream fos = new FileOutputStream(file);

        byte [] bytes = write_str.getBytes();

        fos.write(bytes);

        fos.close();
    }

    //创建此刻图片文件夹
    public int mkdirBitmapFile(){

        if (!CommonUtils.isExitsSdcard()) {
            ToolLog.dbg("SD CARD LACK");
            return -1;
        }

        File destDir = new File(Constant.CIKEAPPPATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File destDir2 = new File(Constant.CIKEAPPPICPATH);
        if (!destDir2.exists()) {
            destDir2.mkdirs();
        }

        File destDir3 = new File(Constant.CIKEAPPDATAPATH);
        if (!destDir3.exists()) {
            destDir3.mkdirs();
        }

        return 0;
    }

    /** 保存此刻图片方法 */
    public void saveBitmapToFile(Bitmap bm, String picName) {
        if(delImageFlag == 1){
            ToolLog.dbg("正在删除图片，不保存");
            return;
        }
        ToolLog.dbg("保存图片");
        File f = new File(Constant.CIKEAPPPICPATH, picName);
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                s = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(s>0){
                ToolLog.dbg("已经存在");
                return;
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 40, out);
            out.flush();
            out.close();
            //bm.recycle();
            ToolLog.dbg("已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            ToolLog.dbg("文件不存在");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            ToolLog.dbg("保存失败");
            e.printStackTrace();
        }
    }

    //读取此刻图片方法
    public Bitmap loadBitmapFromFile(String picName){
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int freeMemory = (int) (Runtime.getRuntime().freeMemory() / 1024);
        int totalMemory = (int) (Runtime.getRuntime().totalMemory() / 1024);
        ToolLog.dbg("Max1 memory is " + maxMemory + "KB");
        ToolLog.dbg("Free1 memory is " + freeMemory + "KB");
        ToolLog.dbg("Total1 memory is " + totalMemory + "KB");
        String path = Constant.CIKEAPPPICPATH + "/" + picName;
        ToolLog.dbg("路径:"+path);
        try{
            File f=new File(path);
            if(!f.exists()){
                ToolLog.dbg("f n exists");
                return null;
            }
        }catch (Exception e) {
            // TODO: handle exception
            ToolLog.dbg("f open err");
            return null;
        }

        ToolLog.dbg("max2:"+String.valueOf(maxMemory));
//        if(maxMemory<102400) {
//            ToolLog.dbg("in");
//            BitmapFactory.Options newOpts = new BitmapFactory.Options();
//            newOpts.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(path, newOpts);
//            newOpts.inJustDecodeBounds = false;
//            newOpts.inSampleSize = 1;
//            return BitmapFactory.decodeFile(path,newOpts); //内存小的，缩放改在存储时做了
//        } else {
            return BitmapFactory.decodeFile(path);
//        }
    }

    /** 删除此刻图片方法 */
    public void delBitmapFromFile() {
        delImageFlag = 1;
//        ToolLog.dbg("删除图片");
        File mfile = new File(Constant.CIKEAPPPICPATH);
        File[] files = mfile.listFiles();
        long outDayTime = 30*3600*1000;   //30小时的微秒值，作为删除时间
        if(files.length > 1000) {
            for(int i=0; i<files.length; i++){
                files[i].delete();
            }
        } else {
            for (int i = 0; i < files.length; i++) {
                long lastModifiedTime = files[i].lastModified();
//                ToolLog.dbg("name:" + files[i].getName() + " lastModTime:" + lastModifiedTime);
//                ToolLog.dbg("nowTime:" + String.valueOf(System.currentTimeMillis()));

                if (System.currentTimeMillis() - lastModifiedTime >= outDayTime) {
                    files[i].delete();
                }
            }
        }
        delImageFlag = 0;
    }

    /** 保存此刻第一次搜索方法 */
    public void saveSquareToFile(JSONObject jo, String fileName) {
        File f = new File(Constant.CIKEAPPDATAPATH, fileName);
        long s = 0;
        if (f.exists()) {
           f.delete();
        }
        String data = jo.toString();
//        ToolLog.dbg("in data:"+data);
//        ToolLog.dbg("data length:"+data.length()+" byte len:"+data.getBytes().length);
        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(data.getBytes(),0,data.getBytes().length);
            out.flush();
            out.close();
//            ToolLog.dbg("已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
//            ToolLog.dbg("文件不存在");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            ToolLog.dbg("保存失败");
            e.printStackTrace();
        }
    }

    //此刻第一次搜索读取方法
    public JSONObject loadSquareFromFile(String fileName){
        int s = 0;
        String path = Constant.CIKEAPPDATAPATH + "/" + fileName;
//        ToolLog.dbg("路径:"+path);
        try{
            File f=new File(path);
            if(!f.exists()){
//                ToolLog.dbg("f not exists");
                return null;
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
//                ToolLog.dbg("f not exists");
                return null;
            }
            try {
                s = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
//                ToolLog.dbg("f len err");
                return null;
            }
        }catch (Exception e) {
            // TODO: handle exception
//            ToolLog.dbg("f open fail");
            return null;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            ToolLog.dbg("f not exists");
            return null;
        }

        if(s<=0){
            return null;
        }
        byte[] data = new byte[s];
        try {
            fis.read(data);
        } catch (IOException e) {
            e.printStackTrace();
//            ToolLog.dbg("read data err");
            return null;
        }
        String fileContent = EncodingUtils.getString(data, "UTF-8");
//        ToolLog.dbg("UTF8:" + fileContent);
        JSONObject jo = null;
        try {
            jo = new JSONObject(fileContent);
        } catch (JSONException e) {
            e.printStackTrace();
//            ToolLog.dbg("change to json err");
            return null;
        }
//        ToolLog.dbg("finish:"+fileContent);
        return jo;
    }

    /** 保存此刻关系链方法(只存第一组) */
    public void saveUserChainToFile(JSONObject jo, String fileName) {
        File f = new File(Constant.CIKEAPPDATAPATH, fileName);
        long s = 0;
        if (f.exists()) {
            f.delete();
        }
        String data = jo.toString();
//        ToolLog.dbg("in data:"+data);
//        ToolLog.dbg("data length:"+data.length()+" byte len:"+data.getBytes().length);
        try {
            FileOutputStream out = new FileOutputStream(f);
            out.write(data.getBytes(),0,data.getBytes().length);
            out.flush();
            out.close();
//            ToolLog.dbg("已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
//            ToolLog.dbg("文件不存在");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
//            ToolLog.dbg("保存失败");
            e.printStackTrace();
        }
    }

    //此刻关系链读取方法(只存第一组)
    public JSONObject loadUserChainFromFile(String fileName){
        int s = 0;
        String path = Constant.CIKEAPPDATAPATH + "/" + fileName;
//        ToolLog.dbg("路径:"+path);
        try{
            File f=new File(path);
            if(!f.exists()){
//                ToolLog.dbg("f not exists");
                return null;
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
//                ToolLog.dbg("f not exists");
                return null;
            }
            try {
                s = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
//                ToolLog.dbg("f len err");
                return null;
            }
        }catch (Exception e) {
            // TODO: handle exception
//            ToolLog.dbg("f open fail");
            return null;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            ToolLog.dbg("f not exists");
            return null;
        }

        if(s<=0){
            return null;
        }
        byte[] data = new byte[s];
        try {
            fis.read(data);
        } catch (IOException e) {
            e.printStackTrace();
//            ToolLog.dbg("read data err");
            return null;
        }
        String fileContent = EncodingUtils.getString(data, "UTF-8");
//        ToolLog.dbg("UTF8:" + fileContent);
        JSONObject jo = null;
        try {
            jo = new JSONObject(fileContent);
        } catch (JSONException e) {
            e.printStackTrace();
//            ToolLog.dbg("change to json err");
            return null;
        }
//        ToolLog.dbg("finish:"+fileContent);
        return jo;
    }

    //像素缩放
    private Bitmap scaleCompress(Bitmap bitmapPre){
        ByteArrayOutputStream baosPre = new ByteArrayOutputStream();
        bitmapPre.compress(Bitmap.CompressFormat.JPEG, 100, baosPre);

        ByteArrayInputStream isBm = new ByteArrayInputStream(baosPre.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
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
        isBm = new ByteArrayInputStream(baosPre.toByteArray());
        ToolLog.dbg("scale after:"+String.valueOf(baosPre.toByteArray().length)+" be:"+String.valueOf(be));
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;
    }

    //质量压缩
    private int massCompress(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        ToolLog.dbg("baos length first:" + String.valueOf(baos.toByteArray().length));
        int options = 50;
        while ( baos.toByteArray().length / 1024>25) {  //循环判断如果压缩后图片是否大于25kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 5;
            if(options <= 20){
                break;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ToolLog.dbg("options:" + String.valueOf(options));
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        ToolLog.dbg("compressed size" + String.valueOf(baos.toByteArray().length));

        return options;
    }
}
