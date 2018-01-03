package com.vrd.tech.yfd.network;

import com.vrd.tech.yfd.entity.AccessToken;
import com.vrd.tech.yfd.entity.CreateVehicleInfo;
import com.vrd.tech.yfd.entity.DeviceInfo;
import com.vrd.tech.yfd.entity.LoginInfo;
import com.vrd.tech.yfd.entity.RegisterInfo;
import com.vrd.tech.yfd.entity.StatusCode;
import com.vrd.tech.yfd.entity.UsertExist;
import com.vrd.tech.yfd.entity.ValidCheck;
import com.vrd.tech.yfd.entity.WiGpsDatas;
import com.vrd.tech.yfd.entity.WiHistoryInfo;
import com.vrd.tech.yfd.entity.WiUserInfo;
import com.vrd.tech.yfd.entity.WiVehicle;
import com.vrd.tech.yfd.entity.WiVehicleList;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by tsang on 16/7/1.
 */
public interface MobileApi {
    public static final String Method_Access_Token = "wicare.user.access_token";//获取token
    public static final String Method_User_User_exist = "wicare.user.exists"; //判断用户是否存在
    public static final String Method_User_Login = "wicare.user.login"; //登陆
    public static final String Method_User_Register = "wicare.user.register"; //注册
    public static final String Method_User_Psd_Reset = "wicare.user.password.reset"; //密码重置
    public static final String Method_User_Volid_Code = "wicare.user.valid_code"; //验证短信验证码

    public static final String Wicare_User_Get = "wicare.user.get"; //获取用户信息
    public static final String Wicare_User_Update = "wicare.user.update"; //更改用户信息
    public static final String Method_Comm_Sms_Send = "wicare.comm.sms.send"; //发送短信验证码

    public static final String Method_Vehicle_Create = "wicare.vehicle.create";//创建车辆信息
    public static final String Method_Vehicle_Delete = "wicare.vehicle.delete";//删除车辆信息
    public static final String Method_Vehicle_Update = "wicare.vehicle.update";//修改车辆
    public static final String Method_Vehicle_List = "wicare.vehicles.list";//获取车辆列表
    public static final String Method_Vehicle_Get = "wicare.vehicle.get";//获取车辆信息

    public static final String Method_Device_List = "wicare.devices.list";//获取设备列表
    public static final String Method_Device_Update = "wicare.device.update";//更新设备信息
    public static final String Method_Device_Get = "wicare.device.get";//获取单个设备信息

    public static final String Method_Command = "wicare.command.create"; //发送控制请求命令
    public static final String Method_Notification = "wicare.notifications.list"; //获取历史操作记录
    public static final String Method_Gps_Lists = "wicare.gps_datas.list"; //获取历史gps数据

    public static final String CMD_UNLOCKDOOR = "16417";//开锁
    public static final String CMD_LOCKDOOR = "16416";//关锁
    public static final String CMD_STARTENGINE = "16406";//启动
    public static final String CMD_STOPENGINE = "16421";//熄火
    public static final String CMD_FINDVEHICLE = "16420"; //寻车
    public static final String COMMAND_AUTOLOCKON = "16418"; //落锁开
    public static final String COMMAND_AUTOLOCKOFF = "16419"; //落锁关
    public static final String COMMAND_ACCOFF_INTERVAL_10 = "16456";// 预热
    public static final String COMMAND_ACCOFF_INTERVAL_20 = "16457";
    public static final String COMMAND_ACCOFF_INTERVAL_30 = "16458";
    public static final String CMD_SILENT = "16408";// 静音模式
    public static final String CMD_SOUND = "16409";// 声光模式
    public static final String CMD_SET_MILEAGE = "16404";//更改里程
    public static final String CMD_TRANSFER = "16461";//指令透传，用于车型标定

    @GET("router/rest")
    public Observable<LoginInfo> getLoginInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<CreateVehicleInfo> getCreateVehicleInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<UsertExist> getUsertExist(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<StatusCode> getStatusCode(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<ValidCheck> getValidCheck(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<RegisterInfo> getRegisterInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<AccessToken> getAccessToken(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<DeviceInfo> getDeviceInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Call<DeviceInfo> getDeviceInfoTask(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<WiVehicleList> getVehicleList(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<WiUserInfo> getUserInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<WiHistoryInfo> getHistoryInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<WiVehicle> getVehicleInfo(@QueryMap Map<String, String> options);

    @GET("router/rest")
    public Observable<WiGpsDatas> getWiGpsDatas(@QueryMap Map<String, String> options);
}
