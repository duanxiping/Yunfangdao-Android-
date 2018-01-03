package com.vrd.tech.yfd.entity;

import java.util.List;

/**
 * Created by tsang on 16/7/1.终端信息
 */
public class DeviceInfo {


    /**
     * sim : 00000000000
     * serial : 699501000005028
     * device_id : 1716
     * cust_id : 111
     * p20c_status : {"P20C_ENGINE":false,"P20C_BREAK":false,"P20C_RIGHT_BACK_DOOR":false,"P20C_LEFT_BACK_DOOR":false,"P20C_RIGHT_FRONT_DOOR":false,"P20C_LEFT_FRONT_DOOR":false,"P20C_ACC":false,"P20C_FORTIFY":false,"P20C_ALARM":false,"P20C_HAND_BREAK":false,"P20C_ENGINE_HOOD":false,"P20C_TRUNK":false,"P20C_DOOR_LOCK":false,"P20C_LITTLE_LAMP":false,"P20C_ALERT_OPEN":false,"P20C_ALERT_VIRBRATE":false,"P20C_ALERT_LOOK":false,"P20C_LEFT_FRONT_WIN":false,"P20C_RIGHT_FRONT_WIN":false,"P20C_LEFT_BACK_WIN":false,"P20C_RIGHT_BACK_WIN":false,"P20C_LEFT_FRONT_TYRE":false,"P20C_RIGHT_FRONT_TYRE":false,"P20C_LEFT_BACK_TYRE":false,"P20C_RIGHT_BACK_TYRE":false}
     * last_data_time : 2016-07-04T03:25:58.641Z
     * active_gps_data : {"uni_alerts":[],"uni_status":[8197,8209],"_lat":0,"_lon":0,"battery":0,"signal":20,"rcv_time":"2016-07-04T03:26:09.483Z","fuel":0,"mileage":0.9}
     * active_obd_data : {"update_time":"2016-07-04T03:21:43.211Z","gz":0,"gy":0,"gx":0,"jqll":0,"jsjqll":0,"dhtqj":535,"cqryxz":3.77,"fdjfz":20,"ryyl":0,"jqwd":11,"jqyl":51,"dqyl":0,"hjwd":0,"syyl":0,"chqwd":0,"sw":130,"rfdjzs":200,"fdjzs":0,"ss":0,"jqmkd":20,"dpdy":11.834}
     */

    private boolean is_online;
    private String sim;
    private String serial;
    private String device_id;
    private String cust_id;
    private int status_code;
    /**
     * P20C_ENGINE : false
     * P20C_BREAK : false
     * P20C_RIGHT_BACK_DOOR : false
     * P20C_LEFT_BACK_DOOR : false
     * P20C_RIGHT_FRONT_DOOR : false
     * P20C_LEFT_FRONT_DOOR : false
     * P20C_ACC : false
     * P20C_FORTIFY : false
     * P20C_ALARM : false
     * P20C_HAND_BREAK : false
     * P20C_ENGINE_HOOD : false
     * P20C_TRUNK : false
     * P20C_DOOR_LOCK : false
     * P20C_LITTLE_LAMP : false
     * P20C_ALERT_OPEN : false
     * P20C_ALERT_VIRBRATE : false
     * P20C_ALERT_LOOK : false
     * P20C_LEFT_FRONT_WIN : false
     * P20C_RIGHT_FRONT_WIN : false
     * P20C_LEFT_BACK_WIN : false
     * P20C_RIGHT_BACK_WIN : false
     * P20C_LEFT_FRONT_TYRE : false
     * P20C_RIGHT_FRONT_TYRE : false
     * P20C_LEFT_BACK_TYRE : false
     * P20C_RIGHT_BACK_TYRE : false
     */

    private P20cStatusBean p20c_status;
    private String last_data_time;
    /**
     * uni_alerts : []
     * uni_status : [8197,8209]
     * _lat : 0.0
     * _lon : 0.0
     * battery : 0
     * signal : 20
     * rcv_time : 2016-07-04T03:26:09.483Z
     * fuel : 0
     * mileage : 0.9
     */

    private ActiveGpsDataBean active_gps_data;
    /**
     * update_time : 2016-07-04T03:21:43.211Z
     * gz : 0
     * gy : 0
     * gx : 0
     * jqll : 0
     * jsjqll : 0
     * dhtqj : 535
     * cqryxz : 3.77
     * fdjfz : 20
     * ryyl : 0
     * jqwd : 11
     * jqyl : 51
     * dqyl : 0
     * hjwd : 0
     * syyl : 0
     * chqwd : 0
     * sw : 130
     * rfdjzs : 200
     * fdjzs : 0
     * ss : 0
     * jqmkd : 20
     * dpdy : 11.834
     */

    private ActiveObdDataBean active_obd_data;
    /**
     * accoff_interval : 10
     * is_start : 0
     * is_autolockdoor : true
     * is_sound : false
     * is_lockdoor : true
     * gps_interval : 30
     * version : W12_EX_60A_VER_1.0.71+OBD_VER_1.0.74
     */

    private ParamsBean params;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public boolean is_online() {
        return is_online;
    }

    public void setIs_online(boolean is_online) {
        this.is_online = is_online;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDevice_id() {

        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public P20cStatusBean getP20c_status() {
        return p20c_status;
    }

    public void setP20c_status(P20cStatusBean p20c_status) {
        this.p20c_status = p20c_status;
    }

    public String getLast_data_time() {
        return last_data_time;
    }

    public void setLast_data_time(String last_data_time) {
        this.last_data_time = last_data_time;
    }

    public ActiveGpsDataBean getActive_gps_data() {
        return active_gps_data;
    }

    public void setActive_gps_data(ActiveGpsDataBean active_gps_data) {
        this.active_gps_data = active_gps_data;
    }

    public ActiveObdDataBean getActive_obd_data() {
        return active_obd_data;
    }

    public void setActive_obd_data(ActiveObdDataBean active_obd_data) {
        this.active_obd_data = active_obd_data;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class P20cStatusBean {
        private boolean P20C_ENGINE;
        private boolean P20C_BREAK;
        private boolean P20C_RIGHT_BACK_DOOR;
        private boolean P20C_LEFT_BACK_DOOR;
        private boolean P20C_RIGHT_FRONT_DOOR;
        private boolean P20C_LEFT_FRONT_DOOR;
        private boolean P20C_ACC;
        private boolean P20C_FORTIFY;
        private boolean P20C_ALARM;
        private boolean P20C_HAND_BREAK;
        private boolean P20C_ENGINE_HOOD;
        private boolean P20C_TRUNK;
        private boolean P20C_DOOR_LOCK;
        private boolean P20C_LITTLE_LAMP;
        private boolean P20C_ALERT_OPEN;
        private boolean P20C_ALERT_VIRBRATE;
        private boolean P20C_ALERT_LOOK;
        private boolean P20C_LEFT_FRONT_WIN;
        private boolean P20C_RIGHT_FRONT_WIN;
        private boolean P20C_LEFT_BACK_WIN;
        private boolean P20C_RIGHT_BACK_WIN;
        private boolean P20C_LEFT_FRONT_TYRE;
        private boolean P20C_RIGHT_FRONT_TYRE;
        private boolean P20C_LEFT_BACK_TYRE;
        private boolean P20C_RIGHT_BACK_TYRE;

        public boolean isP20C_ENGINE() {
            return P20C_ENGINE;
        }

        public void setP20C_ENGINE(boolean P20C_ENGINE) {
            this.P20C_ENGINE = P20C_ENGINE;
        }

        public boolean isP20C_BREAK() {
            return P20C_BREAK;
        }

        public void setP20C_BREAK(boolean P20C_BREAK) {
            this.P20C_BREAK = P20C_BREAK;
        }

        public boolean isP20C_RIGHT_BACK_DOOR() {
            return P20C_RIGHT_BACK_DOOR;
        }

        public void setP20C_RIGHT_BACK_DOOR(boolean P20C_RIGHT_BACK_DOOR) {
            this.P20C_RIGHT_BACK_DOOR = P20C_RIGHT_BACK_DOOR;
        }

        public boolean isP20C_LEFT_BACK_DOOR() {
            return P20C_LEFT_BACK_DOOR;
        }

        public void setP20C_LEFT_BACK_DOOR(boolean P20C_LEFT_BACK_DOOR) {
            this.P20C_LEFT_BACK_DOOR = P20C_LEFT_BACK_DOOR;
        }

        public boolean isP20C_RIGHT_FRONT_DOOR() {
            return P20C_RIGHT_FRONT_DOOR;
        }

        public void setP20C_RIGHT_FRONT_DOOR(boolean P20C_RIGHT_FRONT_DOOR) {
            this.P20C_RIGHT_FRONT_DOOR = P20C_RIGHT_FRONT_DOOR;
        }

        public boolean isP20C_LEFT_FRONT_DOOR() {
            return P20C_LEFT_FRONT_DOOR;
        }

        public void setP20C_LEFT_FRONT_DOOR(boolean P20C_LEFT_FRONT_DOOR) {
            this.P20C_LEFT_FRONT_DOOR = P20C_LEFT_FRONT_DOOR;
        }

        public boolean isP20C_ACC() {
            return P20C_ACC;
        }

        public void setP20C_ACC(boolean P20C_ACC) {
            this.P20C_ACC = P20C_ACC;
        }

        public boolean isP20C_FORTIFY() {
            return P20C_FORTIFY;
        }

        public void setP20C_FORTIFY(boolean P20C_FORTIFY) {
            this.P20C_FORTIFY = P20C_FORTIFY;
        }

        public boolean isP20C_ALARM() {
            return P20C_ALARM;
        }

        public void setP20C_ALARM(boolean P20C_ALARM) {
            this.P20C_ALARM = P20C_ALARM;
        }

        public boolean isP20C_HAND_BREAK() {
            return P20C_HAND_BREAK;
        }

        public void setP20C_HAND_BREAK(boolean P20C_HAND_BREAK) {
            this.P20C_HAND_BREAK = P20C_HAND_BREAK;
        }

        public boolean isP20C_ENGINE_HOOD() {
            return P20C_ENGINE_HOOD;
        }

        public void setP20C_ENGINE_HOOD(boolean P20C_ENGINE_HOOD) {
            this.P20C_ENGINE_HOOD = P20C_ENGINE_HOOD;
        }

        public boolean isP20C_TRUNK() {
            return P20C_TRUNK;
        }

        public void setP20C_TRUNK(boolean P20C_TRUNK) {
            this.P20C_TRUNK = P20C_TRUNK;
        }

        public boolean isP20C_DOOR_LOCK() {
            return P20C_DOOR_LOCK;
        }

        public void setP20C_DOOR_LOCK(boolean P20C_DOOR_LOCK) {
            this.P20C_DOOR_LOCK = P20C_DOOR_LOCK;
        }

        public boolean isP20C_LITTLE_LAMP() {
            return P20C_LITTLE_LAMP;
        }

        public void setP20C_LITTLE_LAMP(boolean P20C_LITTLE_LAMP) {
            this.P20C_LITTLE_LAMP = P20C_LITTLE_LAMP;
        }

        public boolean isP20C_ALERT_OPEN() {
            return P20C_ALERT_OPEN;
        }

        public void setP20C_ALERT_OPEN(boolean P20C_ALERT_OPEN) {
            this.P20C_ALERT_OPEN = P20C_ALERT_OPEN;
        }

        public boolean isP20C_ALERT_VIRBRATE() {
            return P20C_ALERT_VIRBRATE;
        }

        public void setP20C_ALERT_VIRBRATE(boolean P20C_ALERT_VIRBRATE) {
            this.P20C_ALERT_VIRBRATE = P20C_ALERT_VIRBRATE;
        }

        public boolean isP20C_ALERT_LOOK() {
            return P20C_ALERT_LOOK;
        }

        public void setP20C_ALERT_LOOK(boolean P20C_ALERT_LOOK) {
            this.P20C_ALERT_LOOK = P20C_ALERT_LOOK;
        }

        public boolean isP20C_LEFT_FRONT_WIN() {
            return P20C_LEFT_FRONT_WIN;
        }

        public void setP20C_LEFT_FRONT_WIN(boolean P20C_LEFT_FRONT_WIN) {
            this.P20C_LEFT_FRONT_WIN = P20C_LEFT_FRONT_WIN;
        }

        public boolean isP20C_RIGHT_FRONT_WIN() {
            return P20C_RIGHT_FRONT_WIN;
        }

        public void setP20C_RIGHT_FRONT_WIN(boolean P20C_RIGHT_FRONT_WIN) {
            this.P20C_RIGHT_FRONT_WIN = P20C_RIGHT_FRONT_WIN;
        }

        public boolean isP20C_LEFT_BACK_WIN() {
            return P20C_LEFT_BACK_WIN;
        }

        public void setP20C_LEFT_BACK_WIN(boolean P20C_LEFT_BACK_WIN) {
            this.P20C_LEFT_BACK_WIN = P20C_LEFT_BACK_WIN;
        }

        public boolean isP20C_RIGHT_BACK_WIN() {
            return P20C_RIGHT_BACK_WIN;
        }

        public void setP20C_RIGHT_BACK_WIN(boolean P20C_RIGHT_BACK_WIN) {
            this.P20C_RIGHT_BACK_WIN = P20C_RIGHT_BACK_WIN;
        }

        public boolean isP20C_LEFT_FRONT_TYRE() {
            return P20C_LEFT_FRONT_TYRE;
        }

        public void setP20C_LEFT_FRONT_TYRE(boolean P20C_LEFT_FRONT_TYRE) {
            this.P20C_LEFT_FRONT_TYRE = P20C_LEFT_FRONT_TYRE;
        }

        public boolean isP20C_RIGHT_FRONT_TYRE() {
            return P20C_RIGHT_FRONT_TYRE;
        }

        public void setP20C_RIGHT_FRONT_TYRE(boolean P20C_RIGHT_FRONT_TYRE) {
            this.P20C_RIGHT_FRONT_TYRE = P20C_RIGHT_FRONT_TYRE;
        }

        public boolean isP20C_LEFT_BACK_TYRE() {
            return P20C_LEFT_BACK_TYRE;
        }

        public void setP20C_LEFT_BACK_TYRE(boolean P20C_LEFT_BACK_TYRE) {
            this.P20C_LEFT_BACK_TYRE = P20C_LEFT_BACK_TYRE;
        }

        public boolean isP20C_RIGHT_BACK_TYRE() {
            return P20C_RIGHT_BACK_TYRE;
        }

        public void setP20C_RIGHT_BACK_TYRE(boolean P20C_RIGHT_BACK_TYRE) {
            this.P20C_RIGHT_BACK_TYRE = P20C_RIGHT_BACK_TYRE;
        }

        @Override
        public String toString() {
            return "P20cStatusBean{" +
                    "P20C_ENGINE=" + P20C_ENGINE +
                    ", P20C_BREAK=" + P20C_BREAK +
                    ", P20C_RIGHT_BACK_DOOR=" + P20C_RIGHT_BACK_DOOR +
                    ", P20C_LEFT_BACK_DOOR=" + P20C_LEFT_BACK_DOOR +
                    ", P20C_RIGHT_FRONT_DOOR=" + P20C_RIGHT_FRONT_DOOR +
                    ", P20C_LEFT_FRONT_DOOR=" + P20C_LEFT_FRONT_DOOR +
                    ", P20C_ACC=" + P20C_ACC +
                    ", P20C_FORTIFY=" + P20C_FORTIFY +
                    ", P20C_ALARM=" + P20C_ALARM +
                    ", P20C_HAND_BREAK=" + P20C_HAND_BREAK +
                    ", P20C_ENGINE_HOOD=" + P20C_ENGINE_HOOD +
                    ", P20C_TRUNK=" + P20C_TRUNK +
                    ", P20C_DOOR_LOCK=" + P20C_DOOR_LOCK +
                    ", P20C_LITTLE_LAMP=" + P20C_LITTLE_LAMP +
                    ", P20C_ALERT_OPEN=" + P20C_ALERT_OPEN +
                    ", P20C_ALERT_VIRBRATE=" + P20C_ALERT_VIRBRATE +
                    ", P20C_ALERT_LOOK=" + P20C_ALERT_LOOK +
                    ", P20C_LEFT_FRONT_WIN=" + P20C_LEFT_FRONT_WIN +
                    ", P20C_RIGHT_FRONT_WIN=" + P20C_RIGHT_FRONT_WIN +
                    ", P20C_LEFT_BACK_WIN=" + P20C_LEFT_BACK_WIN +
                    ", P20C_RIGHT_BACK_WIN=" + P20C_RIGHT_BACK_WIN +
                    ", P20C_LEFT_FRONT_TYRE=" + P20C_LEFT_FRONT_TYRE +
                    ", P20C_RIGHT_FRONT_TYRE=" + P20C_RIGHT_FRONT_TYRE +
                    ", P20C_LEFT_BACK_TYRE=" + P20C_LEFT_BACK_TYRE +
                    ", P20C_RIGHT_BACK_TYRE=" + P20C_RIGHT_BACK_TYRE +
                    '}';
        }
    }

    public static class ActiveGpsDataBean {
        private double _lat;
        private double _lon;
        private double lat;
        private double lon;
        private String battery;
        private String signal;
        private String rcv_time;
        private String gps_time;

        private String fuel;
        private String mileage;
        private List<?> uni_alerts;
        private List<Integer> uni_status;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double get_lat() {
            return _lat;
        }

        public void set_lat(double _lat) {
            this._lat = _lat;
        }

        public double get_lon() {
            return _lon;
        }

        public void set_lon(double _lon) {
            this._lon = _lon;
        }

        public String getBattery() {
            return battery;
        }

        public void setBattery(String battery) {
            this.battery = battery;
        }

        public String getSignal() {
            return signal;
        }

        public void setSignal(String signal) {
            this.signal = signal;
        }

        public String getRcv_time() {
            return rcv_time;
        }

        public void setRcv_time(String rcv_time) {
            this.rcv_time = rcv_time;
        }

        public String getGps_time() {
            return gps_time;
        }

        public void setGps_time(String gps_time) {
            this.gps_time = gps_time;
        }

        public String getFuel() {
            return fuel;
        }

        public void setFuel(String fuel) {
            this.fuel = fuel;
        }

        public String getMileage() {
            return mileage;
        }

        public void setMileage(String mileage) {
            this.mileage = mileage;
        }

        public List<?> getUni_alerts() {
            return uni_alerts;
        }

        public void setUni_alerts(List<?> uni_alerts) {
            this.uni_alerts = uni_alerts;
        }

        public List<Integer> getUni_status() {
            return uni_status;
        }

        public void setUni_status(List<Integer> uni_status) {
            this.uni_status = uni_status;
        }
    }

    public static class ActiveObdDataBean {
        private String update_time;
        private String gz;
        private String gy;
        private String gx;
        private String jqll;
        private String jsjqll;
        private String dhtqj;
        private String cqryxz;
        private String fdjfz;
        private String ryyl;
        private String jqwd;
        private String jqyl;
        private String dqyl;
        private String hjwd;
        private String syyl;
        private String chqwd;
        private String sw;
        private String rfdjzs;
        private String fdjzs;
        private String ss;
        private String jqmkd;
        private String dpdy;

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getGz() {
            return gz;
        }

        public void setGz(String gz) {
            this.gz = gz;
        }

        public String getGy() {
            return gy;
        }

        public void setGy(String gy) {
            this.gy = gy;
        }

        public String getGx() {
            return gx;
        }

        public void setGx(String gx) {
            this.gx = gx;
        }

        public String getJqll() {
            return jqll;
        }

        public void setJqll(String jqll) {
            this.jqll = jqll;
        }

        public String getJsjqll() {
            return jsjqll;
        }

        public void setJsjqll(String jsjqll) {
            this.jsjqll = jsjqll;
        }

        public String getDhtqj() {
            return dhtqj;
        }

        public void setDhtqj(String dhtqj) {
            this.dhtqj = dhtqj;
        }

        public String getCqryxz() {
            return cqryxz;
        }

        public void setCqryxz(String cqryxz) {
            this.cqryxz = cqryxz;
        }

        public String getFdjfz() {
            return fdjfz;
        }

        public void setFdjfz(String fdjfz) {
            this.fdjfz = fdjfz;
        }

        public String getRyyl() {
            return ryyl;
        }

        public void setRyyl(String ryyl) {
            this.ryyl = ryyl;
        }

        public String getJqwd() {
            return jqwd;
        }

        public void setJqwd(String jqwd) {
            this.jqwd = jqwd;
        }

        public String getJqyl() {
            return jqyl;
        }

        public void setJqyl(String jqyl) {
            this.jqyl = jqyl;
        }

        public String getDqyl() {
            return dqyl;
        }

        public void setDqyl(String dqyl) {
            this.dqyl = dqyl;
        }

        public String getHjwd() {
            return hjwd;
        }

        public void setHjwd(String hjwd) {
            this.hjwd = hjwd;
        }

        public String getSyyl() {
            return syyl;
        }

        public void setSyyl(String syyl) {
            this.syyl = syyl;
        }

        public String getChqwd() {
            return chqwd;
        }

        public void setChqwd(String chqwd) {
            this.chqwd = chqwd;
        }

        public String getSw() {
            return sw;
        }

        public void setSw(String sw) {
            this.sw = sw;
        }

        public String getRfdjzs() {
            return rfdjzs;
        }

        public void setRfdjzs(String rfdjzs) {
            this.rfdjzs = rfdjzs;
        }

        public String getFdjzs() {
            return fdjzs;
        }

        public void setFdjzs(String fdjzs) {
            this.fdjzs = fdjzs;
        }

        public String getSs() {
            return ss;
        }

        public void setSs(String ss) {
            this.ss = ss;
        }

        public String getJqmkd() {
            return jqmkd;
        }

        public void setJqmkd(String jqmkd) {
            this.jqmkd = jqmkd;
        }

        public String getDpdy() {
            return dpdy;
        }

        public void setDpdy(String dpdy) {
            this.dpdy = dpdy;
        }
    }


    public static class ParamsBean {
        private int accoff_interval;
        private int is_start;
        private boolean is_autolock;
        private boolean is_sound;
        private boolean is_lockdoor;
        private String gps_interval;
        private String version;
        private String vk_result;

        public String getVk_result() {
            return vk_result;
        }

        public void setVk_result(String vk_result) {
            this.vk_result = vk_result;
        }

        public int getAccoff_interval() {
            return accoff_interval;
        }

        public void setAccoff_interval(int accoff_interval) {
            this.accoff_interval = accoff_interval;
        }

        public int getIs_start() {
            return is_start;
        }

        public void setIs_start(int is_start) {
            this.is_start = is_start;
        }

        public boolean isIs_autolock() {
            return is_autolock;
        }

        public void setIs_autolock(boolean is_autolock) {
            this.is_autolock = is_autolock;
        }

        public boolean isIs_sound() {
            return is_sound;
        }

        public void setIs_sound(boolean is_sound) {
            this.is_sound = is_sound;
        }

        public boolean isIs_lockdoor() {
            return is_lockdoor;
        }

        public void setIs_lockdoor(boolean is_lockdoor) {
            this.is_lockdoor = is_lockdoor;
        }

        public String getGps_interval() {
            return gps_interval;
        }

        public void setGps_interval(String gps_interval) {
            this.gps_interval = gps_interval;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return "ParamsBean{" +
                    "accoff_interval=" + accoff_interval +
                    ", is_start=" + is_start +
                    ", is_autolock=" + is_autolock +
                    ", is_sound=" + is_sound +
                    ", is_lockdoor=" + is_lockdoor +
                    ", gps_interval='" + gps_interval + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "is_online=" + is_online +
                ", sim='" + sim + '\'' +
                ", serial='" + serial + '\'' +
                ", device_id='" + device_id + '\'' +
                ", cust_id='" + cust_id + '\'' +
                ", p20c_status=" + p20c_status.toString() +
                ", last_data_time='" + last_data_time + '\'' +
                ", active_gps_data=" + active_gps_data +
                ", active_obd_data=" + active_obd_data +
                ", params=" + params.toString() +
                '}';
    }
}
