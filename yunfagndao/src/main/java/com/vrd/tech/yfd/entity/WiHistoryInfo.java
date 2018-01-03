package com.vrd.tech.yfd.entity;

import java.util.List;

/**
 * Created by tsang on 16/7/5.
 */
public class WiHistoryInfo {


    /**
     * total : 106
     * data : [{"rcv_time":"2016-07-05T05:04:47.485Z","content":"原车钥匙解锁成功！","cust_id":1513,"obj_id":3244,"noti_id":1437458},{"rcv_time":"2016-07-05T05:04:47.426Z","content":"原车钥匙解锁成功！","cust_id":1513,"obj_id":3244,"noti_id":1437457}]
     */

    private int total;
    /**
     * rcv_time : 2016-07-05T05:04:47.485Z
     * content : 原车钥匙解锁成功！
     * cust_id : 1513
     * obj_id : 3244
     * noti_id : 1437458
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
        private String rcv_time;
        private String content;
        private String cust_id;
        private String obj_id;
        private String noti_id;

        public String getRcv_time() {
            return rcv_time;
        }

        public void setRcv_time(String rcv_time) {
            this.rcv_time = rcv_time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public String getNoti_id() {
            return noti_id;
        }

        public void setNoti_id(String noti_id) {
            this.noti_id = noti_id;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "rcv_time='" + rcv_time + '\'' +
                    ", content='" + content + '\'' +
                    ", cust_id='" + cust_id + '\'' +
                    ", obj_id='" + obj_id + '\'' +
                    ", noti_id='" + noti_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WiHistoryInfo{" +
                "total=" + total +
                ", data=" + data +
                '}';
    }
}
