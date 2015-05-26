package com.yunkairichu.cike.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.FileMessageBody;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.chat.VoiceMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.DateUtils;
import com.yunkairichu.cike.main.ActivityChat;
import com.yunkairichu.cike.main.Constant;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.main.ToolLog;
import com.yunkairichu.cike.main.ToolShowBigImage;
import com.yunkairichu.cike.task.LoadImageTask;
import com.yunkairichu.cike.utils.ImageCache;
import com.yunkairichu.cike.utils.ImageUtils;
import com.yunkairichu.cike.utils.SmileUtils;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vida2009 on 2015/5/15.
 */
public class MessageAdapter extends BaseAdapter {
    private final static String TAG = "msg";

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 12;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 13;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 14;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 15;

    public static final String IMAGE_DIR = "chat/image/";
    public static final String VOICE_DIR = "chat/audio/";
    public static final String VIDEO_DIR = "chat/video";

    private String username;
    private LayoutInflater inflater;
    private Activity activity;
    private View bigImage;
    private BigImageOnTouchListener bigImageOnTouchListener = new BigImageOnTouchListener();

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    // reference to conversation object in chatsdk
    private EMConversation conversation;
    EMMessage[] messages = null;

    private Context context;

    private Map<String, Timer> timers = new Hashtable<String, Timer>();

    private Long titleId;
    private int imageHolder;
    private int txtHolder;
    private int voiceHolder;
    private int delHolder;
    private int holderHeight;
    private int holderWidth;
    private ToolShowBigImage toolShowBigImage;

    public MessageAdapter(Context context, String username, int chatType, long titleId) {
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (Activity) context;
        toolShowBigImage = new ToolShowBigImage(activity);
        this.conversation = EMChatManager.getInstance().getConversation(username);
        this.titleId = titleId;
        this.imageHolder = 0;
        this.txtHolder = 0;
        this.voiceHolder = 0;
        this.delHolder = 0;
        this.holderHeight = 1;
        this.holderWidth = 1;
    }

    Handler handler = new Handler() {
        private void refreshList() {
            messages = (EMMessage[]) conversation.getAllMessages().toArray(new EMMessage[conversation.getAllMessages().size()]);
            ToolLog.dbg("bfoCnt:"+String.valueOf(conversation.getMsgCount()));
            for (int i=messages.length-1; i>=0; i--) {
                // getMessage will set message as read status
                conversation.getMessage(i);
            }
            ToolLog.dbg("aftCnt:"+String.valueOf(conversation.getMsgCount()));
            notifyDataSetChanged();
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    refreshList();
                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (activity instanceof ActivityChat) {
                        ListView listView = ((ActivityChat)activity).getListView();
                        if (messages.length > 0) {
                            listView.setSelection(messages.length - 1);
                        }
                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    if (activity instanceof ActivityChat) {
                        ListView listView = ((ActivityChat)activity).getListView();
                        listView.setSelection(position);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * ��ȡitem��
     */
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }

    /**
     * ˢ��ҳ��
     */
    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * ˢ��ҳ��, ѡ�����һ��
     */
    public void refreshSelectLast() {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
    }

    /**
     * ˢ��ҳ��, ѡ��Position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
        msg.arg1 = position;
        handler.sendMessage(msg);
    }

    public EMMessage getItem(int position) {
        if (messages != null && position < messages.length) {
            return messages[position];
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    /**
     * ��ȡitem������
     */
    public int getViewTypeCount() {
        return 16;
    }

    /**
     * ��ȡitem����
     */
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }
        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
            else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }


    private View createViewByMessage(EMMessage message, int position, int del) {
        if(del==0) {
            switch (message.getType()) {
//            case LOCATION:
//                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_location, null) : inflater.inflate(
//                        R.layout.row_sent_location, null);
                case IMAGE:
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_picture, null) : inflater.inflate(
                            R.layout.row_send_picture, null);
                case VOICE:
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice, null) : inflater.inflate(
                            R.layout.row_send_voice, null);
//            case VIDEO:
//                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video, null) : inflater.inflate(
//                        R.layout.row_sent_video, null);
//            case FILE:
//                return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_file, null) : inflater.inflate(
//                        R.layout.row_sent_file, null);
                default:
//                // ����ͨ��
//                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
//                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice_call, null) : inflater
//                            .inflate(R.layout.row_sent_voice_call, null);
//                    // ��Ƶͨ��
//                else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
//                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video_call, null) : inflater
//                            .inflate(R.layout.row_sent_video_call, null);
                    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_msg, null) : inflater.inflate(
                            R.layout.row_send_msg, null);
            }
        }
        else{
            return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_del, null) : inflater
                            .inflate(R.layout.row_send_del, null);
        }
    }

    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EMMessage message = getItem(position);
        EMMessage.ChatType chatType = message.getChatType();
        ViewHolder holder;
        int del = 0;

        if (convertView == null || convertView.findViewById(R.id.userhead)==null) {
            holder = new ViewHolder();
            convertView = createViewByMessage(message, position, 0);
            if (message.getType() == EMMessage.Type.IMAGE) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.sendPicture));    //��ͼƬ
                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.userhead); //ͷ����ͬ
                    //holder.tv = (TextView) convertView.findViewById(R.id.percentage);  //�ٷֱ��Ȳ�Ū����ͬ
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar); //�����Ҳ��Ū����ͬ
                    //holder.pb = (ProgressBar) convertView.findViewById(R.id.sending); //�����תȦȦ����ͬ
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status); //�ش�����ͬ
                    //holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid); //�ǳƲ�Ҫ����ͬ
                } catch (Exception e) {

                }
            } else if (message.getType() == EMMessage.Type.TXT) {
                try {
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.userhead);
                    // ��������������
                    holder.tv = (TextView) convertView.findViewById(R.id.chatcontent);
                    //holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }

                // ����ͨ������Ƶͨ��
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                        || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
//                    holder.iv = (ImageView) convertView.findViewById(R.id.iv_call_icon);
//                    holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                }
            } else if (message.getType() == EMMessage.Type.VOICE) {
                try {
                    holder.iv = ((ImageView) convertView.findViewById(R.id.voice_play));
                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.userhead);
                    holder.tv = (TextView) convertView.findViewById(R.id.length);
                    holder.pb = (ProgressBar) convertView.findViewById(R.id.sending);
                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
                    //holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                    holder.iv_read_status = (ImageView) convertView.findViewById(R.id.unread_voice);
                } catch (Exception e) {
                }
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                try {
//                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }
            } else if (message.getType() == EMMessage.Type.VIDEO) {
                try {
//                    holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
//                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
//                    holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
//                    holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
//                    holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);

                } catch (Exception e) {
                }
            } else if (message.getType() == EMMessage.Type.FILE) {
                try {
//                    holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
//                    holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
//                    holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
//                    holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
//                    holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
//                    holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
//                    holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
//                    // �����ǽ��ֵ
//                    holder.tv = (TextView) convertView.findViewById(R.id.percentage);
                } catch (Exception e) {
                }
                try {
//                    holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
                } catch (Exception e) {
                }
            }

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

//        if(del!=0){
//            return convertView;
//        }

        // 群聊时，显示接收的消息的发送人的名称
        if (chatType == EMMessage.ChatType.GroupChat && message.direct == EMMessage.Direct.RECEIVE) {
            //demo里使用username代码nick
            holder.tv_usernick.setText(message.getFrom());
        }
        // 如果是发送的消息并且不是群聊消息，显示已读textview
        if (message.direct == EMMessage.Direct.SEND && chatType != EMMessage.ChatType.GroupChat) {
            //��������ʱ����
            //holder.tv_ack = (TextView) convertView.findViewById(R.id.ack);
            //holder.tv_delivered = (TextView) convertView.findViewById(R.id.tv_delivered);
            if (holder.tv_ack != null) {
                if (message.isAcked) {
                    if (holder.tv_delivered != null) {
                        holder.tv_delivered.setVisibility(View.INVISIBLE);
                    }
                    holder.tv_ack.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_ack.setVisibility(View.INVISIBLE);

                    // check and display msg delivered ack status
                    if (holder.tv_delivered != null) {
                        if (message.isDelivered) {
                            holder.tv_delivered.setVisibility(View.VISIBLE);
                        } else {
                            holder.tv_delivered.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        } else {
            // ������ı����ߵ�ͼ��Ϣ���Ҳ���group messgae����ʾ��ʱ���Է������Ѷ���ִ
            if ((message.getType() == EMMessage.Type.TXT || message.getType() == EMMessage.Type.LOCATION) && !message.isAcked && chatType != EMMessage.ChatType.GroupChat) {
                // ��������ͨ����¼
                if (!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    try {
                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                        // �����Ѷ���ִ
                        message.isAcked = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //�����û�ͷ��
        // setUserAvatar(message, holder.iv_avatar);
        switch (message.getType()) {
            // �����Ϣtype��ʾitem
            case IMAGE: // ͼƬ
                ToolLog.dbg("image here");
                handleImageMessage(message, holder, position, convertView);
                break;
            case TXT: // �ı�
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                        || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                    // ����Ƶͨ��
                    handleCallMessage(message, holder, position);
                else
                    handleTextMessage(message, holder, position);
                break;
//            case LOCATION: // λ��
//                handleLocationMessage(message, holder, position, convertView);
//                break;
            case VOICE: // ����
                handleVoiceMessage(message, holder, position, convertView);
//                break;
//            case VIDEO: // ��Ƶ
//                handleVideoMessage(message, holder, position, convertView);
//                break;
//            case FILE: // һ���ļ�
//                handleFileMessage(message, holder, position, convertView);
//                break;
            default:
                // not supported
        }

        if (message.direct == EMMessage.Direct.SEND) {
            View statusView = convertView.findViewById(R.id.msg_status);
            // �ط���ť����¼�
//            statusView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // ��ʾ�ط���Ϣ���Զ���alertdialog
//                    Intent intent = new Intent(activity, AlertDialog.class);
//                    intent.putExtra("msg", activity.getString(R.string.confirm_resend));
//                    intent.putExtra("title", activity.getString(R.string.resend));
//                    intent.putExtra("cancel", true);
//                    intent.putExtra("position", position);
//                    if (message.getType() == EMMessage.Type.TXT)
//                        activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_TEXT);
//                    else if (message.getType() == EMMessage.Type.VOICE)
//                        activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_VOICE);
//                    else if (message.getType() == EMMessage.Type.IMAGE)
//                        activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_PICTURE);
//                    else if (message.getType() == EMMessage.Type.LOCATION)
//                        activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_LOCATION);
//                    else if (message.getType() == EMMessage.Type.FILE)
//                        activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_FILE);
//                    else if (message.getType() == EMMessage.Type.VIDEO)
//                        activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_VIDEO);
//
//                }
//            });

        } else {
//            final String st = context.getResources().getString(R.string.Into_the_blacklist);
//            // ����ͷ���������
//            holder.iv_avatar.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    Intent intent = new Intent(activity, AlertDialog.class);
//                    intent.putExtra("msg", st);
//                    intent.putExtra("cancel", true);
//                    intent.putExtra("position", position);
//                    activity.startActivityForResult(intent, ActivityChat.REQUEST_CODE_ADD_TO_BLACKLIST);
//                    return true;
//                }
//            });
        }

        int iTitleId = 0;
        ToolLog.dbg(message.getBody().toString());
        try {
            iTitleId = Integer.parseInt(message.getStringAttribute("broadcast"));
        } catch (EaseMobException e) {
            e.printStackTrace();
        }

        ToolLog.dbg("iTitleId:" + String.valueOf(iTitleId) + " LocTitleId:" + String.valueOf(titleId));
        if((long)iTitleId != titleId){
            convertView = createViewByMessage(message, position, 1);
            convertView.setVisibility(View.GONE);
        }else{
            convertView.setVisibility(View.VISIBLE);
        }

        if((long)iTitleId == titleId) {
            TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // ������Ϣʱ���������Գ�����ʾʱ��
//                EMMessage prevMessage = null;
//                int iLastTitleId;
//                int iPos = position;
//                while (iPos>0) {
//                    prevMessage = getItem(iPos - 1);
//                    try {
//                        iLastTitleId = prevMessage.getIntAttribute("iTitleId");
//                        if((long)iLastTitleId == titleId) break;
//                        iPos--;
//                    } catch (EaseMobException e) {
//                        e.printStackTrace();
//                    }
//                }
                EMMessage prevMessage = getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                        timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                        timestamp.setVisibility(View.VISIBLE);
                }
            }
        }

        return convertView;
    }


    /**
     * ��ʾ�û�ͷ��
     * @param message
     * @param imageView
     */
    private void setUserAvatar(EMMessage message, ImageView imageView){
        if(message.direct == EMMessage.Direct.SEND){
            //��ʾ�Լ�ͷ��
            //���ǵĳ���û���û��Լ���ͷ���������
            //UserUtils.setUserAvatar(context, EMChatManager.getInstance().getCurrentUser(), imageView);
        }else{
            //UserUtils.setUserAvatar(context, message.getFrom(), imageView);
        }
    }

    /**
     * �ı���Ϣ
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleTextMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        Spannable span = SmileUtils.getSmiledText(context, txtBody.getMessage());
        // ��������
        holder.tv.setText(span, TextView.BufferType.SPANNABLE);
        // ���ó����¼�����
//        holder.tv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                activity.startActivityForResult(
//                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
//                                EMMessage.Type.TXT.ordinal()), ActivityChat.REQUEST_CODE_CONTEXT_MENU);
//                return true;
//            }
//        });
        if (message.direct == EMMessage.Direct.SEND) {
            switch (message.status) {
                case SUCCESS: // ���ͳɹ�
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                case FAIL: // ����ʧ��
                    holder.pb.setVisibility(View.GONE);
                    holder.staus_iv.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // ������
                    holder.pb.setVisibility(View.VISIBLE);
                    holder.staus_iv.setVisibility(View.GONE);
                    break;
                default:
                    // ������Ϣ
                    sendMsgInBackground(message, holder);
            }
        }
    }

    /**
     * ����Ƶͨ����¼
     *
     * @param message
     * @param holder
     * @param position
     */
    private void handleCallMessage(EMMessage message, ViewHolder holder, final int position) {
        TextMessageBody txtBody = (TextMessageBody) message.getBody();
        holder.tv.setText(txtBody.getMessage());

    }

    /**
     * ͼƬ��Ϣ
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleImageMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        //holder.pb.setTag(position);

        // ���շ������Ϣ
        if (message.direct == EMMessage.Direct.RECEIVE) {
            // "it is receive msg";
            if (message.status == EMMessage.Status.INPROGRESS) {
                // "!!!! back receive";
                holder.iv.setImageResource(R.drawable.default_image);
                showDownloadImageProgress(message, holder);
                // downloadImage(message, holder);
            } else {
                // "!!!! not back receive, show image directly");
                if(holder.pb!=null)holder.pb.setVisibility(View.GONE);
                if(holder.tv!=null)holder.tv.setVisibility(View.GONE);
                holder.iv.setImageResource(R.drawable.default_image);
                ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
                if (imgBody.getLocalUrl() != null) {
                    // String filePath = imgBody.getLocalUrl();
                    String remotePath = imgBody.getRemoteUrl();
                    String filePath = ImageUtils.getImagePath(remotePath);
                    String thumbRemoteUrl = imgBody.getThumbnailUrl();
                    String thumbnailPath = ImageUtils.getThumbnailImagePath(thumbRemoteUrl);
                    showImageView(thumbnailPath, holder.iv, filePath, imgBody.getRemoteUrl(), message, position);
                }
            }
            return;
        }

        // ���͵���Ϣ
        // process send message
        // send pic, show the pic directly
        ImageMessageBody imgBody = (ImageMessageBody) message.getBody();
        String filePath = imgBody.getLocalUrl();
        if (filePath != null && new File(filePath).exists()) {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message, position);
        } else {
            showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, IMAGE_DIR, message, position);
        }

        switch (message.status) {
            case SUCCESS:
                if(holder.pb!=null)holder.pb.setVisibility(View.GONE);
                //holder.tv.setVisibility(View.GONE);
                //holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                if(holder.pb!=null)holder.pb.setVisibility(View.GONE);
                //holder.tv.setVisibility(View.GONE);
                //holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                //holder.staus_iv.setVisibility(View.GONE);
                if(holder.pb!=null)holder.pb.setVisibility(View.VISIBLE);
                //holder.tv.setVisibility(View.VISIBLE);
                if (timers.containsKey(message.getMsgId()))
                    return;
                // set a timer
                final Timer timer = new Timer();
                timers.put(message.getMsgId(), timer);
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                if(holder.pb!=null)holder.pb.setVisibility(View.VISIBLE);
                                //holder.tv.setVisibility(View.VISIBLE);
                                //holder.tv.setText(message.progress + "%");
                                if (message.status == EMMessage.Status.SUCCESS) {
                                    if(holder.pb!=null)holder.pb.setVisibility(View.GONE);
                                    //holder.tv.setVisibility(View.GONE);
                                    // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
                                    timer.cancel();
                                } else if (message.status == EMMessage.Status.FAIL) {
                                    if(holder.pb!=null)holder.pb.setVisibility(View.GONE);
                                    //holder.tv.setVisibility(View.GONE);
                                    // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                                    // message.setProgress(0);
                                    //holder.staus_iv.setVisibility(View.VISIBLE);
                                    Toast.makeText(activity,
                                            activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT)
                                            .show();
                                    timer.cancel();
                                }

                            }
                        });

                    }
                }, 0, 500);
                break;
            default:
                sendPictureMessage(message, holder);
        }
    }

    /**
     * ��Ƶ��Ϣ
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
//    private void handleVideoMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
//
//        VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
//        // final File image=new File(PathUtil.getInstance().getVideoPath(),
//        // videoBody.getFileName());
//        String localThumb = videoBody.getLocalThumb();
//
//        holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
//
//            @Override
//            public boolean onLongClick(View v) {
//                activity.startActivityForResult(
//                        new Intent(activity, ContextMenu.class).putExtra("position", position).putExtra("type",
//                                EMMessage.Type.VIDEO.ordinal()), ActivityChat.REQUEST_CODE_CONTEXT_MENU);
//                return true;
//            }
//        });
//
//        if (localThumb != null) {
//            showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
//        }
//        if (videoBody.getLength() > 0) {
//            String time = DateUtils.toTimeBySecond(videoBody.getLength());
//            holder.timeLength.setText(time);
//        }
//        //holder.playBtn.setImageResource(R.drawable.video_download_btn_nor);
//
//        if (message.direct == EMMessage.Direct.RECEIVE) {
//            if (videoBody.getVideoFileLength() > 0) {
//                String size = TextFormater.getDataSize(videoBody.getVideoFileLength());
//                holder.size.setText(size);
//            }
//        } else {
//            if (videoBody.getLocalUrl() != null && new File(videoBody.getLocalUrl()).exists()) {
//                String size = TextFormater.getDataSize(new File(videoBody.getLocalUrl()).length());
//                holder.size.setText(size);
//            }
//        }
//
//        if (message.direct == EMMessage.Direct.RECEIVE) {
//
//            // System.err.println("it is receive msg");
//            if (message.status == EMMessage.Status.INPROGRESS) {
//                // System.err.println("!!!! back receive");
//                //holder.iv.setImageResource(R.drawable.default_image);
//                showDownloadImageProgress(message, holder);
//
//            } else {
//                // System.err.println("!!!! not back receive, show image directly");
//                //holder.iv.setImageResource(R.drawable.default_image);
//                if (localThumb != null) {
//                    showVideoThumbView(localThumb, holder.iv, videoBody.getThumbnailUrl(), message);
//                }
//
//            }
//
//            return;
//        }
//        holder.pb.setTag(position);
//
//        // until here ,deal with send video msg
//        switch (message.status) {
//            case SUCCESS:
//                holder.pb.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.GONE);
//                holder.tv.setVisibility(View.GONE);
//                break;
//            case FAIL:
//                holder.pb.setVisibility(View.GONE);
//                holder.tv.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.VISIBLE);
//                break;
//            case INPROGRESS:
//                if (timers.containsKey(message.getMsgId()))
//                    return;
//                // set a timer
//                final Timer timer = new Timer();
//                timers.put(message.getMsgId(), timer);
//                timer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        activity.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                holder.pb.setVisibility(View.VISIBLE);
//                                holder.tv.setVisibility(View.VISIBLE);
//                                holder.tv.setText(message.progress + "%");
//                                if (message.status == EMMessage.Status.SUCCESS) {
//                                    holder.pb.setVisibility(View.GONE);
//                                    holder.tv.setVisibility(View.GONE);
//                                    // message.setSendingStatus(Message.SENDING_STATUS_SUCCESS);
//                                    timer.cancel();
//                                } else if (message.status == EMMessage.Status.FAIL) {
//                                    holder.pb.setVisibility(View.GONE);
//                                    holder.tv.setVisibility(View.GONE);
//                                    // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
//                                    // message.setProgress(0);
//                                    holder.staus_iv.setVisibility(View.VISIBLE);
//                                    Toast.makeText(activity,
//                                            "fail2", Toast.LENGTH_SHORT)
//                                            .show();
//                                    timer.cancel();
//                                }
//
//                            }
//                        });
//
//                    }
//                }, 0, 500);
//                break;
//            default:
//                // sendMsgInBackground(message, holder);
//                sendPictureMessage(message, holder);
//
//        }
//
//    }

    /**
     * ������Ϣ
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
    private void handleVoiceMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
        VoiceMessageBody voiceBody = (VoiceMessageBody) message.getBody();
        holder.tv.setText(voiceBody.getLength() + "\"");
        holder.iv.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, activity, username));
        holder.iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.startActivityForResult(
                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
                                EMMessage.Type.VOICE.ordinal()), ActivityChat.REQUEST_CODE_CONTEXT_MENU);
                return true;
            }
        });
        if (((ActivityChat)activity).playMsgId != null
                && ((ActivityChat)activity).playMsgId.equals(message
                .getMsgId())&&VoicePlayClickListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv.setImageResource(R.anim.voice_from_icon);
            } else {
                holder.iv.setImageResource(R.anim.voice_to_icon);
            }
            voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
            voiceAnimation.start();
        } else {
            if (message.direct == EMMessage.Direct.RECEIVE) {
                holder.iv.setImageResource(R.drawable.voice_received_play_3);
            } else {
                holder.iv.setImageResource(R.drawable.voice_send_play_3);
            }
        }


        if (message.direct == EMMessage.Direct.RECEIVE) {
            if (message.isListened()) {
                // ��������δ���־
                holder.iv_read_status.setVisibility(View.INVISIBLE);
            } else {
                holder.iv_read_status.setVisibility(View.VISIBLE);
            }
            System.err.println("it is receive msg");
            if (message.status == EMMessage.Status.INPROGRESS) {
                if(holder.pb!=null)holder.pb.setVisibility(View.VISIBLE);
                System.err.println("!!!! back receive");
                ((FileMessageBody) message.getBody()).setDownloadCallback(new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(holder.pb!=null)holder.pb.setVisibility(View.INVISIBLE);
                                notifyDataSetChanged();
                            }
                        });

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if(holder.pb!=null)holder.pb.setVisibility(View.INVISIBLE);
                            }
                        });

                    }
                });

            } else {
                if(holder.pb!=null)holder.pb.setVisibility(View.INVISIBLE);

            }
            return;
        }

        // until here, deal with send voice msg
        switch (message.status) {
            case SUCCESS:
//                holder.pb.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.GONE);
                break;
            case FAIL:
                if(holder.pb!=null)holder.pb.setVisibility(View.GONE);
                holder.staus_iv.setVisibility(View.VISIBLE);
                break;
            case INPROGRESS:
                if(holder.pb!=null)holder.pb.setVisibility(View.VISIBLE);
                holder.staus_iv.setVisibility(View.GONE);
                break;
            default:
                sendMsgInBackground(message, holder);
        }
    }

    /**
     * �ļ���Ϣ
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
//    private void handleFileMessage(final EMMessage message, final ViewHolder holder, int position, View convertView) {
//        final NormalFileMessageBody fileMessageBody = (NormalFileMessageBody) message.getBody();
//        final String filePath = fileMessageBody.getLocalUrl();
//        holder.tv_file_name.setText(fileMessageBody.getFileName());
//        holder.tv_file_size.setText(TextFormater.getDataSize(fileMessageBody.getFileSize()));
//        holder.ll_container.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                File file = new File(filePath);
//                if (file != null && file.exists()) {
//                    // �ļ����ڣ�ֱ�Ӵ�
//                    FileUtils.openFile(file, (Activity) context);
//                } else {
//                    // ����
//                    context.startActivity(new Intent(context, ShowNormalFileActivity.class).putExtra("msgbody", fileMessageBody));
//                }
//                if (message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
//                    try {
//                        EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//                        message.isAcked = true;
//                    } catch (EaseMobException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        String st1 = context.getResources().getString(R.string.Have_downloaded);
//        String st2 = context.getResources().getString(R.string.Did_not_download);
//        if (message.direct == EMMessage.Direct.RECEIVE) { // ���յ���Ϣ
//            System.err.println("it is receive msg");
//            File file = new File(filePath);
//            if (file != null && file.exists()) {
//                holder.tv_file_download_state.setText(st1);
//            } else {
//                holder.tv_file_download_state.setText(st2);
//            }
//            return;
//        }
//
//        // until here, deal with send voice msg
//        switch (message.status) {
//            case SUCCESS:
//                holder.pb.setVisibility(View.INVISIBLE);
//                holder.tv.setVisibility(View.INVISIBLE);
//                holder.staus_iv.setVisibility(View.INVISIBLE);
//                break;
//            case FAIL:
//                holder.pb.setVisibility(View.INVISIBLE);
//                holder.tv.setVisibility(View.INVISIBLE);
//                holder.staus_iv.setVisibility(View.VISIBLE);
//                break;
//            case INPROGRESS:
//                if (timers.containsKey(message.getMsgId()))
//                    return;
//                // set a timer
//                final Timer timer = new Timer();
//                timers.put(message.getMsgId(), timer);
//                timer.schedule(new TimerTask() {
//
//                    @Override
//                    public void run() {
//                        activity.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                holder.pb.setVisibility(View.VISIBLE);
//                                holder.tv.setVisibility(View.VISIBLE);
//                                holder.tv.setText(message.progress + "%");
//                                if (message.status == EMMessage.Status.SUCCESS) {
//                                    holder.pb.setVisibility(View.INVISIBLE);
//                                    holder.tv.setVisibility(View.INVISIBLE);
//                                    timer.cancel();
//                                } else if (message.status == EMMessage.Status.FAIL) {
//                                    holder.pb.setVisibility(View.INVISIBLE);
//                                    holder.tv.setVisibility(View.INVISIBLE);
//                                    holder.staus_iv.setVisibility(View.VISIBLE);
//                                    Toast.makeText(activity,
//                                            activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0)
//                                            .show();
//                                    timer.cancel();
//                                }
//
//                            }
//                        });
//
//                    }
//                }, 0, 500);
//                break;
//            default:
//                // ������Ϣ
//                sendMsgInBackground(message, holder);
//        }
//
//    }

    /**
     * ����λ����Ϣ
     *
     * @param message
     * @param holder
     * @param position
     * @param convertView
     */
//    private void handleLocationMessage(final EMMessage message, final ViewHolder holder, final int position, View convertView) {
//        TextView locationView = ((TextView) convertView.findViewById(R.id.tv_location));
//        LocationMessageBody locBody = (LocationMessageBody) message.getBody();
//        locationView.setText(locBody.getAddress());
//        LatLng loc = new LatLng(locBody.getLatitude(), locBody.getLongitude());
//        locationView.setOnClickListener(new MapClickListener(loc, locBody.getAddress()));
//        locationView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                activity.startActivityForResult(
//                        (new Intent(activity, ContextMenu.class)).putExtra("position", position).putExtra("type",
//                                EMMessage.Type.LOCATION.ordinal()), ActivityChat.REQUEST_CODE_CONTEXT_MENU);
//                return false;
//            }
//        });
//
//        if (message.direct == EMMessage.Direct.RECEIVE) {
//            return;
//        }
//        // deal with send message
//        switch (message.status) {
//            case SUCCESS:
//                holder.pb.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.GONE);
//                break;
//            case FAIL:
//                holder.pb.setVisibility(View.GONE);
//                holder.staus_iv.setVisibility(View.VISIBLE);
//                break;
//            case INPROGRESS:
//                holder.pb.setVisibility(View.VISIBLE);
//                break;
//            default:
//                sendMsgInBackground(message, holder);
//        }
//    }

    /**
     * ������Ϣ
     *
     * @param message
     * @param holder
     * @param position
     */
    public void sendMsgInBackground(final EMMessage message, final ViewHolder holder) {
        //holder.staus_iv.setVisibility(View.GONE);
        //holder.pb.setVisibility(View.VISIBLE);

        final long start = System.currentTimeMillis();
        // ����sdk�����첽���ͷ���
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onSuccess() {

                updateSendedView(message, holder);
            }

            @Override
            public void onError(int code, String error) {

                updateSendedView(message, holder);
            }

            @Override
            public void onProgress(int progress, String status) {
            }

        });

    }

    /*
     * chat sdk will automatic download thumbnail image for the image message we
     * need to register callback show the download progress
     */
    private void showDownloadImageProgress(final EMMessage message, final ViewHolder holder) {
        System.err.println("!!! show download image progress");
        // final ImageMessageBody msgbody = (ImageMessageBody)
        // message.getBody();
        final FileMessageBody msgbody = (FileMessageBody) message.getBody();
        if(holder.pb!=null)
            holder.pb.setVisibility(View.VISIBLE);
        if(holder.tv!=null)
            holder.tv.setVisibility(View.VISIBLE);

        msgbody.setDownloadCallback(new EMCallBack() {

            @Override
            public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // message.setBackReceive(false);
                        if (message.getType() == EMMessage.Type.IMAGE) {
                            holder.pb.setVisibility(View.GONE);
                            holder.tv.setVisibility(View.GONE);
                        }
                        notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void onProgress(final int progress, String status) {
                if (message.getType() == EMMessage.Type.IMAGE) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv.setText(progress + "%");

                        }
                    });
                }

            }

        });
    }

    /*
     * send message with new sdk
     */
    private void sendPictureMessage(final EMMessage message, final ViewHolder holder) {
        try {
            String to = message.getTo();

            // before send, update ui
            //holder.staus_iv.setVisibility(View.GONE);
            holder.pb.setVisibility(View.VISIBLE);
            //holder.tv.setVisibility(View.VISIBLE);
            //holder.tv.setText("0%");

            final long start = System.currentTimeMillis();
            // if (chatType == ChatActivity.CHATTYPE_SINGLE) {
            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "send image message successfully");
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            // send success
                            holder.pb.setVisibility(View.GONE);
                            //holder.tv.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {

                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            holder.pb.setVisibility(View.GONE);
                            //holder.tv.setVisibility(View.GONE);
                            // message.setSendingStatus(Message.SENDING_STATUS_FAIL);
                            //holder.staus_iv.setVisibility(View.VISIBLE);
                            Toast.makeText(activity,
                                    "fail1", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            //holder.tv.setText(progress + "%");
                        }
                    });
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ����ui����Ϣ����״̬
     *
     * @param message
     * @param holder
     */
    private void updateSendedView(final EMMessage message, final ViewHolder holder) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // send success
                if (message.getType() == EMMessage.Type.VIDEO) {
                    holder.tv.setVisibility(View.GONE);
                }
                System.out.println("message status : " + message.status);
                if (message.status == EMMessage.Status.SUCCESS) {
                    // if (message.getType() == EMMessage.Type.FILE) {
                    // holder.pb.setVisibility(View.INVISIBLE);
                    // holder.staus_iv.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.pb.setVisibility(View.GONE);
                    // holder.staus_iv.setVisibility(View.GONE);
                    // }

                } else if (message.status == EMMessage.Status.FAIL) {
                    // if (message.getType() == EMMessage.Type.FILE) {
                    // holder.pb.setVisibility(View.INVISIBLE);
                    // } else {
                    // holder.pb.setVisibility(View.GONE);
                    // }
                    // holder.staus_iv.setVisibility(View.VISIBLE);
                    Toast.makeText(activity, "fail3", Toast.LENGTH_SHORT)
                            .show();
                }

                notifyDataSetChanged();
            }
        });
    }

    /**
     * load image into image view
     *
     * @param thumbernailPath
     * @param iv
     * @param position
     * @return the image exists or not
     */
    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, String remoteDir,
                                  final EMMessage message, final int position) {
        // String imagename =
        // localFullSizePath.substring(localFullSizePath.lastIndexOf("/") + 1,
        // localFullSizePath.length());
        // final String remote = remoteDir != null ? remoteDir+imagename :
        // imagename;
        LayoutInflater inflater = activity.getLayoutInflater();
        bigImage = inflater.inflate(R.layout.view_photo, null);
        final String remote = remoteDir;
        ToolLog.dbg("local = " + localFullSizePath + " remote: " + remote);
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            //Bitmap coverBitmap = BitmapFactory.decodeResource();
            iv.setImageResource(R.drawable.default_image);
            iv.setClickable(true);
            iv.setTag(R.id.tag_message, message);
            iv.setTag(R.id.tag_position,position);
            iv.setOnTouchListener(bigImageOnTouchListener);
            iv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    System.err.println("image view on click");
                    //Intent intent = new Intent(activity, ActivityShowBigImage.class);
                    File file = new File(localFullSizePath);
                    Uri uri = null;
                    String bodySecret = "";
                    if (file.exists()) {
                        uri = Uri.fromFile(file);
                        //intent.putExtra("uri", uri);
                        System.err.println("here need to check why download everytime");
                    } else {
                        // The local full size pic does not exist yet.
                        // ShowBigImage needs to download it from the server
                        // first
                        // intent.putExtra("", message.get);
                        ImageMessageBody body = (ImageMessageBody) message.getBody();
                        bodySecret = body.getSecret();
                        //intent.putExtra("secret", body.getSecret());
                        //intent.putExtra("remotepath", remote);
                    }
                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
                            && message.getChatType() != EMMessage.ChatType.GroupChat) {
                        try {
                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
                            message.isAcked = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //activity.startActivity(intent);

                    toolShowBigImage.start_show_photo(uri, remote, "");
                    return false;
                }
            });
            return true;
        } else {
            new LoadImageTask().execute(thumbernailPath, localFullSizePath, remote, message.getChatType(), iv, activity, message, conversation, position);
            notifyDataSetChanged();
            return true;
        }

    }

    /**
     * չʾ��Ƶ����ͼ
     *
     * @param localThumb
     *            ��������ͼ·��
     * @param iv
     * @param thumbnailUrl
     *            Զ������ͼ·��
     * @param message
     */
//    private void showVideoThumbView(String localThumb, ImageView iv, String thumbnailUrl, final EMMessage message) {
//        // first check if the thumbnail image already loaded into cache
//        Bitmap bitmap = ImageCache.getInstance().get(localThumb);
//        if (bitmap != null) {
//            // thumbnail image is already loaded, reuse the drawable
//            iv.setImageBitmap(bitmap);
//            iv.setClickable(true);
//            iv.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    VideoMessageBody videoBody = (VideoMessageBody) message.getBody();
//                    System.err.println("video view is on click");
//                    Intent intent = new Intent(activity, ShowVideoActivity.class);
//                    intent.putExtra("localpath", videoBody.getLocalUrl());
//                    intent.putExtra("secret", videoBody.getSecret());
//                    intent.putExtra("remotepath", videoBody.getRemoteUrl());
//                    if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked
//                            && message.getChatType() != EMMessage.ChatType.GroupChat) {
//                        message.isAcked = true;
//                        try {
//                            EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    activity.startActivity(intent);
//
//                }
//            });
//
//        } else {
//            new LoadVideoImageTask().execute(localThumb, thumbnailUrl, iv, activity, message, this);
//        }
//
//    }

    public static class ViewHolder {
        ImageView iv;
        TextView tv;
        ProgressBar pb;
        ImageView staus_iv;
        ImageView iv_avatar;
        TextView tv_usernick;
        ImageView playBtn;
        TextView timeLength;
        TextView size;
        LinearLayout container_status_btn;
        LinearLayout ll_container;
        ImageView iv_read_status;
        // ��ʾ�Ѷ���ִ״̬
        TextView tv_ack;
        // ��ʾ�ʹ��ִ״̬
        TextView tv_delivered;

        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_file_download_state;
    }

    /*
     * �����ͼ��Ϣlistener
     */
//    class MapClickListener implements View.OnClickListener {
//
//        LatLng location;
//        String address;
//
//        public MapClickListener(LatLng loc, String address) {
//            location = loc;
//            this.address = address;
//
//        }
//
//        @Override
//        public void onClick(View v) {
//            Intent intent;
//            intent = new Intent(context, BaiduMapActivity.class);
//            intent.putExtra("latitude", location.latitude);
//            intent.putExtra("longitude", location.longitude);
//            intent.putExtra("address", address);
//            activity.startActivity(intent);
//        }
//
//    }

    protected void postLoadBigImage(EMMessage message, int position){
        toolShowBigImage.gonePhotoView();
        conversation.removeMessage(message.getMsgId());
        ListView listView = ((ActivityChat)activity).getListView();
        listView.getChildAt(position).setVisibility(View.GONE);
        notifyDataSetChanged();
        refresh();
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
