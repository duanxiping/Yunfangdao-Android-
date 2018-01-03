package com.vrd.tech.yfd.activity;

import android.content.Context;

import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.util.MyUtil;

import rx.Subscriber;

/**
 * Created by Administrator on 2016/6/8.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {
    protected Context context;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onCompleted() {
        onSubEnd();
    }

    @Override
    public void onError(Throwable e) {
        onSubEnd();
        if (e.getMessage() == null || e.getMessage().contains("Failed to connect to")) {
            MyUtil.showToast(context, context.getString(R.string.network_exception));
        } else {
            onSubError(e);
        }
    }

    protected abstract void onSubEnd();

    protected abstract void onSubError(Throwable e);
}
