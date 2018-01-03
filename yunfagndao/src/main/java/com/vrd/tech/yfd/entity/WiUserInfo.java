package com.vrd.tech.yfd.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.vrd.tech.yfd.BR;

/**
 * Created by tsang on 16/7/5.
 */
public class WiUserInfo extends BaseObservable {

    /**
     * mobile : 18666293309
     * password : e10adc3949ba59abbe56e057f20f883e
     * cust_id : 1513
     * create_time : 2016-07-01T03:30:29.647Z
     * update_time : 2016-07-05T03:09:51.941Z
     * cust_name : 曾bbbb
     * annual_inspect_date : 2016-11-11T00:00:00.000Z
     */

    private String mobile;
    private String password;
    private String cust_id;
    private String create_time;
    private String update_time;
    private String cust_name;
    private String annual_inspect_date;
    private boolean annual_inspect_alert;

    @Bindable
    public boolean isAnnual_inspect_alert() {
        return annual_inspect_alert;
    }

    public void setAnnual_inspect_alert(boolean annual_inspect_alert) {
        this.annual_inspect_alert = annual_inspect_alert;
        notifyPropertyChanged(BR.annual_inspect_alert);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    @Bindable
    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
        notifyPropertyChanged(BR.cust_name);
    }

    @Bindable
    public String getAnnual_inspect_date() {
        return annual_inspect_date;
    }

    public void setAnnual_inspect_date(String annual_inspect_date) {
        this.annual_inspect_date = annual_inspect_date;
        notifyPropertyChanged(BR.annual_inspect_date);
    }
}
