package com.vrd.tech.yfd;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.NetWorkHelper;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by tsang on 16/7/1.
 */
public class VRDApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        NetWorkHelper.init(this);
        APIUtil.initKey(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);
    }
}
