package com.yunkairichu.cike.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.ImageUtils;
import com.yunkairichu.cike.main.ActivityChat;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.main.ToolLog;
import com.yunkairichu.cike.main.ToolShowBigImage;
import com.yunkairichu.cike.utils.CommonUtils;
import com.yunkairichu.cike.utils.ImageCache;

import java.io.File;

/**
 * Created by vida2009 on 2015/5/21.
 */
public class LoadImageTask extends AsyncTask<Object, Void, Bitmap> {
    private ImageView iv = null;
    String localFullSizePath = null;
    String thumbnailPath = null;
    String remotePath = null;
    EMMessage message = null;
    EMMessage.ChatType chatType;
    Activity activity;
    private EMConversation conversation;
    View bigImage;
    BigImageOnTouchListener bigImageOnTouchListener = new BigImageOnTouchListener();
    ToolShowBigImage toolShowBigImage;
    int position;

    @Override
    protected Bitmap doInBackground(Object... args) {
        thumbnailPath = (String) args[0];
        localFullSizePath = (String) args[1];
        remotePath = (String) args[2];
        chatType = (EMMessage.ChatType) args[3];
        iv = (ImageView) args[4];
        // if(args[2] != null) {
        activity = (Activity) args[5];
        toolShowBigImage = new ToolShowBigImage(activity);
        // }
        message = (EMMessage) args[6];
        conversation = (EMConversation) args[7];
        position = (int) args[8];
        File file = new File(thumbnailPath);
        if (file.exists()) {
            return ImageUtils.decodeScaleImage(thumbnailPath, 160, 160);
        } else {
            if (message.direct == EMMessage.Direct.SEND) {
                return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
            } else {
                return null;
            }
        }


    }

    protected void onPostExecute(Bitmap image) {
        LayoutInflater inflater = activity.getLayoutInflater();
        bigImage = inflater.inflate(R.layout.view_photo, null);

        if (image != null) {
            iv.setImageBitmap(image);
            iv.setImageResource(R.drawable.default_image_gray);
            ImageCache.getInstance().put(thumbnailPath, image);  //缓存小图片
            iv.setClickable(true);
            iv.setTag(thumbnailPath);
            iv.setTag(R.id.tag_message,message);
            iv.setTag(R.id.tag_position,position);
            iv.setOnTouchListener(bigImageOnTouchListener);
            iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (thumbnailPath != null) {

                        //Intent intent = new Intent(activity, ActivityShowBigImage.class);
                        File file = new File(localFullSizePath);
                        Uri uri = null;
                        if (file.exists()) {
                            uri = Uri.fromFile(file);
                            //intent.putExtra("uri", uri);
                        } else {
                            // The local full size pic does not exist yet.
                            // ShowBigImage needs to download it from the server
                            // first
                            //intent.putExtra("remotepath", remotePath);
                        }
                        if (message.getChatType() != EMMessage.ChatType.Chat) {
                            // delete the image from server after download
                        }
                        if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
                            message.isAcked = true;
                            try {
                                // ���˴�ͼ�󷢸��Ѷ���ִ��Է�
                                EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //activity.startActivity(intent);

                        toolShowBigImage.start_show_photo(uri, remotePath, "");
                    }
                    return false;
                }
            });
        } else {
            if (message.status == EMMessage.Status.FAIL) {
                if (CommonUtils.isNetWorkConnected(activity)) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            EMChatManager.getInstance().asyncFetchMessage(message);
                        }
                    }).start();
                }
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void postLoadBigImage(EMMessage message, int position){
        toolShowBigImage.gonePhotoView();
        conversation.removeMessage(message.getMsgId());
//        ListView listView = ((ActivityChat)activity).getListView();
//        listView.getChildAt(position).setVisibility(View.GONE);
        ((ActivityChat)activity).getAdapter().notifyDataSetChanged();
        ((ActivityChat)activity).getAdapter().refresh();
    }

    class BigImageOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    ToolLog.dbg("hello up");
                    postLoadBigImage((EMMessage)view.getTag(R.id.tag_message), (int)view.getTag(R.id.tag_position));
                    break;
                default:
                    ToolLog.dbg("hello oth");
                    break;
            }
            return false;
        }
    }
}
