package com.yunkairichu.cike.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaf.jcore.Application;
import com.yunkairichu.cike.main.ActivitySquare;
import com.yunkairichu.cike.main.ActivityTakePhoto;
import com.yunkairichu.cike.main.R;
import com.yunkairichu.cike.utils.CommonUtils;
import com.yunkairichu.cike.widget.StatusSelectorLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaobo on 15/5/28.
 */

//自定义适配器
public class PictureAdapter extends BaseAdapter {
    //本类常量
    public static final int REQUEST_CODE_CAMERA = 18;

    private LayoutInflater inflater;
    private List<Picture> pictures;
    private Activity activity;

    private int sendMsgTag = -1;
    private SendChoStaOnClickListener sendChoStaOnClickListener = new SendChoStaOnClickListener();

    public PictureAdapter(String[] titles, int[] images, Context context, Context context2)
    {
        super();
        pictures = new ArrayList<Picture>();
        inflater = LayoutInflater.from(context);
        activity = (Activity) context2;
        for (int i = 0; i < images.length; i++)
        {
            Picture picture = new Picture(titles[i], images[i]);
            pictures.add(picture);
        }
    }

    @Override
    public int getCount()
    {
        if (null != pictures)
        {
            return pictures.size();
        } else
        {
            return 0;
        }
    }

    @Override
    public Object getItem(int position)
    {
        return pictures.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.status_grid_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(pictures.get(position).getTitle());
        viewHolder.image.setImageResource(pictures.get(position).getImageId());
        viewHolder.image.setTag(R.id.tag_msg_tag, position + 1);
        viewHolder.image.setOnClickListener(sendChoStaOnClickListener);
        return convertView;
    }

    class ViewHolder
    {
        public TextView title;
        public ImageView image;
    }

    class Picture
    {
        private String title;
        private int imageId;

        public Picture()
        {
            super();
        }

        public Picture(String title, int imageId)
        {
            super();
            this.title = title;
            this.imageId = imageId;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public int getImageId()
        {
            return imageId;
        }

        public void setImageId(int imageId)
        {
            this.imageId = imageId;
        }
    }

    class SendChoStaOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (!CommonUtils.isExitsSdcard()) {
                String st = activity.getResources().getString(R.string.sd_card_does_not_exist);
                Toast.makeText(activity.getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                return;
            }
//
//            sendMsgTag = (int)view.getTag(R.id.tag_msg_tag);
//            cameraFile = new File(PathUtil.getInstance().getImagePath(), Application.getInstance().getUserName()
//                    + System.currentTimeMillis() + ".jpg");
//            cameraFile.getParentFile().mkdirs();
//            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
//                    REQUEST_CODE_CAMERA);

            sendMsgTag = (int)view.getTag(R.id.tag_msg_tag);
            Application.getInstance().squareGender = 0;
            Application.getInstance().squareStatus = sendMsgTag;
            Application.getInstance().squareLoc = 0;
            Intent i = new Intent((ActivitySquare)activity, ActivityTakePhoto.class);
            Bundle bundle = new Bundle();
            bundle.putInt("msgTag", StatusSelectorLayout.statusNameNum[sendMsgTag]);
            i.putExtras(bundle);
            ((ActivitySquare)activity).clearTitleBitmap();
            //((ActivitySquare)activity).getSquareScrollView().removeAllViews();
            ((ActivitySquare)activity).getSquareScrollView().setVisibility(View.GONE);
//            ((ActivitySquare)activity).cancelTimer2();
            ((ActivitySquare)activity).startActivityForResult(i, REQUEST_CODE_CAMERA);
            ((ActivitySquare)activity).overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
            sendMsgTag = -1;
        }
    }

}

