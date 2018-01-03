package com.vrd.tech.yfd.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.victor.loading.rotate.RotateLoading;
import com.vrd.tech.yfd.BuildConfig;
import com.vrd.tech.yfd.R;
import com.vrd.tech.yfd.entity.DeviceInfo;
import com.vrd.tech.yfd.entity.LoginInfo;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.entity.WiVehicleList;
import com.vrd.tech.yfd.network.APIUtil;
import com.vrd.tech.yfd.network.MobileApi;
import com.vrd.tech.yfd.network.NetWorkHelper;
import com.vrd.tech.yfd.util.MyUtil;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

public class MainActivity extends AppCompatActivity implements GeocodeSearch.OnGeocodeSearchListener, AdapterView.OnItemClickListener {
    LoginInfo loginInfo;
    String username;
    String password;
    Subscription peroidSubscription, refreshUISubscription; //轮询订阅者
    CompositeSubscription mCompositeSubscription
            = new CompositeSubscription();
    int currentPosition = 0;//选中车辆的位置
    GeocodeSearch geocodeSearch;
    @Bind(R.id.tv_obj_name)
    TextView tvObjName;
    @Bind(R.id.container_car_obj_name)
    PercentRelativeLayout containerCarObjName;
    @Bind(R.id.tv_car_location)
    TextView tvCarLocation;
    @Bind(R.id.container_location)
    PercentRelativeLayout containerLocation;
    @Bind(R.id.device_state)
    ImageView deviceState;
    @Bind(R.id.gps_state)
    ImageView gpsState;
    @Bind(R.id.image_alert)
    ImageView imageAlert;
    @Bind(R.id.car_body)
    RelativeLayout carBody;
    @Bind(R.id.car_front_light)
    ImageView carFrontLight;
    @Bind(R.id.front_left)
    ImageView frontLeft;
    @Bind(R.id.back_left)
    ImageView backLeft;
    @Bind(R.id.front_right)
    ImageView frontRight;
    @Bind(R.id.back_right)
    ImageView backRight;
    @Bind(R.id.image_lock)
    ImageView imageLock;
    @Bind(R.id.image_menu)
    ImageView imageMenu;
    @Bind(R.id.image_unlock)
    ImageView imageUnlock;
    @Bind(R.id.image_start)
    ImageView image_start;
    ListPopupWindow carlist;
    ArrayAdapter<String> carlistAdapter;
    @Bind(R.id.tv_voltage)
    TextView tvVoltage;
    List<String> objNameArr;
    @Bind(R.id.rotateloading)
    RotateLoading rotateloading;
    @Bind(R.id.tv_wait_moment)
    TextView tvWaitMoment;
    PopupWindow mainMenu;
    int menuOffsetY = 0;
    ImageView faIcon1, faIcon2, faIcon3, faIcon4, faIcon5, faIcon6;
    @Bind(R.id.wifiCamera)
    ImageView wifiCamera;
    //载入语音文件
    private SoundPool soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
    private SparseArray<Integer> soundPoolMap;
    private boolean isSendingCmd = false; //是否正在执行指令
    boolean isDeviceOnline = false; //设备是否在线
    boolean isDoorOpen = false; //车门是否打开
    AlertDialog stopDialog = null; //熄火对话框
    boolean menuQuickClick = false; //防止菜单多次响应
    boolean isChagingCar = false;  //是否正在更换车辆
    long quickClickProof = 0;

    private MobileApi mobileApi;
    private WiVehicleList wiVehicleList;  //车辆列表原始bean
    private final String deviceFields = "cust_id,sim,serial,p20c_status,active_gps_data,active_obd_data,params";
    private HashMap<String, String> deviceOptions;
    private DeviceInfo currentDeviceInfo; //当前设备状态信息
    private String currentDeviceId;
    private String accessToken;
    private boolean isRefreshing = false; //是否正在轮询标志
    long defaultInterval = 1500; //默认更新UI时间间隔
    long checkStartTime = 0; //操作成功检查开始时间

    public SharedPreferences mySharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        carlist = new ListPopupWindow(this);
        carlist.setAnchorView(containerCarObjName);
        carlist.setOnItemClickListener(this);
        mobileApi = NetWorkHelper.getInstance().getMobileApi();
        deviceOptions = new HashMap<String, String>();
        initUserCarInfo();
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        rotateloading.bringToFront();
        showWaitingIcon();
        //初始化menu
        initMenu();
        //载入语音文件
        soundPoolMap = new SparseArray<Integer>(4);
        soundPoolMap.put(1, soundPool.load(this, R.raw.lock, 1));
        soundPoolMap.put(2, soundPool.load(this, R.raw.unlock, 1));
        soundPoolMap.put(3, soundPool.load(this, R.raw.start_car, 1));
        soundPoolMap.put(4, soundPool.load(this, R.raw.alert, 1));

        //启动按钮长按，响应启动
        image_start.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v.getId() == R.id.image_start) {
                    int is_start = currentDeviceInfo.getParams().getIs_start();
                    if (isSendingCmd) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.frequent_operation));
                    } else if (!isDeviceOnline) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.device_offline));
                    } else if (!currentDeviceInfo.getParams().isIs_lockdoor()) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.door_unlock_cant_start));
                    } else if (is_start == 1 || is_start == 2) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.start_to_start));
                    } else {
                        //执行启动
                        boxerSendCmd(MobileApi.CMD_STARTENGINE);
                    }
                }
                return false;
            }
        });

    }

    private void initMenu() {
        View menuView = LayoutInflater.from(this).inflate(R.layout.main_menu, null);
        int[] screenWh = MyUtil.getScreenWidthAndHeight(getApplicationContext());
        int menuWidth = screenWh[0] - MyUtil.dip2px(getApplicationContext(), 10);
        mainMenu = new PopupWindow(menuView, menuWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mainMenu.setFocusable(true);//屏蔽外部点击
        mainMenu.setBackgroundDrawable(new ColorDrawable(0x00000000));//点击外部和按下back会关闭menu
        mainMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                imageMenu.setImageResource(R.drawable.menu_close);
            }
        });
        faIcon1 = (ImageView) menuView.findViewById(R.id.fa_icon_1);
        faIcon1.setOnClickListener(menuOnClick);
        faIcon2 = (ImageView) menuView.findViewById(R.id.fa_icon_2);
        faIcon2.setOnClickListener(menuOnClick);
        faIcon3 = (ImageView) menuView.findViewById(R.id.fa_icon_3);
        faIcon3.setOnClickListener(menuOnClick);
        faIcon4 = (ImageView) menuView.findViewById(R.id.fa_icon_4);
        faIcon4.setOnClickListener(menuOnClick);
        faIcon5 = (ImageView) menuView.findViewById(R.id.fa_icon_5);
        faIcon5.setOnClickListener(menuOnClick);
        faIcon6 = (ImageView) menuView.findViewById(R.id.fa_icon_6);
        faIcon6.setOnClickListener(menuOnClick);
    }

    View.OnClickListener menuOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (menuQuickClick) {
                return;
            } else {
                menuQuickClick = true;
                Observable.timer(500, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        menuQuickClick = false;
                    }
                });
            }
            switch (v.getId()) {
                case R.id.fa_icon_1:
                    //进入obd信息页面（即远程诊断）
                    if (currentDeviceInfo == null) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
                    } else {
                        if (TextUtils.isEmpty(currentDeviceInfo.getActive_obd_data().getDpdy())) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.no_obd_data));
                        } else {
                            toAnotherActivity(OBDActivity.class, 0);
                        }
                    }
                    break;
                case R.id.fa_icon_2:
                    //发送寻车指令
                    if (isSendingCmd) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.frequent_operation));
                    } else if (!isDeviceOnline) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.device_offline));
                    } else if (currentDeviceInfo.getParams().getIs_start() == 1) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.start_cant_find_car));
                    } else {
                        sendFindCarCmd();
                    }
                    break;
                case R.id.fa_icon_3:
                    //违章查询
                    MyUtil.showToast(getApplicationContext(), getString(R.string.add_in_future));
                    break;
                case R.id.fa_icon_4:
                    //车辆设置
                    if (currentDeviceInfo == null) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
                    } else {
                        toAnotherActivity(CarSetActivity.class, 0);
                    }
                    break;
                case R.id.fa_icon_5:
                    //更多页面
                    if (currentDeviceInfo == null) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
                    } else {
                        toAnotherActivity(MoreActivity.class, 0);
                    }
                    break;
                case R.id.fa_icon_6:
                    //历史消息
                    if (currentDeviceInfo == null) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
                    } else {
                        toAnotherActivity(HistoryInfoActivity.class, 0);
                    }
                    break;
            }
        }
    };

    private void toAnotherActivity(Class<?> cls, int messageType) {
        Intent intent = new Intent(MainActivity.this, cls);
        intent.putExtra("objId", wiVehicleList.getData().get(currentPosition).getObj_id());
        intent.putExtra("device_id", currentDeviceId);
        intent.putExtra("access_token", loginInfo.getAccess_token());
        intent.putExtra("objName", wiVehicleList.getData().get(currentPosition).getObj_name());
        intent.putExtra("serial", currentDeviceInfo.getSerial());
        Log.e("tag", "设备编号ID           "+currentDeviceInfo.getSerial());
        intent.putExtra("messageType", messageType);//消息类型，进入消息页面时起作用
        if (cls.getName().contains("CarSet")) {
            startActivityForResult(intent, 101);
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String obj_name = data.getStringExtra("obj_name");
            tvObjName.setText(obj_name);
            objNameArr.set(currentPosition, obj_name);
            carlistAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 反序列化LoginInfo
     *
     * @param subscriber
     */
    private void deserializationLoginInfo(Subscriber<? super String> subscriber) {
        File dir = getFilesDir();
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(new File(dir.getPath() + "loginInfo")));
            loginInfo = (LoginInfo) ois.readObject();
            if (loginInfo != null)
                subscriber.onNext(loginInfo.getAccess_token());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            MyUtil.IOClose(ois);
        }
    }

    /**
     * 初始化车辆列表
     */
    private void initUserCarInfo() {
        mCompositeSubscription.add(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                SharedPreferences sp = getSharedPreferences("vrd_yfd", MODE_PRIVATE);
                username = sp.getString("username", "");
                password = sp.getString("password", "");
                deserializationLoginInfo(subscriber);
            }
        }).subscribeOn(Schedulers.io()).flatMap(new Func1<String, Observable<WiVehicleList>>() {
            @Override
            public Observable<WiVehicleList> call(String token) {
                HashMap<String, String> options = new HashMap<String, String>();
                options.put("cust_id", loginInfo.getCust_id());
                mySharedPreferences = getSharedPreferences("custID",Context.MODE_PRIVATE);
                editor=mySharedPreferences.edit();
                editor.putString("cust",loginInfo.getCust_id());
                editor.putString("atoken",loginInfo.getAccess_token());
                editor.commit();
                options.put("access_token", loginInfo.getAccess_token());
                String fields = "device_id,obj_name";
                return mobileApi.getVehicleList(APIUtil.createOptions(MobileApi.Method_Vehicle_List, options, fields));
            }
        }).flatMap(new Func1<WiVehicleList, Observable<DeviceInfo>>() {
            @Override
            public Observable<DeviceInfo> call(WiVehicleList wiVehicleList) {
                if (wiVehicleList != null && wiVehicleList.getTotal() != 0) {
                    MainActivity.this.wiVehicleList = wiVehicleList;
                    bindSpinner();
                    currentDeviceId = wiVehicleList.getData().get(0).getDevice_id();
                    accessToken = loginInfo.getAccess_token();
                    deviceOptions.put("device_id", currentDeviceId);
                    deviceOptions.put("access_token", accessToken);
                    return mobileApi.getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, deviceOptions, deviceFields));
                } else {
                    throw new RuntimeException("no vehicle");
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<DeviceInfo>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingIcon();
                if (e.getMessage() == null) {
                    MyUtil.showToast(getApplicationContext(), getString(R.string.network_exception));
                } else {
                    if (e.getMessage().equals("no vehicle")) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
                    }
                }
            }

            @Override
            public void onNext(DeviceInfo result) {
                currentDeviceInfo = result;
                dismissWaitingIcon();
                tvObjName.setText(objNameArr.get(currentPosition));
                periodGetData(defaultInterval);
                bindDataToUi(result);
            }
        }));
    }

    /**
     * 发控制指令
     *
     * @param cmd
     */
    private Observable<StatusCode> sendCommand(String cmd) {
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("device_id", currentDeviceId);
        body.put("params", "{}");
        body.put("cmd_type", cmd);
        body.put("access_token", accessToken);
        return mobileApi.getStatusCode(APIUtil.createOptions(MobileApi.Method_Command, body));
    }

    /**
     * 绑定车辆信息以供选择
     */
    private void bindSpinner() {
        //绑定车辆信息
        objNameArr = new ArrayList<String>();
        for (int i = 0; i < wiVehicleList.getTotal(); i++) {
            objNameArr.add(wiVehicleList.getData().get(i).getObj_name());
        }
        carlistAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item_carlist, objNameArr);
        carlistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                carlist.setAdapter(carlistAdapter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
        closeCarList();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPeriodGetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (peroidSubscription == null && currentDeviceId != null) {
            periodGetData(defaultInterval);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i == 1000) {
            String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            //Log.i("main", "address=" + address);
            if (address.length() > 20) {
                address = address.substring(0, 20) + "..";
            }
            String gps_time = "";
            if (currentDeviceInfo != null) {
                gps_time = MyUtil.changeTime(currentDeviceInfo.getActive_gps_data().getGps_time(), 2).substring(0, 5);
            }
            tvCarLocation.setText(address + " " + gps_time);
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @OnClick({R.id.container_car_obj_name, R.id.container_location, R.id.image_menu, R.id.image_lock, R.id.image_unlock, R.id.image_start, R.id.image_alert, R.id.wifiCamera})
    public void onClick(View view) {
        if (System.currentTimeMillis() - quickClickProof < 500) {
            return;
        }
        quickClickProof = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.container_car_obj_name:
                if (carlist != null && !carlist.isShowing())
                    carlist.show();
                break;
            case R.id.container_location:
                //进入地图页面
                if (currentDeviceInfo != null) {
                    toAnotherActivity(MapActivity.class, 0);
                } else {
                    //无车辆信息
                }
                break;
            case R.id.image_menu:
                if (menuOffsetY == 0) {
                    int[] screenWh = MyUtil.getScreenWidthAndHeight(getApplicationContext());
                    menuOffsetY = imageAlert.getHeight() + (int) (screenWh[1] * 0.09) + MyUtil.dip2px(getApplicationContext(), 5);
                }
                if (mainMenu.isShowing()) {
                    mainMenu.dismiss();
                } else {
                    mainMenu.showAtLocation(imageMenu, Gravity.BOTTOM, 0, menuOffsetY);
                    imageMenu.setImageResource(R.drawable.menu_open);
                }
                break;
            case R.id.image_lock:
                if (currentDeviceInfo != null) {
                    if (isSendingCmd) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.frequent_operation));
                    } else {
                        int is_start = currentDeviceInfo.getParams().getIs_start();
                        if (is_start == 1 || is_start == 2) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.start_cant_lock));
                        } else if (isDoorOpen) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.door_open_cant_lock));
                        } else if (currentDeviceInfo.getParams().isIs_lockdoor()) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.lock_to_lock));
                        } else {
                            boxerSendCmd(MobileApi.CMD_LOCKDOOR);
                        }
                    }
                } else {
                    //无车辆
                }

                break;
            case R.id.image_unlock:
                if (currentDeviceInfo != null) {
                    if (isSendingCmd) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.frequent_operation));
                    } else {
                        if (currentDeviceInfo.getParams().getIs_start() == 1) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.start_cant_unlock));
                        } else if (!currentDeviceInfo.getParams().isIs_lockdoor()) {
                            MyUtil.showToast(getApplicationContext(), getString(R.string.unlock_to_unlock));
                        } else {
                            boxerSendCmd(MobileApi.CMD_UNLOCKDOOR);
                        }
                    }
                } else {
                    //无车辆
                }
                break;
            case R.id.image_start:
                if (currentDeviceInfo != null) {
                    if (currentDeviceInfo.getParams().getIs_start() == 1) {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.start_cant_stop));
                    } else if (currentDeviceInfo.getParams().getIs_start() == 2) {
                        if (stopDialog == null) {
                            stopDialog = new AlertDialog.Builder(MainActivity.this).create();
                            stopDialog.setTitle(getString(R.string.stop_engine));
                            stopDialog.setMessage(getString(R.string.stop_dialog_msg));
                            stopDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_yes),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (isSendingCmd) {
                                                MyUtil.showToast(getApplicationContext(), getString(R.string.frequent_operation));
                                            } else {
                                                boxerSendCmd(MobileApi.CMD_STOPENGINE);
                                            }
                                            dialog.dismiss();
                                        }
                                    });
                            stopDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.btn_no),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            stopDialog.show();
                        } else {
                            if (!stopDialog.isShowing()) {
                                stopDialog.show();
                            }
                        }
                    } else {
                        //不在启动状态
                    }
                } else {
                    //无车辆信息
                }
                break;
            case R.id.image_alert:
                //防盗报警消息
//                if (currentCarInfo == null) {
//                    MyUtil.showToast(getApplicationContext(), getString(R.string.no_vehicle));
//                } else {
//                    if (currentCarInfo.getAlarm_num() > 0) {
//                        toAnotherActivity(HistoryInfoActivity.class, 1);
//                    }
//                }
                break;
            case R.id.wifiCamera:
                //记录仪
//                if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    mDrawerLayout.openDrawer(GravityCompat.START);
//                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isSendingCmd) {
            closeCarList();
            return;
        }
        if (isChagingCar) {
            return;
        }
        if (!isRefreshing || currentPosition != position) {
            isChagingCar = true;
            currentPosition = position;
            showWaitingIcon();
            tvObjName.setText(objNameArr.get(currentPosition));
            tvCarLocation.setText("");
            stopPeriodGetData();
            currentDeviceId = wiVehicleList.getData().get(currentPosition).getDevice_id();
            deviceOptions.put("device_id", currentDeviceId);
            mobileApi.getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, deviceOptions, deviceFields)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<DeviceInfo>() {
                @Override
                public void onCompleted() {
                    dismissWaitingIcon();
                    isChagingCar = false;
                }

                @Override
                public void onError(Throwable e) {
                    dismissWaitingIcon();
                    isChagingCar = false;
                    if (BuildConfig.DEBUG) {
                        if (e.getMessage() != null)
                            MyUtil.showToast(getApplicationContext(), "异常信息：" + e.getMessage());
                    } else {
                        MyUtil.showToast(getApplicationContext(), getString(R.string.network_retry));
                    }
                }

                @Override
                public void onNext(DeviceInfo deviceInfo) {
                    periodGetData(defaultInterval);
                    bindDataToUi(deviceInfo);
                }
            });
        }
        closeCarList();
    }

    /**
     * 绑定数据到UI
     */
    private void bindDataToUi(DeviceInfo carInfo) {
        if (isChagingCar) {
            return;
        }
        // Log.i("main", "绑定数据: " + carInfo.toString());
        //解析地址信息
        if (0 != carInfo.getActive_gps_data().get_lat() || carInfo.getActive_gps_data().get_lon() != 0) {
            RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(carInfo.getActive_gps_data().get_lat(), carInfo.getActive_gps_data().get_lon()), 200, GeocodeSearch.GPS);
            geocodeSearch.getFromLocationAsyn(query);
        }
        //设置开关锁
        if (carInfo.getParams().isIs_lockdoor()) {
            imageLock.setImageResource(R.drawable.lock_true);
            imageUnlock.setImageResource(R.drawable.unlock_false);
        } else {
            imageLock.setImageResource(R.drawable.lock_false);
            imageUnlock.setImageResource(R.drawable.unlock_ture);
        }
        //设置启动
        if (1 == carInfo.getParams().getIs_start() || 2 == carInfo.getParams().getIs_start()) {
            image_start.setImageResource(R.drawable.button_stop);
        } else {
            image_start.setImageResource(R.drawable.button_start);
        }
        if (carInfo.getP20c_status() != null) {
            //设置门状态
            if (carInfo.getP20c_status().isP20C_LEFT_FRONT_DOOR() || carInfo.getP20c_status().isP20C_LEFT_BACK_DOOR() || carInfo.getP20c_status().isP20C_RIGHT_FRONT_DOOR() || carInfo.getP20c_status().isP20C_RIGHT_BACK_DOOR()) {
                isDoorOpen = true;
            } else {
                isDoorOpen = false;
            }
            //左前门
            if (carInfo.getP20c_status().isP20C_LEFT_FRONT_DOOR()) {
                if (carInfo.getP20c_status().isP20C_LEFT_FRONT_WIN()) {
                    frontLeft.setImageResource(R.drawable.front_left_open_win);
                } else {
                    frontLeft.setImageResource(R.drawable.front_left_open);
                }
            } else {
                if (carInfo.getP20c_status().isP20C_LEFT_FRONT_WIN()) {
                    frontLeft.setImageResource(R.drawable.front_left_close_win);
                } else {
                    frontLeft.setImageResource(R.drawable.front_left_close);
                }
            }
            //左后门
            if (carInfo.getP20c_status().isP20C_LEFT_BACK_DOOR()) {
                if (carInfo.getP20c_status().isP20C_LEFT_BACK_WIN()) {
                    backLeft.setImageResource(R.drawable.back_left_open_win);
                } else {
                    backLeft.setImageResource(R.drawable.back_left_open);
                }
            } else {
                if (carInfo.getP20c_status().isP20C_LEFT_BACK_WIN()) {
                    backLeft.setImageResource(R.drawable.back_left_close_win);
                } else {
                    backLeft.setImageResource(R.drawable.back_left_close);
                }
            }
            //右前门
            if (carInfo.getP20c_status().isP20C_RIGHT_FRONT_DOOR()) {
                if (carInfo.getP20c_status().isP20C_RIGHT_FRONT_WIN()) {
                    frontRight.setImageResource(R.drawable.front_right_open_win);
                } else {
                    frontRight.setImageResource(R.drawable.front_right_open);
                }
            } else {
                if (carInfo.getP20c_status().isP20C_RIGHT_FRONT_WIN()) {
                    frontRight.setImageResource(R.drawable.front_right_close_win);
                } else {
                    frontRight.setImageResource(R.drawable.front_right_close);
                }
            }
            //右后门
            if (carInfo.getP20c_status().isP20C_RIGHT_BACK_DOOR()) {
                if (carInfo.getP20c_status().isP20C_RIGHT_BACK_WIN()) {
                    backRight.setImageResource(R.drawable.back_right_open_win);
                } else {
                    backRight.setImageResource(R.drawable.back_right_open);
                }
            } else {
                if (carInfo.getP20c_status().isP20C_RIGHT_BACK_WIN()) {
                    backRight.setImageResource(R.drawable.back_right_close_win);
                } else {
                    backRight.setImageResource(R.drawable.back_right_close);
                }
            }
            //车灯
            if (carInfo.getP20c_status().isP20C_LITTLE_LAMP()) {
                carFrontLight.setVisibility(View.VISIBLE);
            } else {
                carFrontLight.setVisibility(View.INVISIBLE);
            }
        }
        if (carInfo.getActive_obd_data() != null) {
            //电压
            String volt = carInfo.getActive_obd_data().getDpdy();
            if (volt.length() > 4) {
                tvVoltage.setText(volt.substring(0, 4) + "V");
            } else {
                tvVoltage.setText(volt + "V");
            }
        }
        //设备状态
        String currentTime = APIUtil.getCurrentTime();
        String deviceTime = MyUtil.changeTime(carInfo.getActive_gps_data().getRcv_time(), 0);
        int offsetDevice = MyUtil.calTimeOffset(deviceTime, currentTime);
        if (offsetDevice < 5 && offsetDevice >= 0) {
            deviceState.setImageResource(R.drawable.online);
            isDeviceOnline = true;
        } else {
            deviceState.setImageResource(R.drawable.offline);
            isDeviceOnline = false;
        }
        //gps状态
        String gpsTime = MyUtil.changeTime(carInfo.getActive_gps_data().getGps_time(), 0);
        int offsetGps = MyUtil.calTimeOffset(gpsTime, currentTime);
        if (offsetGps < 5 && offsetGps >= 0) {
            gpsState.setImageResource(R.drawable.online);
        } else {
            gpsState.setImageResource(R.drawable.offline);
        }
        //报警状态
//        if (carInfo.getAlarm_num() > 0) {
//            imageAlert.setImageResource(R.drawable.alert_info_true);
//        } else {
//            imageAlert.setImageResource(R.drawable.alert_info);
//        }
    }

    boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                MyUtil.showToast(getApplicationContext(), getString(R.string.ensure_exit));
                mCompositeSubscription.add(Observable.timer(1500, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        isExit = false;
                    }
                }));
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void
    playSound(int position) {
        AudioManager mgr = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        float currentVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
//        Log.i("main", "当前音量" + currentVolume);
//        float maxVolume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//        Log.i("main", "最大音量" + maxVolume);
        if (null != soundPoolMap)
            soundPool.play(soundPoolMap.get(position), currentVolume, currentVolume, 1, 0, 1.0f);
    }


    private void closeCarList() {
        if (carlist.isShowing()) {
            carlist.dismiss();
        }
    }

    private void showWaitingIcon() {
        rotateloading.start();
        tvWaitMoment.setVisibility(View.VISIBLE);
    }

    private void dismissWaitingIcon() {
        rotateloading.stop();
        tvWaitMoment.setVisibility(View.INVISIBLE);
    }

    private void sendFindCarCmd() {
        isSendingCmd = true;
        showWaitingIcon();
        mCompositeSubscription.add(sendCommand(MobileApi.CMD_FINDVEHICLE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<StatusCode>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                dismissWaitingIcon();
                isSendingCmd = false;
            }

            @Override
            public void onNext(StatusCode statusCode) {
                dismissWaitingIcon();
                isSendingCmd = false;
            }
        }));
    }


    private void carBodyFlash() {
        carBody.setBackgroundResource(R.drawable.car_with_light);
        Observable.timer(150, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                carBody.setBackgroundResource(R.drawable.car_nolight);
            }
        });
    }


    /**
     * 检查操作是否成功
     */
    private void checkControlState(String command) {
        checkStartTime = System.currentTimeMillis();
        long intervalTime = command.equals(MobileApi.CMD_STARTENGINE) ? 1500 : 500;
        periodGetData(intervalTime, command);
    }

    /**
     * 完整发送控制指令操作
     *
     * @param cmd
     */
    private void boxerSendCmd(final String cmd) {
        if (!isDeviceOnline) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.device_offline));
            return;
        }
        preSendCommand(cmd);
        Subscription sub = sendCommand(cmd).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<StatusCode>(getApplicationContext()) {
            @Override
            protected void onSubError(Throwable e) {
                onFailSendCommand(cmd);
            }

            @Override
            protected void onSubEnd() {
                //过程结束，重置状态
                isSendingCmd = false;
                dismissWaitingIcon();
                periodGetData(defaultInterval);
            }

            @Override
            public void onNext(StatusCode statusCode) {
                if (statusCode != null && statusCode.getStatus_code() == 0) {
                    //指令下发成功，验证状态
                    checkControlState(cmd);
                } else {
                    //指令下发失败
                    onFailSendCommand(cmd);
                }
            }
        });
        mCompositeSubscription.add(sub);
    }

    /**
     * 发送指令准备工作
     *
     * @param cmd
     */
    private void preSendCommand(String cmd) {
        stopPeriodGetData();
        showWaitingIcon();
        if (cmd.equals(MobileApi.CMD_LOCKDOOR)) {
            playSound(1);
            carBodyFlash();
        }
        if (cmd.equals(MobileApi.CMD_UNLOCKDOOR)) {
            playSound(2);
            carBodyFlash();
            Observable.timer(350, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    carBodyFlash();
                }
            });
        }
        if (cmd.equals(MobileApi.CMD_STARTENGINE)) {
            playSound(3);
        }
        isSendingCmd = true;
    }

    /**
     * 下发指令失败
     *
     * @param cmd
     */
    private void onFailSendCommand(String cmd) {
        if (cmd.equals(MobileApi.CMD_STARTENGINE)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.start_fail));
        }
        if (cmd.equals(MobileApi.CMD_LOCKDOOR)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.lock_fail));
        }
        if (cmd.equals(MobileApi.CMD_UNLOCKDOOR)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.unlock_fail));
        }
        if (cmd.equals(MobileApi.CMD_STOPENGINE)) {
            MyUtil.showToast(getApplicationContext(), getString(R.string.stop_fail));
        }
    }

    /**
     * 检查控制命令字段是否已经更改
     *
     * @param cmd
     */
    private void checkControlField(String cmd) {
        long timeoutCheck = cmd.equals(MobileApi.CMD_STARTENGINE) ? 40 * 1000 : 5 * 1000;
        if (cmd.equals(MobileApi.CMD_STARTENGINE)) {
            checkStartField(timeoutCheck);
        }
        if (cmd.equals(MobileApi.CMD_STOPENGINE)) {
            checkStopField(timeoutCheck);
        }
        if (cmd.equals(MobileApi.CMD_UNLOCKDOOR)) {
            checkUnlockField(timeoutCheck);
        }
        if (cmd.equals(MobileApi.CMD_LOCKDOOR)) {
            checkLockField(timeoutCheck);
        }
    }

    private void checkStartField(long timeOut) {
        if (System.currentTimeMillis() - checkStartTime > timeOut) {
            //超时失败
            MyUtil.showToast(getApplicationContext(), getString(R.string.start_fail));
            checkFieldEnd();
        } else {
            if (currentDeviceInfo.getParams().getIs_start() == 2) {
                //成功
                MyUtil.showToast(getApplicationContext(), getString(R.string.start_success));
                checkFieldEnd();
            }
        }
    }

    private void checkLockField(long timeOut) {
        if (System.currentTimeMillis() - checkStartTime > timeOut) {
            //超时失败
            MyUtil.showToast(getApplicationContext(), getString(R.string.lock_fail));
            checkFieldEnd();
        } else {
            if (currentDeviceInfo.getParams().isIs_lockdoor()) {
                //成功
                MyUtil.showToast(getApplicationContext(), getString(R.string.lock_success));
                checkFieldEnd();
            }
        }
    }

    private void checkUnlockField(long timeOut) {
        if (System.currentTimeMillis() - checkStartTime > timeOut) {
            //超时失败
            MyUtil.showToast(getApplicationContext(), getString(R.string.unlock_fail));
            checkFieldEnd();
        } else {
            if (!currentDeviceInfo.getParams().isIs_lockdoor()) {
                //成功
                MyUtil.showToast(getApplicationContext(), getString(R.string.unlock_success));
                checkFieldEnd();
            }
        }
    }

    private void checkStopField(long timeOut) {
        if (System.currentTimeMillis() - checkStartTime > timeOut) {
            //超时失败
            MyUtil.showToast(getApplicationContext(), getString(R.string.stop_fail));
            checkFieldEnd();
        } else {
            if (currentDeviceInfo.getParams().getIs_start() == 0) {
                //成功
                MyUtil.showToast(getApplicationContext(), getString(R.string.stop_success));
                checkFieldEnd();
            }
        }
    }

    /**
     * 操作结束，重置状态
     */
    private void checkFieldEnd() {
        checkStartTime = 0;
        stopPeriodGetData();
        dismissWaitingIcon();
        isSendingCmd = false;
        bindDataToUi(currentDeviceInfo);
        periodGetData(defaultInterval);
    }

    /**
     * 轮询获取汽车状态
     */
    private void periodGetData(final long delay) {
        isRefreshing = true;
        periodGetData(delay, "");
    }

    private void periodGetData(final long delay, final String cmd) {
        refreshUISubscription = mobileApi.getDeviceInfo(APIUtil.createOptions(MobileApi.Method_Device_Get, deviceOptions, deviceFields)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<DeviceInfo>() {
            @Override
            public void onCompleted() {
                //完成发起下一次请求
                peroidSubscription = Observable.timer(delay, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        periodGetData(delay);
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                //失败也发起下一次请求
                peroidSubscription = Observable.timer(delay, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        periodGetData(delay);
                    }
                });
            }

            @Override
            public void onNext(DeviceInfo deviceInfo) {
                currentDeviceInfo = deviceInfo;
                if (isSendingCmd) {
                    //状态验证用
                    if (!TextUtils.isEmpty(cmd)) {
                        checkControlField(cmd);
                    }
                } else {
                    //平常刷新视图
                    bindDataToUi(deviceInfo);
                }
            }
        });
    }

    /**
     * 停止轮询
     */
    private void stopPeriodGetData() {
        Log.i("zeng", "停止轮询");
        isRefreshing = false;
        if (peroidSubscription != null && !peroidSubscription.isUnsubscribed()) {
            peroidSubscription.unsubscribe();
            peroidSubscription = null;
        }
        if (refreshUISubscription != null && !refreshUISubscription.isUnsubscribed()) {
            refreshUISubscription.unsubscribe();
            refreshUISubscription = null;
        }
    }
}
