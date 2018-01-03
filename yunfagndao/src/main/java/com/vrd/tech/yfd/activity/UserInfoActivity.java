package com.vrd.tech.yfd.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.databinding.ActivityUserInfoBinding;
import com.vrd.tech.yfd.event.UserInfoEvent;


public class UserInfoActivity extends AppCompatActivity {
    private ActivityUserInfoBinding binding;
    private UserInfoEvent userInfoEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        userInfoEvent = new UserInfoEvent(this, binding);
        binding.setEventAction(userInfoEvent);
        userInfoEvent.getUserInfoFromInternet();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userInfoEvent.clearSubscription();
    }
}
