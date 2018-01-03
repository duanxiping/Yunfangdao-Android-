package com.vrd.tech.yfd.entity;

/**
 * Created by tsang on 16/3/10.
 */
public class StatusCode {

    /**
     * status_code : 36870
     * err_msg : invalid app key
     */

    private int status_code;
    private String err_msg;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }
}
