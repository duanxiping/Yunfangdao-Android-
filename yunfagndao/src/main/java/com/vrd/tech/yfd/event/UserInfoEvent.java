package com.vrd.tech.yfd.event;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;

import com.victor.loading.book.BookLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.databinding.ActivityUserInfoBinding;
import com.vrd.tech.yfd.entity.LoginInfo;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.entity.WiUserInfo;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;
import com.vrd.tech.yfd.view.CustomDatePick;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.HashMap;

import cn.aigestudio.datepicker.bizs.themes.DPTManager;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by tsang on 16/4/25.
 */
public class UserInfoEvent {
    private Context mContext;
    private AlertDialog dialog;
    private LoginInfo mLoginInfo;
    private ActivityUserInfoBinding binding;
    private CompositeSubscription compositeSubscription;
    private boolean isSendingRequest = false;
    private MobileApi mobileApi;

    public UserInfoEvent(Context context, ActivityUserInfoBinding binding) {
        mContext = context;
        this.binding = binding;
        mobileApi = NetWorkHelper.getInstance().getMobileApi();
        compositeSubscription = new CompositeSubscription();
    }

    /**
     * 年检是否开启按钮事件
     *
     * @param userInfo
     * @param isChecked
     */
    public void onAlertToggleChange(WiUserInfo userInfo, boolean isChecked) {
        if (isChecked) {
            Log.i("zeng", "选中");
            userInfo.setAnnual_inspect_alert(true);
        } else {
            Log.i("zeng", "取消");
            userInfo.setAnnual_inspect_alert(false);
        }
    }

    /**
     * 显示日期选择对话框，点击tv时出现
     *
     * @param userInfo
     */
    public void showDatePicker(final WiUserInfo userInfo) {
        Calendar calendar = Calendar.getInstance();
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        DPTManager.getInstance().initCalendar(new CustomDatePick());
        DatePicker picker = new DatePicker(mContext);
        picker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
        picker.setMode(DPMode.SINGLE);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                //获取到日期
                userInfo.setAnnual_inspect_date(date);
                dialog.dismiss();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setContentView(picker, params);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    /**
     * 提交更改
     *
     * @param userInfo
     */
    public void submmitAlterInfo(WiUserInfo userInfo) {
        if (isSendingRequest) {
            return;
        }
        final HashMap<String, String> options = new HashMap<String, String>();
        options.put("_cust_id", userInfo.getCust_id());
        options.put("cust_name", userInfo.getCust_name());
        options.put("access_token", mLoginInfo.getAccess_token());
        if (userInfo.isAnnual_inspect_alert()) {
            options.put("annual_inspect_date", userInfo.getAnnual_inspect_date());
        } else {
            if (TextUtils.isEmpty(userInfo.getAnnual_inspect_date()) || userInfo.getAnnual_inspect_date().contains("1970-01-01")) {
                //不修改，同时修改cust_name不生效
            } else {
                options.put("annual_inspect_date", "");
            }
        }
        isSendingRequest = true;
        showWaitingDialog();
        Subscription sub = mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Wicare_User_Update, options)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new AlterUserInfoSubscriber());
        compositeSubscription.add(sub);
    }

    /**
     * 提交更改回调
     */
    class AlterUserInfoSubscriber extends Subscriber<StatusCode> {
        @Override
        public void onCompleted() {
            isSendingRequest = false;
            dismissWaitingDialog();
        }

        @Override
        public void onError(Throwable e) {
            isSendingRequest = false;
            dismissWaitingDialog();
            if (e.getMessage() == null || e.getMessage().contains("Failed to connect to")) {
                MyUtil.showToast(mContext.getApplicationContext(), mContext.getString(R.string.network_exception));
            } else {
                MyUtil.showToast(mContext.getApplicationContext(), mContext.getString(R.string.alter_user_info_fail));
            }
        }

        @Override
        public void onNext(StatusCode statusCode) {
            if (statusCode != null && statusCode.getStatus_code() == 0) {
                MyUtil.showToast(mContext.getApplicationContext(), mContext.getString(R.string.alter_user_info_success));
            } else {
                Log.i("zeng", "xiugai");
                MyUtil.showToast(mContext.getApplicationContext(), mContext.getString(R.string.alter_user_info_fail));
            }
        }
    }

    public void showWaitingDialog() {
        dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setContentView(R.layout.waiting_content_view);
        dialog.getWindow().setGravity(Gravity.CENTER);
        BookLoading loading = (BookLoading) dialog.getWindow().findViewById(R.id.bookLoading);
        loading.start();
    }

    private void dismissWaitingDialog() {
        if (dialog != null) {
            BookLoading loading = (BookLoading) dialog.getWindow().findViewById(R.id.bookLoading);
            loading.stop();
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 获取用户信息
     */
    public void getUserInfoFromInternet() {
        Subscription s = Observable.create(new Observable.OnSubscribe<LoginInfo>() {
            @Override
            public void call(Subscriber<? super LoginInfo> subscriber) {
                deserializationLoginInfo(subscriber);
            }
        }).subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                showWaitingDialog();
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Func1<LoginInfo, Observable<WiUserInfo>>() {
            @Override
            public Observable<WiUserInfo> call(LoginInfo loginInfo) {
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("cust_id", loginInfo.getCust_id());
                options.put("access_token", loginInfo.getAccess_token());
                String fields = "cust_id,cust_name,annual_inspect_date,create_time,update_time,mobile,password";
                return mobileApi.getUserInfo(APIUtil.createOptions(MobileApi.Wicare_User_Get, options, fields));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<WiUserInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingDialog();
                MyUtil.showToast(mContext.getApplicationContext(), mContext.getString(R.string.network_exception));
            }

            @Override
            public void onNext(WiUserInfo userInfo) {
                dismissWaitingDialog();
                if (TextUtils.isEmpty(userInfo.getAnnual_inspect_date()) || userInfo.getAnnual_inspect_date().contains("1970-01-01")) {
                    userInfo.setAnnual_inspect_alert(false);
                } else {
                    userInfo.setAnnual_inspect_alert(true);
                }
                binding.setUserInfo(userInfo);
                Log.i("yyyyyyyyyyyyyyyyyyyyyyy","我的信息"+userInfo);
            }
        });
        compositeSubscription.add(s);
    }


    private void deserializationLoginInfo(Subscriber<? super LoginInfo> subscriber) {
        File dir = mContext.getFilesDir();
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(new File(dir.getPath() + "loginInfo")));
            LoginInfo loginInfo = (LoginInfo) ois.readObject();
            mLoginInfo = loginInfo;
            if (loginInfo != null)
                subscriber.onNext(loginInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            MyUtil.IOClose(ois);
        }
    }

    public void clearSubscription() {
        compositeSubscription.clear();
    }

    /**
     * 用户名输入监听
     *
     * @param s
     * @param userInfo
     */
    public void onTextChanged(CharSequence s, WiUserInfo userInfo) {
        if (!userInfo.getCust_name().equals(s.toString())) {
            userInfo.setCust_name(s.toString());
        }
    }

    public String dateHandler(String date) {
        if (TextUtils.isEmpty(date) || date.contains("1970-01-01")) {
            return mContext.getString(R.string.click_for_setting_date);
        } else if (date.length() == 24) {
            return MyUtil.changeTime(date, 1);
        } else {
            return date;
        }
    }
}
