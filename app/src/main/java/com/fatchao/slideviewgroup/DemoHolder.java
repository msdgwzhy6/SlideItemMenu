package com.fatchao.slideviewgroup;

import android.view.View;
import android.widget.TextView;

/**
 * author pangchao
 * created on 2017/6/12
 * email fat_chao@163.com.
 */

class DemoHolder extends RvHolder<String> implements View.OnClickListener {
    private TextView tvDemo, btnDemo;

    public DemoHolder(View itemView, int type, RvListener listener) {
        super(itemView, type, listener);
        tvDemo = (TextView) itemView.findViewById(R.id.tv_demo);
        btnDemo = (TextView) itemView.findViewById(R.id.btn_menu);
        tvDemo.setOnClickListener(this);
        btnDemo.setOnClickListener(this);
    }

    @Override
    public void bindHolder(String s, int position) {
        tvDemo.setText("测试数据" + s);
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v.getId(), getAdapterPosition());
    }
}
