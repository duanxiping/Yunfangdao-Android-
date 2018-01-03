package com.vrd.tech.yfd.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.victor.loading.book.BookLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;
import com.vrd.tech.yfd.view.DividerItemDecoration;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CarModelSetActivity extends AppCompatActivity {

    private int[] logoResources;
    private String[] carBrands;
    @Bind(R.id.carLogoList)
    RecyclerView carLogoList;
    Subscription sub;
    String accessToken;
    String deviceId;
    AlertDialog dialog;
    String choseBrand;
    String choseSeries;
    final String defaultSerial = "4C4310020000EB8E0D0A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_model_set);
        ButterKnife.bind(this);
        logoResources = new int[]{R.drawable.audi, R.drawable.benz, R.drawable.bmw, R.drawable.buick, R.drawable.chevrolet,
                R.drawable.chrysler, R.drawable.citroen, R.drawable.dazhong, R.drawable.ford, R.drawable.haima,
                R.drawable.honda, R.drawable.hyundai, R.drawable.jeep, R.drawable.kia, R.drawable.lexus,
                R.drawable.mazda, R.drawable.nissan, R.drawable.peugeot, R.drawable.roewe, R.drawable.skoda, R.drawable.toyota};
        carBrands = getResources().getStringArray(R.array.car_brands);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        carLogoList.setLayoutManager(layoutManager);
        carLogoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        carLogoList.setAdapter(new CarLogoAdapter());
        Intent intent = getIntent();
        accessToken = intent.getStringExtra("access_token");
        deviceId = intent.getStringExtra("device_id");
    }


    class CarLogoAdapter extends RecyclerView.Adapter<CarLogoAdapter.LogoItemHolder> {

        @Override
        public LogoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(CarModelSetActivity.this).inflate(R.layout.item_car_brand, parent, false);
            LogoItemHolder holder = new LogoItemHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(LogoItemHolder holder, final int position) {
            holder.logoIcon.setBackgroundResource(logoResources[position]);
            holder.logoTxt.setText(carBrands[position]);
            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCarSeries(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return logoResources.length;
        }

        class LogoItemHolder extends RecyclerView.ViewHolder {
            ImageView logoIcon;
            TextView logoTxt;
            View container;

            public LogoItemHolder(View itemView) {
                super(itemView);
                logoIcon = (ImageView) itemView.findViewById(R.id.car_logo);
                logoTxt = (TextView) itemView.findViewById(R.id.car_logo_txt);
                container = itemView.findViewById(R.id.container);
            }
        }
    }

    private void showCarSeries(final int position) {
        final int arrayResourceId = getArrayResourceIdByPosition(position);
        if (0 == arrayResourceId)
            return;
        new AlertDialog.Builder(CarModelSetActivity.this)
                .setTitle(getString(R.string.title_choose_model))
                .setItems(arrayResourceId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //点击发送指令
                        String command = getCommanString(position, arrayResourceId, which);
                        sendCommand(command);
//                        new AlertDialog.Builder(CarModelSetActivity.this)
//                                .setMessage("You selected: " + chooosedBrand + "---" + choosedSeries)
//                                .show();
                    }
                }).create().show();
    }

    private int getArrayResourceIdByPosition(int position) {
        choseBrand = carBrands[position];
        switch (position) {
            case 0:
                return R.array.audi_series;
            case 1:
                return R.array.benz_series;
            case 2:
                return R.array.bmw_series;
            case 3:
                return R.array.buick_series;
            case 4:
                return R.array.chevrolet_series;
            case 5:
                return R.array.chrysler_series;
            case 6:
                return R.array.citroen_series;
            case 7:
                return R.array.dazhong_series;
            case 8:
                return R.array.ford_series;
            case 9:
                return R.array.haima_series;
            case 10:
                return R.array.honda_series;
            case 11:
                return R.array.hyundai_series;
            case 12:
                return R.array.jeep_series;
            case 13:
                return R.array.kia_series;
            case 14:
                return R.array.lexus_series;
            case 15:
                return R.array.mazda_series;
            case 16:
                return R.array.nissan_series;
            case 17:
                return R.array.peugeot_series;
            case 18:
                return R.array.roewe_series;
            case 19:
                return R.array.skoda_series;
            case 20:
                return R.array.toyota_series;
            default:
                return 0;
        }
    }

    private String getCommanString(int position, int arrayId, int which) {
        String[] series = getResources().getStringArray(arrayId);
        choseSeries = series[which];
        switch (position) {
            case 0:
                //奥迪
                if (choseSeries.contains("A3") || choseSeries.contains("A4") || choseSeries.contains("A5") || choseSeries.contains("Q5")) {
                    return "4C4310020101C89E0D0A";
                } else if (choseSeries.contains("Q7")) {
                    return "4C4310020103E8DC0D0A";
                } else if (choseSeries.contains("A6")) {
                    return "4C4310020104983B0D0A";
                } else {
                    return "4C4310020100D8BF0D0A";
                }
            case 1:
                //奔驰
                return "4C4310020A0004450D0A";
            case 2:
                //宝马
                return "4C431002090051160D0A";
            case 3:
                //别克
                return "4C43100202008DEC0D0A";
            case 4:
                //雪佛兰
                return "4C43100202008DEC0D0A";
            case 5:
                //克莱斯勒
                return "4C43100212008E9F0D0A";
            case 6:
                //雪铁龙
                return "4C4310020000EB8E0D0A";
            case 7:
                //大众
                if (choseSeries.contains("朗逸") || choseSeries.contains("宝来")) {
                    return "4C4310020102F8FD0D0A";
                } else {
                    return "4C4310020100D8BF0D0A";
                }
            case 8:
                //福特
                if (choseSeries.contains("老蒙迪欧")) {
                    return "4C43100203029E9F0D0A";
                } else if (choseSeries.equals("蒙迪欧")) {
                    return "4C4310020304FE590D0A";
                } else if (choseSeries.equals("翼虎") || choseSeries.equals("新福克斯")) {
                    return "4C4310020300BEDD0D0A";
                } else if (choseSeries.equals("经典福克斯")) {
                    return "4C4310020301AEFC0D0A";
                } else if (choseSeries.equals("翼博") || choseSeries.equals("嘉年华")) {
                    return "4C43100203038EBE0D0A";
                } else if (choseSeries.equals("锐界")) {
                    return "4C4310020305EE780D0A";
                }
            case 9:
                //海马
                return defaultSerial;
            case 10:
                //本田
                if (choseSeries.contains("飞度")) {
                    return "4C4310020501045A0D0A";
                } else {
                    return "4C4310020500147B0D0A";
                }
            case 11:
                //现代
                if (choseSeries.contains("IX35")) {
                    return "4C431002080172060D0A";
                } else {
                    return "4C431002080062270D0A";
                }
            case 12:
                //吉普
                return defaultSerial;
            case 13:
                //起亚
                return "4C431002070072190D0A";
            case 14:
                //雷克萨斯
                return "4C4310020400274A0D0A";
            case 15:
                //马自达
                if (choseSeries.contains("马自达6")) {
                    return "4C431002140024390D0A";
                } else {
                    return defaultSerial;
                }
            case 16:
                //日产
                if (choseSeries.contains("轩逸") || choseSeries.contains("骊威")) {
                    return "4C431002060151090D0A";
                } else {
                    return "4C431002060041280D0A";
                }
            case 17:
                //标致
                return "4C4310020E00C8810D0A";
            case 18:
                //荣威
                return defaultSerial;
            case 19:
                //斯柯达
                return defaultSerial;
            case 20:
                //丰田
                return "4C4310020400274A0D0A";
            default:
                return defaultSerial;
        }
    }


    private void sendCommand(String command) {
        showWaitingDialog();
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("device_id", deviceId);
        options.put("params", "{\"cmd\":\"" + command + "\"}");
        options.put("cmd_type", MobileApi.CMD_TRANSFER);
        options.put("access_token", accessToken);
        sub = NetWorkHelper.getInstance().getMobileApi().getStatusCode(APIUtil.createOptions(MobileApi.Method_Command, options)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<StatusCode>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingDialog();
                MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
            }

            @Override
            public void onNext(StatusCode statusCode) {
                dismissWaitingDialog();
                if (statusCode != null) {
                    if (statusCode.getStatus_code() == 0) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.set_car_series_success));
                    } else {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.set_car_series_fail));
                    }
                } else {
                    throw new RuntimeException("statusCode == null");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sub != null) {
            sub.unsubscribe();
        }
    }

    private void showWaitingDialog() {
        dialog = new AlertDialog.Builder(CarModelSetActivity.this).create();
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
