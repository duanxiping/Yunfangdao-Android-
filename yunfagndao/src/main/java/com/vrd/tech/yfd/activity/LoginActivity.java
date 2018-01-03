package com.vrd.tech.yfd.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.victor.loading.rotate.RotateLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.LoginInfo;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.DownloadIntentService;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_edt_username)
    EditText loginEdtUsername;
    @Bind(R.id.login_edt_password)
    EditText loginEdtPassword;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.login_tv_forget)
    TextView loginTvForget;
    @Bind(R.id.login_tv_register)
    TextView loginTvRegister;
    long quickClickProof = 0;//防止连续点击
    long quickClickProofRegister = 0;
    long quickClickProofForget = 0;
    boolean isLogin = false;
    @Bind(R.id.rotateloading)
    RotateLoading rotateloading;
    @Bind(R.id.username_container)
    TextInputLayout usernameContainer;
    @Bind(R.id.password_container)
    TextInputLayout passwordContainer;
    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription(); //解除订阅，防止内存泄露
    Animation shake;
    MobileApi mobileApi;
    private Call updateCheckCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mobileApi = NetWorkHelper.getInstance().getMobileApi();
        checkUpdate();
        String savedInfo = getSharedPreferences("vrd_yfd", MODE_PRIVATE).getString("username", "");
        if (!TextUtils.isEmpty(savedInfo)) {
            loginEdtUsername.setText(savedInfo);
        }
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        rotateloading.bringToFront();
    }

    @OnClick({R.id.login_btn, R.id.login_tv_forget, R.id.login_tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                if (System.currentTimeMillis() - quickClickProof > 500) {
                    final HashMap<String, String> options = new HashMap<String, String>();
                    if (TextUtils.isEmpty(loginEdtUsername.getText().toString())) {
                        loginEdtUsername.setError(getString(R.string.cant_be_null));
                        usernameContainer.startAnimation(shake);
                    } else if (TextUtils.isEmpty(loginEdtPassword.getText().toString())) {
                        loginEdtPassword.setError(getString(R.string.cant_be_null));
                        passwordContainer.startAnimation(shake);
                    } else {
                        options.put("account", loginEdtUsername.getText().toString());
                        options.put("password", MyUtil.getM5DEndo(loginEdtPassword.getText().toString()));
                        isLogin = true;
                        rotateloading.start();
                        Subscription sub = mobileApi.getLoginInfo(APIUtil.createOptions(MobileApi.Method_User_Login, options)).map(new Func1<LoginInfo, String>() {
                            @Override
                            public String call(LoginInfo loginInfo) {
                                if (loginInfo.getStatus_code() == 0) {
                                    //存储相关信息,工作在io线程
                                    setJPushTag(loginInfo);
                                    saveLoginInfo(loginInfo);
                                    saveCurrentUserInputInfo(options.get("account"), options.get("password"));
                                    return "success";
                                } else {
                                    throw new RuntimeException("login_fail");
                                }

                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new LoginInfoSubscriber(getApplicationContext()));
                        mCompositeSubscription.add(sub);
                    }
                }
                quickClickProof = System.currentTimeMillis();
                break;
            case R.id.login_tv_forget:
                if (isLogin)
                    return;
                if (System.currentTimeMillis() - quickClickProofForget > 500) {
                    startActivity(new Intent(LoginActivity.this, ForgetAndRegisterActivity.class).putExtra("intent", 0));
                }
                quickClickProofForget = System.currentTimeMillis();
                break;
            case R.id.login_tv_register:
                if (isLogin)
                    return;
                if (System.currentTimeMillis() - quickClickProofRegister > 500) {
                    startActivity(new Intent(LoginActivity.this, ForgetAndRegisterActivity.class).putExtra("intent", 1));
                }
                quickClickProofRegister = System.currentTimeMillis();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        mobileApi = null;
        if (updateCheckCall != null && !updateCheckCall.isCanceled()) {
            updateCheckCall.cancel();
            updateCheckCall = null;
        }
    }

    class LoginInfoSubscriber extends BaseSubscriber<String> {
        public LoginInfoSubscriber(Context context) {
            super(context);
        }

        @Override
        protected void onSubEnd() {
            isLogin = false;
            rotateloading.stop();
        }

        @Override
        protected void onSubError(Throwable e) {
            if (e.getMessage().equals("login_fail")) {
                MyUtil.showToast(getApplicationContext(), getString(R.string.login_fail));
                loginEdtPassword.setText("");
            }
        }

        @Override
        public void onNext(String s) {
            if (!TextUtils.isEmpty(s) && "success".equals(s)) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    /**
     * 保存登陆返回信息
     *
     * @param loginInfo
     */
    private void saveLoginInfo(LoginInfo loginInfo) {
        File dir = getFilesDir();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File(dir.getPath() + "loginInfo")));
            oos.writeObject(loginInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            MyUtil.IOClose(oos);
        }
    }

    /**
     * 保存用户名和密码
     *
     * @param username
     * @param password
     */
    private void saveCurrentUserInputInfo(String username, String password) {
        SharedPreferences sp = getSharedPreferences("vrd_yfd", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }


    private void setJPushTag(LoginInfo loginInfo) {
        Set<String> tags = new HashSet<String>();
        tags.add(loginInfo.getCust_id());
        JPushInterface.setTags(getApplicationContext(), tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                //Log.i("JPush", "status_code = " + i);
            }
        });
    }

    private void checkUpdate() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(getString(R.string.update_url)).build();
        updateCheckCall = client.newCall(request);
        updateCheckCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject json = new JSONObject(response.body().string());
                    final double latestVersion = json.getDouble("version");
                    double nativeVersion = Double.valueOf(MyUtil.getAppVersion(getApplicationContext()));
                    if (latestVersion > nativeVersion) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    showUpdateDialog(json.getString("path"), latestVersion + "");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void showUpdateDialog(final String url, final String version) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_tips));
        builder.setMessage(getString(R.string.update_content));
        builder.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //准备更新
                DownloadIntentService.startActionDownload(getApplicationContext(), url, version);
            }
        });
        builder.setNegativeButton(R.string.btn_no, null);
        builder.show();
    }
}
