package com.vrd.tech.yfd.entity;

import java.util.List;

/**
 * Created by tsang on 16/7/3.
 */
public class WiVehicleList {

    /**
     * total : 1
     * data : [{"cust_name":"zeng","mobile":"18666293309","obj_name":"粤123456","cust_id":1513,"obj_id":3244,"device_id":1716}]
     */

    private int total;
    /**
     * cust_name : zeng
     * mobile : 18666293309
     * obj_name : 粤123456
     * cust_id : 1513
     * obj_id : 3244
     * device_id : 1716
     */

    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String cust_name;
        private String mobile;
        private String obj_name;
        private String cust_id;
        private String obj_id;
        private String device_id;

        public String getCust_name() {
            return cust_name;
        }

        public void setCust_name(String cust_name) {
            this.cust_name = cust_name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getObj_name() {
            return obj_name;
        }

        public void setObj_name(String obj_name) {
            this.obj_name = obj_name;
        }

        public String getCust_id() {
            return cust_id;
        }

        public void setCust_id(String cust_id) {
            this.cust_id = cust_id;
        }

        public String getObj_id() {
            return obj_id;
        }

        public void setObj_id(String obj_id) {
            this.obj_id = obj_id;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }
    }
}
