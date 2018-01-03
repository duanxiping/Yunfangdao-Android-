package com.vrd.tech.yfd.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.DeviceInfo;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class OBDActivity extends AppCompatActivity {

    @Bind(R.id.imageScanner)
    ImageView imageScanner;
    @Bind(R.id.circleLeft1)
    ImageView circleLeft1;
    @Bind(R.id.circleLeft2)
    ImageView circleLeft2;
    @Bind(R.id.circleRight1)
    ImageView circleRight1;
    @Bind(R.id.circleRight2)
    ImageView circleRight2;
    String deviceId;
    String token;
    Subscription subscription;
    @Bind(R.id.carHealthyScore)
    TextView carHealthyScore;
    @Bind(R.id.dyVal)
    TextView dyVal;
    @Bind(R.id.dyStateDescription)
    TextView dyStateDescription;
    @Bind(R.id.dyState)
    ImageView dyState;
    @Bind(R.id.swVal)
    TextView swVal;
    @Bind(R.id.swStateDescription)
    TextView swStateDescription;
    @Bind(R.id.swState)
    ImageView swState;
    @Bind(R.id.fdjzsVal)
    TextView fdjzsVal;
    @Bind(R.id.fdjzsStateDescription)
    TextView fdjzsStateDescription;
    @Bind(R.id.fdjzsState)
    ImageView fdjzsState;
    @Bind(R.id.jqmkdVal)
    TextView jqmkdVal;
    @Bind(R.id.jqmkdStateDescription)
    TextView jqmkdStateDescription;
    @Bind(R.id.jqmkdState)
    ImageView jqmkdState;
    @Bind(R.id.sychjVal)
    TextView sychjVal;
    @Bind(R.id.sychjStateDescription)
    TextView sychjStateDescription;
    @Bind(R.id.sychjState)
    ImageView sychjState;
    @Bind(R.id.fdjfhVal)
    TextView fdjfhVal;
    @Bind(R.id.fdjfhStateDescription)
    TextView fdjfhStateDescription;
    @Bind(R.id.fdjfhState)
    ImageView fdjfhState;
    @Bind(R.id.syylVal)
    TextView syylVal;
    @Bind(R.id.syylStateDescription)
    TextView syylStateDescription;
    @Bind(R.id.syylState)
    ImageView syylState;
    @Bind(R.id.dhtqjVal)
    TextView dhtqjVal;
    @Bind(R.id.dhtqjStateDescription)
    TextView dhtqjStateDescription;
    @Bind(R.id.dhtqjState)
    ImageView dhtqjState;
    private ObjectAnimator animator;//汽车扫描动画
    private boolean dpdyException = false;
    private boolean jqmkdException = false;
    private boolean fdjzsException = false;
    private boolean swException = false;
    private boolean sychjException = false;
    private boolean dhtqjException = false;
    private int healthyValue = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obd);
        ButterKnife.bind(this);
        float w = (float) (MyUtil.getScreenWidthAndHeight(getApplicationContext())[0] * 0.8375);
        animator = ObjectAnimator.ofFloat(imageScanner, "translationX", 0.0f, w).setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("device_id");
        token = intent.getStringExtra("access_token");
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("device_id", deviceId);
        options.put("access_token", token);
        String fields = "serial,active_obd_data";
        subscription = NetWorkHelper.getInstance().getMobileApi().getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, options, fields))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GetCarOBDInfoSubscriber());
    }

    /**
     * 获取车辆OBD信息的回调
     */
    class GetCarOBDInfoSubscriber extends Subscriber<DeviceInfo> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            animator.cancel();
            imageScanner.setVisibility(View.GONE);
            MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
        }

        @Override
        public void onNext(final DeviceInfo deviceInfo) {
            animator.cancel();
            imageScanner.setVisibility(View.GONE);
            if (deviceInfo != null) {
                ringActive();
                //打分，待添加
                checkException(deviceInfo);
                setCarHealthyScore(healthyValue);
                Observable.timer(1500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        bindDataToUI(deviceInfo);
                    }
                });

            } else {
                MyUtil.showToast(getApplicationContext(), "获取OBD数据失败！");
            }
        }
    }

    private void ringActive() {
        ObjectAnimator.ofFloat(circleRight2, "rotation", 0.0f, -360.0f).setDuration(1500).start();
        ObjectAnimator.ofFloat(circleRight1, "rotation", 0.0f, 360.0f).setDuration(1500).start();
        ObjectAnimator.ofFloat(circleLeft1, "rotation", 0.0f, 360.0f).setDuration(1500).start();
        ObjectAnimator.ofFloat(circleLeft2, "rotation", 0.0f, -360.0f).setDuration(1500).start();
    }

    /**
     * 设置健康分数
     *
     * @param score
     */
    private void setCarHealthyScore(int score) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(100, score);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                carHealthyScore.setText(animation.getAnimatedValue() + "");

            }
        });
        valueAnimator.start();
    }

    private void checkException(DeviceInfo deviceInfo) {
        DeviceInfo.ActiveObdDataBean obdData = deviceInfo.getActive_obd_data();
        float dpdy = Float.valueOf(obdData.getDpdy());
        if (dpdy < 11 || dpdy > 15) {
            dpdyException = true;
            healthyValue = healthyValue - 3;
        }
        float jqmkd = Float.valueOf(obdData.getJqmkd());
        if (jqmkd < 12 || jqmkd > 17) {
            jqmkdException = true;
            healthyValue = healthyValue - 4;
        }
        float fdjzs = Float.valueOf(obdData.getFdjzs());
        if (fdjzs < 600 || fdjzs > 1000) {
            fdjzsException = true;
            healthyValue = healthyValue - 5;
        }
        float sw = Float.valueOf(obdData.getSw());
        if (sw < 0 || sw > 112) {
            swException = true;
            healthyValue = healthyValue - 5;
        }
        float sychj = Float.valueOf(obdData.getChqwd());
        if (sychj < 300 || sychj > 800) {
            sychjException = true;
            healthyValue = healthyValue - 3;
        }
        float dhtqj = Float.valueOf(obdData.getDhtqj());
        if (dhtqj < 50 || dhtqj > 60) {
            dhtqjException = true;
            healthyValue = healthyValue - 4;
        }
    }

    private void bindDataToUI(DeviceInfo deviceInfo) {
        DeviceInfo.ActiveObdDataBean obdData = deviceInfo.getActive_obd_data();
        dyVal.setText(obdData.getDpdy() + "V");
        if (dpdyException) {
            dyState.setBackgroundResource(R.drawable.obd_state_exception);
            dyStateDescription.setText(getString(R.string.obd_describe_exception));
        }
        jqmkdVal.setText(obdData.getJqmkd() + "%");
        if (jqmkdException) {
            jqmkdState.setBackgroundResource(R.drawable.obd_state_exception);
            jqmkdStateDescription.setText(getString(R.string.obd_describe_exception));
        }
        fdjzsVal.setText(obdData.getFdjzs() + "RPM");
        if (fdjzsException) {
            fdjzsState.setBackgroundResource(R.drawable.obd_state_exception);
            fdjzsStateDescription.setText(getString(R.string.obd_describe_exception));
        }
        swVal.setText(obdData.getSw() + "°C");
        if (swException) {
            swState.setBackgroundResource(R.drawable.obd_state_exception);
            swStateDescription.setText(getString(R.string.obd_describe_exception));
        }
        sychjVal.setText(obdData.getChqwd() + "°C");
        if (sychjException) {
            sychjState.setBackgroundResource(R.drawable.obd_state_exception);
            sychjStateDescription.setText(getString(R.string.obd_describe_exception));
        }
        dhtqjVal.setText(obdData.getDhtqj() + "°");
        if (dhtqjException) {
            dhtqjState.setBackgroundResource(R.drawable.obd_state_exception);
            dhtqjStateDescription.setText(getString(R.string.obd_describe_exception));
        }
        fdjfhVal.setText(obdData.getFdjfz() + "%");
        syylVal.setText(obdData.getSyyl() + "L");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        if (imageScanner != null && imageScanner.getVisibility() == View.VISIBLE) {
            imageScanner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
