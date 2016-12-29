package com.mymonitor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mymonitor.R;
import com.mymonitor.bean.MonitorListBean;
import com.mymonitor.widget.XRecyclerView.XRecyclerView;

import java.util.List;

/**
 * Created by 张京伟 on 2016-12-29.
 */

public class MonitorListAdapter extends XRecyclerView.Adapter<MonitorListAdapter.ViewHolder> {

    private List<MonitorListBean> monitorListBeans;

    public void setMonitorListBeans(List<MonitorListBean> monitorListBeans) {
        this.monitorListBeans = monitorListBeans;
    }

    @Override
    public MonitorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MonitorListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monitor_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MonitorListAdapter.ViewHolder holder, int position) {
        MonitorListBean monitorListBean = monitorListBeans.get(position);
        if (monitorListBean != null){
        }
    }

    @Override
    public int getItemCount() {
        return monitorListBeans != null ? monitorListBeans.size() : 0;
    }

    public class ViewHolder extends XRecyclerView.ViewHolder {

        private CheckBox cb_btn;
        private TextView tv_name;

        public ViewHolder(View view) {
            super(view);
            cb_btn = (CheckBox) view.findViewById(R.id.cb_btn);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
        }
    }
}
