package com.vrd.tech.yfd.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.victor.loading.book.BookLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResetPwdActivity extends AppCompatActivity {

    @Bind(R.id.tvSubmit)
    TextView tvSubmit;
    @Bind(R.id.currentPassword)
    EditText currentPassword;
    @Bind(R.id.newPassword)
    EditText newPassword;
    @Bind(R.id.newPasswordAgain)
    EditText newPasswordAgain;
    @Bind(R.id.currentPasswordContainer)
    TextInputLayout currentPasswordContainer;
    @Bind(R.id.newPasswordContainer)
    TextInputLayout newPasswordContainer;
    @Bind(R.id.newPasswordAgainContainer)
    TextInputLayout newPasswordAgainContainer;
    private String password;
    private String username;
    AlertDialog dialog;
    boolean isSendingRequest = false;
    Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.bind(this);
        SharedPreferences sp = getSharedPreferences("vrd_yfd", MODE_PRIVATE);
        password = sp.getString("password", "");
        username = sp.getString("username", "");
        shake = AnimationUtils.loadAnimation(this, R.anim.shake);
    }


    private void showWaitingDialog() {
        dialog = new AlertDialog.Builder(ResetPwdActivity.this).create();
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
}
