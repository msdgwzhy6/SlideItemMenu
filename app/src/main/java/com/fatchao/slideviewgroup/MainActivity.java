package com.fatchao.slideviewgroup;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvDemo;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvDemo = (RecyclerView) findViewById(R.id.rv_demo);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(this);
        final String[] stringArray = getResources().getStringArray(R.array.pill);

        DemoAdapter demoAdapter = new DemoAdapter(this, Arrays.asList(stringArray), new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                switch (id) {
                    case R.id.tv_demo:
                        Snackbar.make(rvDemo, "内容区域" + position, 3000).show();
                        break;
                    case R.id.btn_menu:
                        Snackbar.make(rvDemo, "菜单" + position, Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        rvDemo.setLayoutManager(new LinearLayoutManager(this));
        rvDemo.setAdapter(demoAdapter);
        rvDemo.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onRefresh() {
        rvDemo.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }
}
