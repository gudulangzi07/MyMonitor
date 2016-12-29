package com.mymonitor.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

import com.mymonitor.R;
import com.mymonitor.adapter.MonitorListAdapter;
import com.mymonitor.widget.XRecyclerView.ProgressStyle;
import com.mymonitor.widget.XRecyclerView.XRecyclerView;
import com.mymonitor.widget.XRecyclerView.decoration.DividerItemDecoration;

/**
 * Created by 张京伟 on 2016-12-29.
 */

public class MonitorListActivity extends AppCompatActivity {

    private XRecyclerView xRecyclerView;
    private MonitorListAdapter monitorListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        monitorListAdapter = new MonitorListAdapter();

        setContentView(R.layout.activity_monitor_list);

        xRecyclerView = (XRecyclerView) findViewById(R.id.xRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(linearLayoutManager);

        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        xRecyclerView.setAdapter(monitorListAdapter);
    }
}
