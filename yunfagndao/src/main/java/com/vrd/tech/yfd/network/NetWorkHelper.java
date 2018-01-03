package com.vrd.tech.yfd.network;

import android.content.Context;

import com.vrd.tech.yfd.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tsang on 16/3/25.
 */
public class NetWorkHelper {
    private static NetWorkHelper mInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mClient;
    private HostSelectInterceptor hostSelectInterceptor;
    private Context mContext;
    private MobileApi mobileApi;

    private NetWorkHelper(Context context) {
        mContext = context;
        hostSelectInterceptor = new HostSelectInterceptor();
        mClient = new OkHttpClient.Builder().addInterceptor(hostSelectInterceptor).build();
        mRetrofit = new Retrofit.Builder().baseUrl(mContext.getString(R.string.server_ip_papa)).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).callFactory(mClient).build();
        mobileApi = mRetrofit.create(MobileApi.class);
    }

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new NetWorkHelper(context);
        }
    }

    public static NetWorkHelper getInstance() {
        if (mInstance == null) {
            return null;
        } else {
            return mInstance;
        }
    }


    public MobileApi getMobileApi() {
        return mobileApi;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

//    public void changeServer(String serverIp) {
//        hostSelectInterceptor.setHost(serverIp);
//    }
}
