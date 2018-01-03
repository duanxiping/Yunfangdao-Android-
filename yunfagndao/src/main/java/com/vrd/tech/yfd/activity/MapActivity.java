package com.vrd.tech.yfd.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.victor.loading.book.BookLoading;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.DeviceInfo;
import com.vrd.tech.yfd.entity.WiGpsDatas;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;
import com.vrd.tech.yfd.view.CustomDatePick;
import com.zhy.android.percent.support.PercentLinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.aigestudio.datepicker.bizs.themes.DPTManager;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MapActivity extends AppCompatActivity {

    @Bind(R.id.tv_obj_name_map)
    TextView tvObjNameMap;
    AMap amap;
    UiSettings uiSettings;
    String deviceId;
    String token;
    String serial;
    @Bind(R.id.mapView)
    MapView mapView;
    @Bind(R.id.tv_trajectory)
    TextView tvTrajectory;
    @Bind(R.id.imageLocate)
    ImageView imageLocate;
    @Bind(R.id.tv_locate)
    TextView tvLocate;
    @Bind(R.id.imageTrajectory)
    ImageView imageTrajectory;
    @Bind(R.id.imageFindCar)
    ImageView imageFindCar;
    @Bind(R.id.tv_find_car)
    TextView tvFindCar;
    @Bind(R.id.trajectoryStopOrStart)
    ImageView trajectoryStopOrStart;
    @Bind(R.id.trajectoryProgress)
    ProgressBar trajectoryProgress;
    @Bind(R.id.trajectorySpeedUp)
    ImageView trajectorySpeedUp;
    @Bind(R.id.trajectorySpeedNormal)
    ImageView trajectorySpeedNormal;
    @Bind(R.id.trajectorySpeedDown)
    ImageView trajectorySpeedDown;
    @Bind(R.id.container_trajectory_control)
    PercentLinearLayout containerTrajectoryControl;
    private CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();
    private Polyline mVirtureRoad;
    private Marker mMoveMarker;
    // 通过设置间隔时间和距离可以控制速度和图标移动的距离
    private int TIME_INTERVAL = 40; //默认值40
    private final double DISTANCE = 0.0001;//默认值0.0001
    private Thread replayThread;
    private AlertDialog dialog;
    private boolean isWaitingResponse = false;
    private LatLng start, end;
    private Thread moveMarkerThread;
    private CoordinateConverter converter;
    Subscription peroidSubscription, refreshUISubscription; //轮询订阅者
    private boolean isTracing = false;

    private MobileApi mobileApi;
    private final String deviceFields = "active_gps_data";
    private final String gpsDatasFields = "rcv_time,_lat,_lon,direct,lat,lon";
    private HashMap<String, String> periodOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        containerTrajectoryControl.bringToFront();
        containerTrajectoryControl.setVisibility(View.INVISIBLE);
        mobileApi = NetWorkHelper.getInstance().getMobileApi();
        mapView.onCreate(savedInstanceState);
        if (amap == null) {
            amap = mapView.getMap();
            uiSettings = amap.getUiSettings();
            uiSettings.setZoomControlsEnabled(false);
        }
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        String objName = intent.getStringExtra("objName");
        if (TextUtils.isEmpty(objName)) {
            //无车辆信息
        } else {
            tvObjNameMap.setText(objName);
            deviceId = intent.getStringExtra("device_id");
            token = intent.getStringExtra("access_token");
            serial = intent.getStringExtra("serial");
            periodOptions = new HashMap<String, String>();
            periodOptions.put("device_id", deviceId);
            periodOptions.put("access_token", token);
            showWaitingDialog();
            getCarLocationInfo(new Subscriber<DeviceInfo>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    dismissWaitingDialog();
                    MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                }

                @Override
                public void onNext(DeviceInfo deviceInfo) {
                    dismissWaitingDialog();
                    if (deviceInfo != null) {
                        double lon = deviceInfo.getActive_gps_data().getLon();
                        double lat = deviceInfo.getActive_gps_data().getLat();
                        if (lon == 0) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.get_gps_data_failed));
                        } else {
                            LatLng latLng = convertGpsData(new LatLng(lat, lon));
                            setMapCenter(latLng);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.setFlat(true);
                            markerOptions.anchor(0.5f, 0.5f);
                            markerOptions.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.car_marker));
                            markerOptions.position(latLng);
                            amap.addMarker(markerOptions);
                        }
                    } else {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.get_gps_data_failed));
                    }
                }
            });
        }
    }

    /**
     * 获取车辆位置
     *
     * @param subscriber 事件订阅者用于处理response回调
     */
    private void getCarLocationInfo(Subscriber<DeviceInfo> subscriber) {
        Subscription sub = mobileApi.getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, periodOptions, deviceFields)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (replayThread != null && !replayThread.isInterrupted()) {
            replayThread.interrupt();
            replayThread = null;
            trajectoryStopOrStart.setBackgroundResource(R.drawable.trajectoroy_start);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isTracing) {
            stopPeriodGetData();
            isTracing = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mobileApi = null;
        mCompositeSubscription.clear();
        mCompositeSubscription = null;
    }

    private void showDatePicker(int year, int month) {
        final AlertDialog dialog = new AlertDialog.Builder(MapActivity.this).create();
        dialog.show();
        DPTManager.getInstance().initCalendar(new CustomDatePick());
        DatePicker picker = new DatePicker(MapActivity.this);
        picker.setDate(year, month);
        picker.setMode(DPMode.SINGLE);
        picker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                //获取到日期
                clearMap();
                getPickedDateTrajectoryInfo(date);
                dialog.dismiss();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setContentView(picker, params);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void getPickedDateTrajectoryInfo(String date) {
        if (isWaitingResponse) {
            return;
        }
        isWaitingResponse = true;
        showWaitingDialog();
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("serial", serial);
        String beginTime = date + " 08:00:00";
        options.put("rcv_time", beginTime + "@" + MyUtil.addHourToDate(beginTime, 24));
        options.put("access_token", token);
        options.put("sorts", "rcv_time");
        Subscription sub = mobileApi.getWiGpsDatas(APIUtil.createOptions(MobileApi.Method_Gps_Lists, options, gpsDatasFields)).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<WiGpsDatas>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                isWaitingResponse = false;
                dismissWaitingDialog();
                MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
            }

            @Override
            public void onNext(WiGpsDatas wiGpsDatas) {
                isWaitingResponse = false;
                dismissWaitingDialog();
                if (wiGpsDatas != null) {
                    if (wiGpsDatas.getTotal() < 10) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.no_enough_gps_data));
                    } else {
                        //显示轨迹
                        if (containerTrajectoryControl.getVisibility() == View.INVISIBLE) {
                            containerTrajectoryControl.setVisibility(View.VISIBLE);
                            trajectoryProgress.setMax(wiGpsDatas.getTotal());
                            trajectoryStopOrStart.setBackgroundResource(R.drawable.trajectoroy_stop);
                        }
                        showTrajectory(wiGpsDatas);
                    }
                }
            }
        });
        mCompositeSubscription.add(sub);
    }

    private void showTrajectory(WiGpsDatas wiGpsDatas) {
        //轨迹数据处理
        List<WiGpsDatas.DataBean> datas = wiGpsDatas.getData();
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < datas.size(); i++) {
            points.add(new LatLng(datas.get(i).getLat(), datas.get(i).getLon()));
        }
        points = convertGpsData(points);
        //绘制折线
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(0xFF1E88E5);
        polylineOptions.addAll(points);
        mVirtureRoad = amap.addPolyline(polylineOptions);
        //移动地图中心
        setMapCenter(points.get(0));
        //添加覆盖物
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.car_marker));
        markerOptions.position(polylineOptions.getPoints().get(0));
        mMoveMarker = amap.addMarker(markerOptions);
        mMoveMarker.setRotateAngle((float) getAngle(0));
        //开始回放
        moveLooper();

    }

    private List<LatLng> convertGpsData(List<LatLng> source) {
        List<LatLng> output = new ArrayList<LatLng>();
        for (LatLng obj : source) {
            output.add(convertGpsData(obj));
        }
        return output;
    }

    private LatLng convertGpsData(LatLng latLng) {
        if (converter == null) {
            converter = new CoordinateConverter(MapActivity.this);
        }
        converter.from(CoordinateConverter.CoordType.BAIDU);
        return converter.coord(latLng).convert();
    }

    /**
     * 根据点获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
        LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }

    /**
     * 计算x方向每次移动的距离
     */
    private double getXMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    private void moveCarMarker() {
        moveMarkerThread = new Thread() {
            @Override
            public void run() {
                mMoveMarker.setPosition(start);
                mMoveMarker.setRotateAngle((float) getAngle(start, end));
                double slope = getSlope(start, end);
                //是不是正向的标示（向上设为正向）
                boolean isReverse = (start.latitude > end.latitude);

                double intercept = getInterception(slope, start);

                double xMoveDistance = isReverse ? getXMoveDistance(slope)
                        : -1 * getXMoveDistance(slope);
                for (double j = start.latitude; !((j > end.latitude) ^ isReverse); j = j - xMoveDistance) {
                    if (currentThread().isInterrupted()) {
                        break;
                    }
                    LatLng latLng = null;
                    if (slope != Double.MAX_VALUE) {
                        latLng = new LatLng(j, (j - intercept) / slope);
                    } else {
                        latLng = new LatLng(j, start.longitude);
                    }
                    mMoveMarker.setPosition(latLng);
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        currentThread().interrupt();
                        break;
                    }
                }
                start = end;
            }
        };
        moveMarkerThread.start();
    }

    /**
     * 循环进行移动逻辑
     */
    private void moveLooper() {
        trajectoryProgress.setProgress(0);
        replayThread = new Thread() {

            public void run() {

                for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {
                    if (currentThread().isInterrupted()) {
                        break;
                    }
                    trajectoryProgress.setProgress(i + 1);
                    LatLng startPoint = mVirtureRoad.getPoints().get(i);
                    LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
                    mMoveMarker.setPosition(startPoint);
                    mMoveMarker.setRotateAngle((float) getAngle(startPoint, endPoint));

                    setMapCenter(startPoint);
                    double slope = getSlope(startPoint, endPoint);
                    //是不是正向的标示（向上设为正向）
                    boolean isReverse = (startPoint.latitude > endPoint.latitude);

                    double intercept = getInterception(slope, startPoint);

                    double xMoveDistance = isReverse ? getXMoveDistance(slope)
                            : -1 * getXMoveDistance(slope);

                    for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse); j = j - xMoveDistance) {
                        if (currentThread().isInterrupted()) {
                            break;
                        }
                        LatLng latLng = null;
                        if (slope != Double.MAX_VALUE) {
                            latLng = new LatLng(j, (j - intercept) / slope);
                        } else {
                            latLng = new LatLng(j, startPoint.longitude);
                        }
                        mMoveMarker.setPosition(latLng);
                        try {
                            Thread.sleep(TIME_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            currentThread().interrupt();
                            break;
                        }
                    }

                }
            }


        };
        replayThread.start();
    }

    private void setMapCenter(LatLng point) {
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(point, 0, 0, 0));
        amap.moveCamera(camera);
        amap.moveCamera(CameraUpdateFactory.zoomTo(14.0f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获得权限，发起寻车导航
                naviToTheCar();
            } else {
                MyUtil.showToast(getApplicationContext(), getString(R.string.no_permission));
            }
        }
    }

    @OnClick({R.id.imageLocate, R.id.tv_locate, R.id.imageTrajectory, R.id.imageFindCar, R.id.tv_find_car, R.id.tv_trajectory, R.id.trajectoryStopOrStart, R.id.trajectorySpeedUp, R.id.trajectorySpeedNormal, R.id.trajectorySpeedDown})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageLocate:
                locateEvent();
                break;
            case R.id.tv_locate:
                locateEvent();
                break;
            case R.id.imageTrajectory:
                trajectoryEvent();
                break;
            case R.id.tv_trajectory:
                trajectoryEvent();
                break;
            case R.id.imageFindCar:
                findCarEvent();
                break;
            case R.id.tv_find_car:
                findCarEvent();
                break;
            case R.id.trajectoryStopOrStart:
                if (replayThread != null && !replayThread.isInterrupted()) {
                    replayThread.interrupt();
                    trajectoryStopOrStart.setBackgroundResource(R.drawable.trajectoroy_start);
                    replayThread = null;
                } else {
                    moveLooper();
                    trajectoryStopOrStart.setBackgroundResource(R.drawable.trajectoroy_stop);
                }
                break;
            case R.id.trajectorySpeedUp:
                if (trajectorySpeedDown.getTag() != null && trajectorySpeedDown.getTag().equals("min")) {
                    trajectorySpeedDown.setBackgroundResource(R.drawable.trajectoroy_speed_down);
                    trajectorySpeedDown.setTag("normal");
                }
                if (TIME_INTERVAL != 5) {
                    TIME_INTERVAL = TIME_INTERVAL / 2;
                    if (TIME_INTERVAL == 5) {
                        trajectorySpeedUp.setBackgroundResource(R.drawable.trajectoroy_speed_up_max);
                        trajectorySpeedUp.setTag("max");
                    }
                } else {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.max_replay_speed));
                }
                break;
            case R.id.trajectorySpeedNormal:
                TIME_INTERVAL = 40;
                if (trajectorySpeedUp.getTag() != null && trajectorySpeedUp.getTag().equals("max")) {
                    trajectorySpeedUp.setBackgroundResource(R.drawable.trajectoroy_speed_up);
                    trajectorySpeedUp.setTag("normal");
                }
                if (trajectorySpeedDown.getTag() != null && trajectorySpeedDown.getTag().equals("min")) {
                    trajectorySpeedDown.setBackgroundResource(R.drawable.trajectoroy_speed_down);
                    trajectorySpeedDown.setTag("normal");
                }
                break;
            case R.id.trajectorySpeedDown:
                if (trajectorySpeedUp.getTag() != null && trajectorySpeedUp.getTag().equals("max")) {
                    trajectorySpeedUp.setBackgroundResource(R.drawable.trajectoroy_speed_up);
                    trajectorySpeedUp.setTag("normal");
                }
                if (TIME_INTERVAL != 160) {
                    TIME_INTERVAL = TIME_INTERVAL * 2;
                    if (TIME_INTERVAL == 160) {
                        trajectorySpeedDown.setBackgroundResource(R.drawable.trajectoroy_speed_down_min);
                        trajectorySpeedDown.setTag("min");
                    }
                } else {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.min_replay_speed));
                }
                break;
        }
    }

    private void locateEvent() {
        if (isTracing) {
            return;
        }
        if (!isWaitingResponse) {
            isWaitingResponse = true;
            getCarLocationInfo(new Subscriber<DeviceInfo>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    isWaitingResponse = false;
                    MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                }

                @Override
                public void onNext(DeviceInfo deviceInfo) {
                    isWaitingResponse = false;
                    if (deviceInfo != null) {
                        //设备状态
                        String gpsTime = MyUtil.changeTime(deviceInfo.getActive_gps_data().getGps_time(), 0);
                        int offsetGps = MyUtil.calTimeOffset(gpsTime, APIUtil.getCurrentTime());
                        if (offsetGps >= 0 && offsetGps > 5) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.gps_offline));
                        } else {
                            //准备跟踪
                            clearMap();
                            double lat = deviceInfo.getActive_gps_data().getLat();
                            double lon = deviceInfo.getActive_gps_data().getLon();
                            if (lat != 0) {
                                MyUtil.showToast(getApplicationContext(), getString(R.string.start_tracing));
                                start = convertGpsData(new LatLng(lat, lon));
                                setMapCenter(start);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.setFlat(true);
                                markerOptions.anchor(0.5f, 0.5f);
                                markerOptions.icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.car_marker));
                                markerOptions.position(start);
                                mMoveMarker = amap.addMarker(markerOptions);
                                //执行跟踪
                                isTracing = true;
                                tvLocate.setTextColor(0xFFFF6338);
                                imageLocate.setBackgroundResource(R.drawable.tracing_car_icon);
                                periodGetData();
                            }
                        }
                    } else {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.get_gps_data_failed));
                    }
                }
            });
        }
    }

    /**
     * 轮询获取汽车状态
     */
    private void periodGetData() {
        refreshUISubscription = Observable.create(new Observable.OnSubscribe<DeviceInfo>() {
            @Override
            public void call(final Subscriber<? super DeviceInfo> subscriber) {
                peroidSubscription = Schedulers.io().createWorker().schedulePeriodically(new Action0() {
                    @Override
                    public void call() {
                        try {
                            DeviceInfo info = mobileApi.getDeviceInfoTask(APIUtil.createOptions(MobileApi.Method_Device_Get, periodOptions, deviceFields)).execute().body();
                            if (info == null || TextUtils.isEmpty(info.getDevice_id())) {
                                subscriber.onNext(null);
                            } else {
                                subscriber.onNext(info);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 30, TimeUnit.SECONDS);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<DeviceInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    //通讯异常
                    //MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                }
            }

            @Override
            public void onNext(DeviceInfo deviceInfo) {
                //移动车辆标注
                if (deviceInfo != null) {
                    //Log.i("zeng", "获取到信息");
                    double lat = deviceInfo.getActive_gps_data().getLat();
                    double lon = deviceInfo.getActive_gps_data().getLon();
                    if (lat != 0) {
                        end = convertGpsData(new LatLng(lat, lon));
                        moveCarMarker();
                    }
                }
            }
        });
    }

    /**
     * 停止轮询
     */
    private void stopPeriodGetData() {
        tvLocate.setTextColor(0xFF666666);
        imageLocate.setBackgroundResource(R.drawable.locate_car_icon);
        if (peroidSubscription != null && !peroidSubscription.isUnsubscribed()) {
            peroidSubscription.unsubscribe();
            peroidSubscription = null;
            refreshUISubscription.unsubscribe();
            refreshUISubscription = null;
        }
        if (moveMarkerThread != null && !moveMarkerThread.isInterrupted()) {
            moveMarkerThread.interrupt();
            moveMarkerThread = null;
        }
    }

    private void trajectoryEvent() {
        Calendar calendar = Calendar.getInstance();
        showDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }


    private void findCarEvent() {
        if (Build.VERSION.SDK_INT > 22 && ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //申请授权
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            //直接发起寻车导航
            naviToTheCar();
        }
    }

    private void naviToTheCar() {
        if (!isWaitingResponse) {
            isWaitingResponse = true;
            getCarLocationInfo(new Subscriber<DeviceInfo>() {
                @Override
                public void onCompleted() {

                }
                @Override
                public void onError(Throwable e) {
                    isWaitingResponse = false;
                    MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                }

                @Override
                public void onNext(DeviceInfo deviceInfo) {
                    isWaitingResponse = false;
                    if (deviceInfo != null) {
                        //设备状态
                        //进入导航
                        clearMap();
                        Intent intent = new Intent(MapActivity.this, NaviCarActivity.class);
                        intent.putExtra("lat", deviceInfo.getActive_gps_data().get_lat());
                        intent.putExtra("lon", deviceInfo.getActive_gps_data().get_lon());
                        if (deviceInfo.getActive_gps_data().getLat() == 0) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.get_gps_data_failed));
                        } else {
                            startActivity(intent);
                        }
                    } else {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.get_gps_data_failed));
                    }
                }
            });
        }
    }

    private void clearMap() {
        if (amap != null) {
            amap.clear();
        }
        if (containerTrajectoryControl.getVisibility() == View.VISIBLE) {
            containerTrajectoryControl.setVisibility(View.INVISIBLE);
        }
        if (replayThread != null && !replayThread.isInterrupted()) {
            replayThread.interrupt();
            replayThread = null;
        }
        if (isTracing) {
            stopPeriodGetData();
            isTracing = false;
        }
    }

    private void showWaitingDialog() {
        dialog = new AlertDialog.Builder(MapActivity.this).create();
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
