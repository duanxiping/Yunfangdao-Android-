package com.vrd.tech.yfd.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.victor.loading.book.BookLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.AccessToken;
import com.vrd.tech.yfd.entity.CreateVehicleInfo;
import com.vrd.tech.yfd.entity.DeviceInfo;
import com.vrd.tech.yfd.entity.LoginInfo;
import com.vrd.tech.yfd.entity.RegisterInfo;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.entity.UsertExist;
import com.vrd.tech.yfd.entity.ValidCheck;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ForgetAndRegisterActivity extends AppCompatActivity {

    @Bind(R.id.titleRegOrFor)
    TextView titleRegOrFor;
    @Bind(R.id.forget_step1)
    TextView forgetStep1;
    @Bind(R.id.forget_step2)
    TextView forgetStep2;
    @Bind(R.id.forget_step3)
    TextView forgetStep3;
    @Bind(R.id.forget_step4)
    TextView forgetStep4;
    @Bind(R.id.forget_current_state)
    LinearLayout forgetCurrentState;
    @Bind(R.id.forget_first_input)
    EditText forgetFirstInput;
    @Bind(R.id.first_input_layout)
    TextInputLayout firstInputLayout;
    @Bind(R.id.tv_verify_code)
    TextView tvVerifyCode;
    @Bind(R.id.image_qrcode)
    ImageView imageQrcode;
    @Bind(R.id.forget_pass_again)
    EditText forgetPassAgain;
    @Bind(R.id.container_second_input)
    TextInputLayout containerSecondInput;
    @Bind(R.id.forget_btn)
    Button forgetBtn;
    @Bind(R.id.binding_btn)
    Button bindingBtn;
    @Bind(R.id.tv_forget_finish)
    TextView tvForgetFinish;
    private int currentStep = 1;//步骤标识
    private int intentTag;//操作标识，0代表找回密码，1代表注册
    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription(); //解除订阅，防止内存泄露
    int colorGray = 0xff8d8d8d;
    int colorWhite = 0xffffffff;
    MobileApi mobileApi;
    String phoneNum;
    int timeTag = 60;
    Timer timer;
    long quickClickProof = 0;
    AlertDialog dialog;

    private String password;
    private String cust_id;
    private String obj_id;
    private String mAccessToken;
    private String deviceId;
    private String validCode;
    private boolean needToRegister = false;
    private boolean isBinding;
    private String custID;
    private String tocken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_and_register);
        ButterKnife.bind(this);
        intentTag = getIntent().getIntExtra("intent", 0);
        if (1 == intentTag) {
            titleRegOrFor.setText(getString(R.string.title_register));
            forgetStep3.setText(getString(R.string.register_step3));
        }
        mobileApi = NetWorkHelper.getInstance().getMobileApi();

        toChooseState(forgetStep1);
        carBinding();
    }


    private void carBinding(){

        Intent intent = getIntent();
        isBinding = intent.getBooleanExtra("isBinding",isBinding);
        if (isBinding == true){
            titleRegOrFor.setText(R.string.title_binding);
            //绑定终端
            imageQrcode.setVisibility(View.VISIBLE);
            forgetCurrentState.setVisibility(View.INVISIBLE);
            tvVerifyCode.setVisibility(View.GONE);
            firstInputLayout.setHint(getString(R.string.device_serial));
            containerSecondInput.setVisibility(View.VISIBLE);
            containerSecondInput.setHint(getString(R.string.device_sim));
            forgetBtn.setVisibility(View.GONE);
            bindingBtn.setVisibility(View.VISIBLE);
            bindingBtn.setText(getString(R.string.binding_ensure));
            forgetPassAgain.setHint("");

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.clear();
            mCompositeSubscription = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String serial = data.getStringExtra("serial");
            if (!TextUtils.isEmpty(serial)) {
                forgetFirstInput.setText(serial);
            }
        }
    }

    @OnClick({R.id.tv_verify_code, R.id.image_qrcode, R.id.forget_btn, R.id.binding_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_verify_code:
                if (currentStep == 2) {
                    if (timeTag == 60) {
                        sendValidCodeRequest();
                        tvVerifyCode.setBackgroundResource(R.drawable.forget_textview_background);
                        tvVerifyCode.setTextColor(colorGray);
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvVerifyCode.setText(timeTag + "");
                                        timeTag--;
                                        if (timeTag == 0) {
                                            resetGetValidCodeState();
                                        }
                                    }
                                });
                            }
                        }, 0, 1000);
                    }
                }
                break;
            case R.id.image_qrcode:
                startActivityForResult(new Intent(ForgetAndRegisterActivity.this, ScannerActivity.class), 0);
                break;
            case R.id.forget_btn:
                if (System.currentTimeMillis() - quickClickProof > 500) {
                    switch (currentStep) {
                        case 1:
                            phoneNum = forgetFirstInput.getText().toString();
                            if (MyUtil.isMobileNO(phoneNum)) {
                                checkAccount();
                            } else {
                                forgetFirstInput.setError(getString(R.string.wrong_phone_num));
                            }
                            break;
                        case 2:
                            String inputStr = forgetFirstInput.getText().toString().trim();
                            if (TextUtils.isEmpty(inputStr)) {
                                forgetFirstInput.setError(getString(R.string.cant_be_null));
                            } else {
                                //验证短信验证码
                                showWaitingDialog();
                                checkValidCode(inputStr);
                            }
                            break;
                        case 3:
                            if (intentTag == 0) {
                                //重置密码
                                String newPass = forgetFirstInput.getText().toString().trim();
                                String passAgain = forgetPassAgain.getText().toString().trim();
                                if (TextUtils.isEmpty(newPass)) {
                                    forgetFirstInput.setError(getString(R.string.cant_be_null));
                                } else if (TextUtils.isEmpty(passAgain)) {
                                    forgetPassAgain.setError(getString(R.string.cant_be_null));
                                } else if (newPass.equals(passAgain)) {
                                    showWaitingDialog();
                                    sendResetPwdRequest(newPass);
                                } else {
                                    MyUtil.showToast(getApplicationContext(), getString(R.string.pwd_again_error));
                                    forgetFirstInput.setText("");
                                    forgetPassAgain.setText("");
                                }
                            } else {
                                bindingCar();
                            }
                            break;
                        case 4:
                            finish();
                            break;
                        default:
                            break;
                    }
                }
                quickClickProof = System.currentTimeMillis();
                break;
            case R.id.binding_btn:
                bindingCar();
                break;
        }
    }

    private void bindingCar(){

        SharedPreferences sharedPreferences= getSharedPreferences("custID",
                Context.MODE_PRIVATE);
        custID =sharedPreferences.getString("cust", "");
        tocken =sharedPreferences.getString("atoken", "");
        cust_id = custID;
        mAccessToken = tocken;

        //绑定终端
        if (intentTag == 1 && TextUtils.isEmpty(mAccessToken)){
                MyUtil.showToast(getApplicationContext(), getString(R.string.invalid_account_valid_again));
                return;
        }


        final String serial = forgetFirstInput.getText().toString().trim();
        final String sim = forgetPassAgain.getText().toString().trim();
        if (TextUtils.isEmpty(serial)) {
            forgetFirstInput.setError(getString(R.string.cant_be_null));
        } else if (TextUtils.isEmpty(sim) || !MyUtil.isMobileNO(sim)) {
            forgetPassAgain.setError(getString(R.string.input_effective_msg));
        } else {
            //发起绑定终端
            showWaitingDialog();
            sendBindDeviceRequest(serial, sim);
        }

    }

    private void toDefaultState(TextView tv) {
        tv.setTextColor(colorGray);
        tv.setBackgroundResource(R.drawable.forget_textview_background);
    }

    private void toChooseState(TextView tv) {
        tv.setTextColor(colorWhite);
        tv.setBackgroundResource(R.drawable.forget_textview_background_choose);
    }

    private void sendValidCodeRequest() {
        HashMap<String, String> optioins = new HashMap<String, String>();
        optioins.put("mobile", phoneNum);
        optioins.put("type", "1");
        Subscription s = mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Comm_Sms_Send, optioins)).subscribeOn(Schedulers.io()).subscribe(new Subscriber<StatusCode>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                resetGetValidCodeState();
            }

            @Override
            public void onNext(StatusCode statusCode) {
                if (statusCode.getStatus_code() != 0) {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.get_valid_code_fail));
                    resetGetValidCodeState();
                }
            }
        });
        mCompositeSubscription.add(s);
    }

    private void resetGetValidCodeState() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            tvVerifyCode.setText(getString(R.string.get_valid_code));
            tvVerifyCode.setBackgroundResource(R.color.colorAccent);
            tvVerifyCode.setTextColor(colorWhite);
            timeTag = 60;
        }
    }

    private void sendResetPwdRequest(String password) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("account", phoneNum);
        options.put("password", MyUtil.getM5DEndo(password));
        options.put("valid_type", "2");
        options.put("valid_code", validCode);
        Subscription s = mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_User_Psd_Reset, options)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<StatusCode>() {
            @Override
            public void onCompleted() {
                dismissWaitingDialog();
            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingDialog();
                MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
            }

            @Override
            public void onNext(StatusCode statusCode) {
                if (statusCode.getStatus_code() == 0) {
                    toDefaultState(forgetStep3);
                    toChooseState(forgetStep4);
                    firstInputLayout.setVisibility(View.INVISIBLE);
                    containerSecondInput.setVisibility(View.GONE);
                    forgetBtn.setText(getString(R.string.to_login_page));
                    tvForgetFinish.setVisibility(View.VISIBLE);
                    currentStep++;
                } else {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.reset_pwd_failed));
                }
            }
        });
        mCompositeSubscription.add(s);
    }

    private void sendBindDeviceRequest(final String serial, final String sim) {
        Subscription sub = null;
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("serial", serial);
//        if (intentTag == 1){
            options.put("access_token", mAccessToken);
//        }

        String fields = "cust_id";
        sub = mobileApi.getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, options, fields)).subscribeOn(Schedulers.io()).subscribe(new Subscriber<DeviceInfo>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingDialog();
                // Log.i("zeng", "error = " + e.getMessage());
            }

            @Override
            public void onNext(DeviceInfo deviceInfo) {


                Log.e("Forget","设备Cust_id是========"+deviceInfo.getCust_id());
                Log.e("Forget","设备State是========"+deviceInfo.getStatus_code());
                Log.e("Forget","设备device是========"+deviceInfo.getDevice_id());
                if (deviceInfo.getStatus_code() == 3 || TextUtils.isEmpty(deviceInfo.getDevice_id())) {
                    //终端不存在
                    MyUtil.showToast(getApplicationContext(), getString(R.string.wrong_device_serial));
                } else {
                    if (deviceInfo.getCust_id().equals("0") || TextUtils.isEmpty(deviceInfo.getCust_id())) {
                        //未绑定,执行绑定（步骤 1.绑定设备到该用户下 2.创建车辆 3.绑定设备到该车辆）
                        deviceId = deviceInfo.getDevice_id();
                        createVehicleAndBindDevice(sim);
                    } else {

                        //已绑定设备
                        MyUtil.showToast(getApplicationContext(), getString(R.string.device_already_registered));
                    }
                }
            }
        });
        mCompositeSubscription.add(sub);
    }

    private void createVehicleAndBindDevice(final String sim) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("_device_id", deviceId);
        options.put("sim", sim);
        options.put("cust_id", cust_id);
//        if (intentTag == 1){
            options.put("access_token", mAccessToken);
//        }

        mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Device_Update, options))
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<StatusCode, Observable<CreateVehicleInfo>>() {
                    @Override
                    public Observable<CreateVehicleInfo> call(StatusCode statusCode) {
                        if (statusCode != null && statusCode.getStatus_code() == 0) {
                            HashMap<String, String> options = new HashMap<String, String>();
                            options.put("cust_id", cust_id);
                            options.put("obj_name", sim);
//                            if (intentTag == 1){
                                options.put("access_token", mAccessToken);
//                            }
                            return mobileApi.getCreateVehicleInfo(APIUtil.createOptions(MobileApi.Method_Vehicle_Create, options));
                        }
                        return null;
                    }
                })
                .flatMap(new Func1<CreateVehicleInfo, Observable<StatusCode>>() {
                    @Override
                    public Observable<StatusCode> call(CreateVehicleInfo createVehicleInfo) {
                        if (null != createVehicleInfo && createVehicleInfo.getStatus_code() == 0) {
                            obj_id = createVehicleInfo.getObj_id();
                            HashMap<String, String> options = new HashMap<String, String>();
                            options.put("_obj_id", obj_id);
                            options.put("device_id", deviceId);
//                            if (intentTag == 1){
                                options.put("access_token", mAccessToken);
//                            }
                            return mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Vehicle_Update, options));
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StatusCode>() {
                    @Override
                    public void onCompleted() {
                        dismissWaitingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissWaitingDialog();
                        //绑定失败
                    }

                    @Override
                    public void onNext(StatusCode statusCode) {
                        if (statusCode != null && statusCode.getStatus_code() == 0) {
                            //成功
                            toDefaultState(forgetStep3);
                            toChooseState(forgetStep4);
                            imageQrcode.setVisibility(View.GONE);
                            firstInputLayout.setVisibility(View.INVISIBLE);
                            containerSecondInput.setVisibility(View.GONE);
                            if (needToRegister) {
                                forgetBtn.setText(getString(R.string.to_login_page_new));
                            } else {
                                forgetBtn.setText(getString(R.string.to_login_page));
                            }
                            tvForgetFinish.setVisibility(View.VISIBLE);
                            tvForgetFinish.setText(getString(R.string.register_finish));
                            currentStep++;
                        } else {
                            //绑定失败
                        }
                    }
                });
    }

    private void showWaitingDialog() {
        dialog = new AlertDialog.Builder(ForgetAndRegisterActivity.this).create();
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


    private void checkAccount() {
        showWaitingDialog();
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("mobile", phoneNum);
        mobileApi.getUsertExist(APIUtil.createOptions(MobileApi.Method_User_User_exist, options)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<UsertExist>() {
            @Override
            public void onCompleted() {
                dismissWaitingDialog();
            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingDialog();
            }

            @Override
            public void onNext(UsertExist usertExist) {
                if (usertExist.isExist()) {
                    //已注册
                    if (intentTag == 1) {
                        showPasswordInput();
                    } else {
                        toValidStep();
                    }
                } else {
                    //未注册，需执行注册流程
                    if (intentTag == 0) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.invalid_account));
                    } else {
                        needToRegister = true;
                        toValidStep();
                    }
                }

            }
        });
    }

    private void registerAccount() {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("mobile", phoneNum);
        options.put("password", phoneNum.substring(5));
        options.put("valid_type", "1");
        options.put("valid_code", validCode);
        mobileApi.getRegisterInfo(APIUtil.createOptions(MobileApi.Method_User_Register, options))
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<RegisterInfo, Observable<AccessToken>>() {
                    @Override
                    public Observable<AccessToken> call(RegisterInfo registerInfo) {
                        if (null != registerInfo && registerInfo.getStatus_code() == 0) {
                            cust_id = registerInfo.getCust_id();
                            HashMap<String, String> options = new HashMap<String, String>();
                            options.put("account", phoneNum);
                            options.put("password", APIUtil.MD5(phoneNum.substring(5)));
                            options.put("type", "1");
                            return mobileApi.getAccessToken(APIUtil.createOptions(MobileApi.Method_Access_Token, options));
                        }
                        return null;
                    }
                })
                .subscribe(new Action1<AccessToken>() {
                    @Override
                    public void call(AccessToken accessToken) {
                        if (accessToken != null) {
                            mAccessToken = accessToken.getAccess_token();
                        }
                    }
                });
    }

    private void showPasswordInput() {
        final View textEntryView = LayoutInflater.from(this).inflate(R.layout.password_dialog, null);
        final EditText password_edit = (EditText) textEntryView.findViewById(R.id.password_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.user_check));
        builder.setView(textEntryView);
        builder.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                password = password_edit.getText().toString();
                //验证密码
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("account", phoneNum);
                options.put("password", APIUtil.MD5(password));
                Subscription sub = null;
                sub = mobileApi.getLoginInfo(APIUtil.createOptions(MobileApi.Method_User_Login, options)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<LoginInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.valid_user_fail));
                    }

                    @Override
                    public void onNext(LoginInfo loginInfo) {
                        if (loginInfo.getStatus_code() == 0) {
                            mAccessToken = loginInfo.getAccess_token();
                            cust_id = loginInfo.getCust_id();
                            toValidStep();
                        } else {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.valid_user_fail));
                        }
                    }
                });
                mCompositeSubscription.add(sub);
            }
        });
        builder.show();
    }

    private void checkValidCode(String validCode) {
        this.validCode = validCode;
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("mobile", phoneNum);
        options.put("valid_code", validCode);
        options.put("valid_type", "1");
        Subscription sub = null;
        mobileApi.getValidCheck(APIUtil.createOptions(MobileApi.Method_User_Volid_Code, options)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ValidCheck>() {
            @Override
            public void onCompleted() {
                dismissWaitingDialog();
            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingDialog();
                MyUtil.showToast(getApplicationContext(), getString(R.string.check_valid_fail));
            }

            @Override
            public void onNext(ValidCheck validCheck) {
                if (validCheck.isValid()) {
                    //验证成功
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    tvVerifyCode.setVisibility(View.GONE);
                    forgetFirstInput.setText("");
                    containerSecondInput.setVisibility(View.VISIBLE);
                    if (intentTag == 0) {
                        //修改密码
                        forgetBtn.setText(getString(R.string.ensure_modify_pwd));
                        firstInputLayout.setHint(getString(R.string.new_password));
                        forgetFirstInput.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                        forgetPassAgain.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        if (needToRegister) {
                            registerAccount();
                        }
                        //绑定终端
                        imageQrcode.setVisibility(View.VISIBLE);
                        firstInputLayout.setHint(getString(R.string.device_serial));
                        forgetBtn.setText(getString(R.string.register_ensure));
                        forgetPassAgain.setHint("");
                        containerSecondInput.setHint(getString(R.string.device_sim));
                    }
                    toDefaultState(forgetStep2);
                    toChooseState(forgetStep3);
                    currentStep++;
                } else {
                    //验证失败
                    forgetFirstInput.setError(getString(R.string.check_valid_fail));
                    forgetFirstInput.setText("");
                }
            }
        });
    }

    private void toValidStep() {
        tvVerifyCode.setVisibility(View.VISIBLE);
        forgetBtn.setText(getString(R.string.check_valid_code));
        forgetFirstInput.setText("");
        forgetFirstInput.setHint("");
        firstInputLayout.setHint(getString(R.string.valid_code));
        toDefaultState(forgetStep1);
        toChooseState(forgetStep2);
        currentStep++;
    }
}
