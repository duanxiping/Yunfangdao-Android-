package com.vrd.tech.yfd.entity;

import java.io.Serializable;

/**
 * Created by tsang on 16/3/8.
 */
public class LoginInfo implements Serializable {

    /**
     * status_code : 0
     * cust_id : 1219
     * cust_name : Dancan
     * access_token : f1b3afaf9bbedfcb0ca3f0465a1d2e7e157c1ea55ad8d2dbcaa7083d125d360c20e75f99257980957f3220a23c5df2b6
     * valid_time : 2016-07-02T03:04:00.200Z
     */

    private int status_code;
    private String cust_id;
    private String cust_name;
    private String access_token;
    private String valid_time;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getCust_name() {
        return cust_name;
    }

    public void setCust_name(String cust_name) {
        this.cust_name = cust_name;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getValid_time() {
        return valid_time;
    }

    public void setValid_time(String valid_time) {
        this.valid_time = valid_time;
    }
}
