package com.vrd.tech.yfd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.vrd.tech.yfd.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoreActivity extends AppCompatActivity {

    @Bind(R.id.toAlterUserInfo)
    RelativeLayout toAlterUserInfo;
    @Bind(R.id.toSetCarModel)
    RelativeLayout toSetCarModel;
    @Bind(R.id.toCarBinding)
    RelativeLayout toCarBinding;
    private long quickClickProof = 0;
    String deviceId;
    String token;
    private boolean isBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        deviceId = intent.getStringExtra("device_id");
        token = intent.getStringExtra("access_token");
    }

    @OnClick({R.id.toAlterUserInfo, R.id.toSetCarModel, R.id.toCarBinding})
    public void onClick(View view) {
        if (System.currentTimeMillis() - quickClickProof > 500) {
            switch (view.getId()) {
                case R.id.toAlterUserInfo:
                    startActivity(new Intent(MoreActivity.this, UserInfoActivity.class));
                    break;
                case R.id.toSetCarModel:
                    Intent intent = new Intent(MoreActivity.this, CarModelSetActivity.class);
                    intent.putExtra("device_id", deviceId);
                    intent.putExtra("access_token", token);
                    startActivity(intent);
                    break;

                case R.id.toCarBinding:
                    isBinding = true;
                    Intent intent1 = new Intent(this, ForgetAndRegisterActivity.class);
                    intent1.putExtra("isBinding", isBinding);
                    startActivity(intent1);

                    break;
            }
        }
        quickClickProof = System.currentTimeMillis();
    }
}
