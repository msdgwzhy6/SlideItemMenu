package com.fatchao.slideviewgroup;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * author pangchao
 * created on 2017/6/12
 * email fat_chao@163.com.
 */

public class DemoAdapter extends RvAdapter<String> {

    public DemoAdapter(Context context, List<String> list, RvListener listener) {
        super(context, list, listener);
    }

    @Override
    protected int getLayoutId(int viewType) {
        return R.layout.item_slide;
    }

    @Override
    protected RvHolder getHolder(View view, int viewType) {
        return new DemoHolder(view,viewType,listener);
    }
}
