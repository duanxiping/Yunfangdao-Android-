package com.vrd.tech.yfd.entity;

/**
 * Created by tsang on 16/7/1.
 */
public class AccessToken {

    /**
     * status_code : 0
     * access_token : 8a756552ecf7b2060b9e8a4880e49c8356e6e815ae98f9aa2a7f9cb3b9cba5930aaff041588fd65ca13782dfb6a13311
     * valid_time : 2016-07-02T07:02:10.708Z
     * user_id : 1513
     */

    private int status_code;
    private String access_token;
    private String valid_time;
    private String user_id;

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "status_code=" + status_code +
                ", access_token='" + access_token + '\'' +
                ", valid_time='" + valid_time + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
