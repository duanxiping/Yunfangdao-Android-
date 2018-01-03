package com.vrd.tech.yfd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.victor.loading.book.BookLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.DeviceInfo;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.entity.WiVehicle;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;
import com.vrd.tech.yfd.view.CustomDatePick;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.aigestudio.datepicker.bizs.themes.DPTManager;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CarSetActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {


    @Bind(R.id.objName)
    EditText objName;
    @Bind(R.id.simNumber)
    TextView simNumber;
    @Bind(R.id.deviceSerial)
    TextView deviceSerial;
    @Bind(R.id.autoPadlock)
    ToggleButton autoPadlock;
    @Bind(R.id.silentMode)
    ToggleButton silentMode;
    @Bind(R.id.currentMileage)
    EditText currentMileage;
    @Bind(R.id.btnAlterMileage)
    Button btnAlterMileage;
    @Bind(R.id.time1)
    RadioButton time1;
    @Bind(R.id.time2)
    RadioButton time2;
    @Bind(R.id.time3)
    RadioButton time3;
    @Bind(R.id.timeChoose)
    RadioGroup timeChoose;
    AlertDialog dialog;
    @Bind(R.id.tvSubmit)
    TextView tvSubmit;
    @Bind(R.id.deleteVehicheBtn)
    TextView btnDeleteVehiche;
    private String objId;
    private String token;
    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();

    private boolean isSendingCmd = false;
    private int interval;//预热时间
    private int tag = 0; //0年检 1保险
    private MobileApi mobileApi;
    private WiVehicle mWiVehicle;
    final String vehicleFields = "cust_id,obj_id,obj_name,device_id,insurance_date,annual_inspect_date,maintain_next_mileage,create_time";
    final String deviceFields = "params,sim,serial,active_gps_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_set);
        ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.clear();
        mCompositeSubscription = null;
    }

    private void init() {
        initData();
    }

    private void beginListenerCheckedChange() {
        timeChoose.setOnCheckedChangeListener(this);
        autoPadlock.setOnCheckedChangeListener(this);
        silentMode.setOnCheckedChangeListener(this);
    }

    private void initData() {
        objId = getIntent().getStringExtra("objId");
        token = getIntent().getStringExtra("access_token");
        mobileApi = NetWorkHelper.getInstance().getMobileApi();
        if (!TextUtils.isEmpty(objId)) {
            showWaitingDialog();
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("obj_id", objId);
            options.put("access_token", token);
            Subscription sub = mobileApi.getVehicleInfo(APIUtil.createOptions(MobileApi.Method_Vehicle_Get, options, vehicleFields))
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<WiVehicle, Observable<DeviceInfo>>() {
                        @Override
                        public Observable<DeviceInfo> call(WiVehicle wiVehicle) {
                            if (wiVehicle == null) {
                                return null;
                            } else {
                                mWiVehicle = wiVehicle;
                                HashMap<String, String> options = new HashMap<String, String>();
                                options.put("device_id", mWiVehicle.getDevice_id());
                                options.put("access_token", token);
                                return mobileApi.getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, options, deviceFields));
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DeviceInfo>() {
                        @Override
                        public void onCompleted() {
                            dismissWaitingDialog();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dismissWaitingDialog();
                            MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                            Log.i("zeng", "error = " + e.getMessage());
                        }

                        @Override
                        public void onNext(DeviceInfo deviceInfo) {
                            if (deviceInfo != null) {
                                bindDataToUI(mWiVehicle, deviceInfo);
                            } else {
                                MyUtil.showToast(getApplicationContext(), getString(R.string.get_car_data_failed));
                            }
                        }
                    });
            mCompositeSubscription.add(sub);
        }
    }

    private void bindDataToUI(WiVehicle wiVehicle, DeviceInfo deviceInfo) {
        //车辆信息
        objName.setText(wiVehicle.getObj_name());
        Log.e("tag","车辆信息是================"+wiVehicle.getObj_name());
        //终端信息
        simNumber.setText(deviceInfo.getSim());
        Log.e("tag","终端SIM卡号是================"+deviceInfo.getSim());
        deviceSerial.setText(deviceInfo.getSerial());
        Log.e("tag","终端条码是================"+wiVehicle.getCust_id());
        //行车自动落锁
        if (deviceInfo.getParams().isIs_autolock()) {
            autoPadlock.setChecked(true);
        }
        //静音模式
        if (!deviceInfo.getParams().isIs_sound()) {
            silentMode.setChecked(true);
        }
        //里程
        currentMileage.setText(deviceInfo.getActive_gps_data().getMileage());
        //预热时间
        interval = deviceInfo.getParams().getAccoff_interval();
        if (interval == 10) {
            time1.setChecked(true);
        } else if (interval == 20) {
            time2.setChecked(true);
        } else if (interval == 30) {
            time3.setChecked(true);
        }
        beginListenerCheckedChange();
    }

    @OnClick({R.id.btnAlterMileage, R.id.tvSubmit, R.id.deleteVehicheBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAlterMileage:
                if (!isSendingCmd) {
                    String mileage = currentMileage.getText().toString().trim();
                    if (!TextUtils.isEmpty(mileage)) {
                        sendCommand(MobileApi.CMD_SET_MILEAGE, "{mileage:" + mileage + "}");
                    }
                }
                break;
            case R.id.tvSubmit:
                String carName = objName.getText().toString().trim();
                if (!TextUtils.isEmpty(carName) && !carName.equals(mWiVehicle.getObj_name())) {
                    mWiVehicle.setObj_name(carName);
                }
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("_obj_id", mWiVehicle.getObj_id());
                options.put("obj_name", mWiVehicle.getObj_name());
                options.put("access_token", token);
                saveCarInfo(options);
                break;

            case R.id.deleteVehicheBtn:

//                HashMap<String, String> optionss = new HashMap<String, String>();
//                optionss.put("_obj_id", mWiVehicle.getObj_id());
//                optionss.put("obj_name", mWiVehicle.getObj_name());
//                optionss.put("access_token", token);
//                deleteCarInfo(optionss);
                break;
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.autoPadlock:
                if (!isSendingCmd) {
                    if (isChecked) {
                        sendCommand(MobileApi.COMMAND_AUTOLOCKON, "{}");
                    } else {
                        sendCommand(MobileApi.COMMAND_AUTOLOCKOFF, "{}");
                    }
                }
                break;
            case R.id.silentMode:
                if (!isSendingCmd) {
                    if (isChecked) {
                        sendCommand(MobileApi.CMD_SILENT, "{}");
                    } else {
                        sendCommand(MobileApi.CMD_SOUND, "{}");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (isSendingCmd) {
            return;
        }
        if (checkedId == R.id.time1) {
            sendCommand(MobileApi.COMMAND_ACCOFF_INTERVAL_10, "{}");
        }
        if (checkedId == R.id.time2) {
            sendCommand(MobileApi.COMMAND_ACCOFF_INTERVAL_20, "{}");
        }
        if (checkedId == R.id.time3) {
            sendCommand(MobileApi.COMMAND_ACCOFF_INTERVAL_30, "{}");
        }
    }

    private void showWaitingDialog() {
        dialog = new AlertDialog.Builder(CarSetActivity.this).create();
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

    private void sendCommand(final String cmd, String params) {
        isSendingCmd = true;
        showWaitingDialog();
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("device_id", mWiVehicle.getDevice_id());
        options.put("cmd_type", cmd);
        options.put("access_token", token);
        options.put("params", params);
        mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Command, options))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusCode>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                        executeCmdFailed(cmd);
                        isSendingCmd = false;
                        dismissWaitingDialog();
                    }

                    @Override
                    public void onNext(StatusCode statusCode) {
                        dismissWaitingDialog();
                        if (statusCode.getStatus_code() == 0) {
                            executeCmdSuccess(cmd);
                            isSendingCmd = false;
                        } else {
                            executeCmdFailed(cmd);
                            isSendingCmd = false;
                        }
                    }
                });
    }

    private void executeCmdSuccess(String cmd) {
        if (cmd.equals(MobileApi.CMD_SILENT)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.set_silent_mode_success));
        } else if (cmd.equals(MobileApi.CMD_SOUND)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.close_silent_mode_success));
        } else if (cmd.equals(MobileApi.CMD_SET_MILEAGE)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.set_mileage_success));
        } else if (cmd.equals(MobileApi.COMMAND_AUTOLOCKON)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.open_auto_padlock_success));
        } else if (cmd.equals(MobileApi.COMMAND_AUTOLOCKOFF)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.close_auto_padlock_success));
        } else {
            MyUtil.showToast(getApplicationContext(), getString(R.string.set_heating_time_success));
            if (time1.isChecked()) {
                interval = 10;
            }
            if (time2.isChecked()) {
                interval = 20;
            }
            if (time3.isChecked()) {
                interval = 30;
            }
        }
    }

    private void executeCmdFailed(String cmd) {
        if (cmd.equals(MobileApi.CMD_SILENT)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.set_silent_mode_failed));
            silentMode.setChecked(false);
        } else if (cmd.equals(MobileApi.CMD_SOUND)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.close_silent_mode_failed));
            silentMode.setChecked(true);
        } else if (cmd.equals(MobileApi.CMD_SET_MILEAGE)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.set_mileage_failed));
        } else if (cmd.equals(MobileApi.COMMAND_AUTOLOCKON)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.open_auto_padlock_failed));
            autoPadlock.setChecked(false);
        } else if (cmd.equals(MobileApi.COMMAND_AUTOLOCKOFF)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.close_auto_padlock_failed));
            autoPadlock.setChecked(true);
        } else {
            MyUtil.showToast(getApplicationContext(), getString(R.string.set_heating_time_failed));
            if (interval == 10) {
                time1.setChecked(true);
            }
            if (interval == 20) {
                time2.setChecked(true);
            }
            if (interval == 30) {
                time3.setChecked(true);
            }
        }
    }

    private void saveCarInfo(HashMap<String, String> options) {
        showWaitingDialog();
        Subscription sub = mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Vehicle_Update, options))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusCode>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.alter_carinfo_failed));
                        dismissWaitingDialog();
                    }

                    @Override
                    public void onNext(StatusCode statusCode) {
                        Log.e("tag", "返回状态码     "+statusCode.getStatus_code());
                        dismissWaitingDialog();
                        if (statusCode != null && statusCode.getStatus_code() == 0) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.alter_carinfo_success));
                            Intent result = new Intent();
                            result.putExtra("obj_name", mWiVehicle.getObj_name());
                            setResult(101, result);
                        } else {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.alter_carinfo_failed));
                        }
                    }
                });
        mCompositeSubscription.add(sub);
    }

    private void deleteCarInfo(HashMap<String, String> options) {
        showWaitingDialog();
        Subscription sub = mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Vehicle_Delete, options))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusCode>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.alter_deletecarinfo_failed));
                        dismissWaitingDialog();
                    }

                    @Override
                    public void onNext(StatusCode statusCode) {
                        dismissWaitingDialog();
                        Log.e("tag", "返回状态码 ====    "+statusCode.getStatus_code());
                        if (statusCode != null && statusCode.getStatus_code() == 0) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.alter_deletecarinfo_success));
                            Intent result = new Intent();
                            result.putExtra("obj_name", "");
                            setResult(101, result);
                        } else {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.alter_deletecarinfo_failed));
                        }
                    }
                });
        mCompositeSubscription.remove(sub);
    }

//    private void setDateToUI(String date) {
//        if (tag == 0) {
//            annualSurveyDate.setText(date);
//        }
//        if (tag == 1) {
//            insuranceDate.setText(date);
//        }
//    }
}
