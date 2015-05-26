package com.yunkairichu.cike.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaf.jcore.Http;
import com.jaf.jcore.HttpCallBack;
import com.jaf.jcore.JacksonWrapper;
import com.yunkairichu.cike.bean.BaseResponseTitleInfo;
import com.yunkairichu.cike.bean.JsonConstant;
import com.yunkairichu.cike.bean.JsonPack;
import com.yunkairichu.cike.bean.ResponseSearchTitle;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by vida2009 on 2015/5/11.
 */
public class ActivitySquare extends Activity{

    private ResponseSearchTitle responseSearchTitle;
    private HorizontalScrollView squareScrollView;
    private GridLayout squareGridLayout;
    private View view;
    private View selectView;
    private squareOnTouchListener sqListener = new squareOnTouchListener();
    private LinearLayout squareSelectButton;
    private Button squareChainButton;
    private Button squarePubTiButton;

    private Bitmap[] titleBitmap = new Bitmap[50]; //??????50
    private int bitmapNum;
    private int tmpCnt;
    private int searchFlag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square);
        squareScrollView = (HorizontalScrollView) findViewById(R.id.squereHScrollView);
        squareGridLayout = (GridLayout) findViewById(R.id.squereGridLayout);
        squareSelectButton = (LinearLayout) findViewById(R.id.status_picker);
        squareSelectButton.setClickable(true);
        squareChainButton = (Button) findViewById(R.id.squareUserChainButton);
        squarePubTiButton = (Button) findViewById(R.id.bigbutton_add);
        LayoutInflater inflater = getLayoutInflater();
        selectView = inflater.inflate(R.layout.view_square_selector, null);
        view = squareGridLayout.getChildAt(0);
        squareScrollView.setOnTouchListener(sqListener);

        Bundle bundle = this.getIntent().getExtras();
        responseSearchTitle = (ResponseSearchTitle)bundle.getSerializable("resSearchTitle");

        squareChainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySquare.this, ActivityTab.class);
                startActivity(i);
                finish();
            }
        });

        squareSelectButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  setContentView(selectView);
              }
         });

        squarePubTiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (popupWindow != null&& popupWindow.isShowing()) {
//                    popupWindow.dismiss();
//                    return;
//                } else {
//                    initmPopupWindowView();
//                    popupWindow.showAsDropDown(v, 0, 5);
//                }
            }
        });

        firReFlashSearchTitle();
        getTitleBitmap();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////������׽/////////////////////////////////////////////////

    class squareOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    // ������������¼������������ݣ�����ScrollView�Ѿ������ײ�������һ������
                    if (/*sqListener != null
                            && view != null
                            &&*/ squareScrollView.getChildAt(0).getMeasuredWidth() <= view.getWidth() + view.getScrollX()) {
                        if(searchFlag==1) {
                            searchFlag = 0;
                            doTitleSearch();
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    ///////////////////////////////////////////���߳�/////////////////////////////////////////////////

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            Bundle data = msg.getData();
            Bitmap bitmap = (Bitmap)data.getParcelable("bitmap");
            int index = data.getInt("index");
            ToolLog.dbg("OrigCnt"+String.valueOf(bitmap.getByteCount()));
            //titleBitmap[index] = compressImage(bitmap);
            titleBitmap[index] = bitmap;
            secReFlashSearchTitle(index);
        }
    };

    private class ThreadGetSquareTitleBitmap implements Runnable {
        private ResponseSearchTitle responseSearchTitle;

        public ThreadGetSquareTitleBitmap(ResponseSearchTitle responseSearchTitle){
            this.responseSearchTitle = responseSearchTitle;
        }

        public void run() {
            bitmapNum = 0;
            tmpCnt=0;
            for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++){
                if(responseSearchTitle.getReturnData().getContData().get(i).getTitleType()==2||responseSearchTitle.getReturnData().getContData().get(i).getTitleType()==3){bitmapNum++;}
            }
            for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++) {
                BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
                if(baseResponseTitleInfo.getTitleType()!=2&&baseResponseTitleInfo.getTitleType()!=3){continue;}
                URL url = null;
                try {
                    url = new URL(baseResponseTitleInfo.getTitleCont());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                conn.setConnectTimeout(5000 * 10);
                try {
                    conn.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = null;
                try {
                    if (conn.getResponseCode() == 200) {
                        InputStream inputStream = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putParcelable("bitmap", bitmap);
                data.putInt("index", i);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }
    }

    //////////////////////////////////////////����������չʾ//////////////////////////////////////////////////////

    public void doTitleSearch() {
        Http http = new Http();
        JSONObject jo = JsonPack.buildSearchTitle(0, 0);
        http.url(JsonConstant.CGI).JSON(jo).post(new HttpCallBack() {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (response == null) {
                    ToolLog.dbg("server error");
                    return;
                }

                setResponseSearchTitle(JacksonWrapper.json2Bean(response, ResponseSearchTitle.class));
                firReFlashSearchTitle();
                getTitleBitmap();
            }
        });
    }

    public void getTitleBitmap(){
        ThreadGetSquareTitleBitmap threadGetSquareTitleBitmap =new ThreadGetSquareTitleBitmap(responseSearchTitle);
        new Thread(threadGetSquareTitleBitmap).start();
    }

    public void firReFlashSearchTitle(){
        int lineNum = responseSearchTitle.getReturnData().getLineNum();
        int[] linePos = new int[lineNum];
        for(int i=0;i<lineNum;i++){linePos[i]=0;}
        squareGridLayout.removeAllViews();
        for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++){
            BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
            ImageView tv = new ImageView(this);
            if(baseResponseTitleInfo.getTitleType()==3){tv.setBackgroundColor(123);}
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int k = i%lineNum;
            params.rowSpec = GridLayout.spec(k);
            params.columnSpec = GridLayout.spec(linePos[k], baseResponseTitleInfo.getBlockLen());
            linePos[k] += baseResponseTitleInfo.getBlockLen();
            params.setGravity(Gravity.FILL);
            tv.setLayoutParams(params);

            tv.setTag(i);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ActivitySquare.this, ActivityChat.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("titleInfo", responseSearchTitle.getReturnData().getContData().get((int) v.getTag()));
                    bundle.putSerializable("resSearchTitle", getResponseSearchTitle());
                    int iTag = (int) v.getTag();
                    ToolLog.dbg("byteCnt:" + String.valueOf(titleBitmap[iTag].getByteCount()));
                    ByteArrayOutputStream baos = compressImage(titleBitmap[iTag]);
                    i.putExtra("bitmap", baos.toByteArray());
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            });
            squareGridLayout.addView(tv, i);
        }
    }

    public void secReFlashSearchTitle(int index){
//        int lineNum = responseSearchTitle.getReturnData().getLineNum();
//        int[] linePos = new int[lineNum];
//        for(int i=0;i<lineNum;i++){linePos[i]=0;}
//        squareGridLayout.removeAllViews();
//        for(int i=0;i<responseSearchTitle.getReturnData().getContData().size();i++){
//            BaseResponseTitleInfo baseResponseTitleInfo = responseSearchTitle.getReturnData().getContData().get(i);
//            ImageView tv = new ImageView(this);
//            if(baseResponseTitleInfo.getTitleType()==3){tv.setImageBitmap(titleBitmap[i]);}
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            int k = i%lineNum;
//            params.rowSpec = GridLayout.spec(k);
//            params.columnSpec = GridLayout.spec(linePos[k], baseResponseTitleInfo.getBlockLen());
//            linePos[k] += baseResponseTitleInfo.getBlockLen();
//            params.setGravity(Gravity.FILL);
//            tv.setLayoutParams(params);
//            squareGridLayout.addView(tv,i);
//        }
        tmpCnt++;
        //ToolLog.dbg("bitmapNum:"+String.valueOf(bitmapNum)+"tmpCnt:" + String.valueOf(tmpCnt));
        if(tmpCnt==bitmapNum){searchFlag = 1;}
        ((ImageView)squareGridLayout.getChildAt(index)).setImageBitmap(titleBitmap[index]);

    }

    public ResponseSearchTitle getResponseSearchTitle(){return responseSearchTitle;}

    public void setResponseSearchTitle(ResponseSearchTitle responseSearchTitle){this.responseSearchTitle = responseSearchTitle;}

    //ͼƬѹ������
    private /*Bitmap*/ ByteArrayOutputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��
        ToolLog.dbg("compressCnt:" + String.valueOf(baos.toByteArray().length));
        int options = 40;
        while ( baos.toByteArray().length / 1024>35) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����35kb,���ڼ���ѹ��
            baos.reset();//����baos�����baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��
            options -= 10;//ÿ�ζ�����10
        }
        //ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��
        //Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ
        //ToolLog.dbg("fiCompressCnt:" + String.valueOf(bitmap.getByteCount()));
        //return bitmap;
        return baos;
    }



}
