package com.mymonitor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymonitor.XRecyclerView.XRecyclerView;

import java.util.List;

/**
 * @ClassName: MyAdapter
 * @author: 张京伟
 * @date: 2016/12/29 14:45
 * @Description:
 * @version: 1.0
 */
public class MyAdapter extends XRecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<NotificationBean> notificationBeans;

    public void setNotificationBeans(List<NotificationBean> notificationBeans) {
        this.notificationBeans = notificationBeans;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        NotificationBean notificationBean = notificationBeans.get(position);
        if (notificationBean != null) {
            if (notificationBean.drawable == null){
                holder.iv_icon.setImageResource(R.drawable.ic_launcher);
            }else{
                holder.iv_icon.setImageDrawable(notificationBean.drawable);
            }
            holder.tv_title.setText(notificationBean.title);
            holder.tv_time.setText(notificationBean.timeText);
            holder.tv_message.setText(notificationBean.message);
        }
    }

    @Override
    public int getItemCount() {
        return notificationBeans != null ? notificationBeans.size() : 0;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public class ViewHolder extends XRecyclerView.ViewHolder {

        private ImageView iv_icon;
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_message;

        public ViewHolder(View view) {
            super(view);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
        }
    }
}
