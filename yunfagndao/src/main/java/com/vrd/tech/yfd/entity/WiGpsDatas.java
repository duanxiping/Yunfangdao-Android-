package com.vrd.tech.yfd.entity;

import java.util.List;

/**
 * Created by tsang on 16/7/7.
 */
public class WiGpsDatas {

    /**
     * total : 106
     * data : [{"_id":"577afc1df2f14d334201fe40","lon":121.3684,"lat":31.25134,"direct":335,"rcv_time":"2016-07-05T00:15:25.824Z"},{"_id":"577b00fbf2f14d3342020b2b","lon":121.3684,"lat":31.25134,"direct":335,"rcv_time":"2016-07-05T00:36:11.323Z"},{"_id":"577b0172f2f14d3342020c84","lon":121.36834,"lat":31.25008,"direct":217,"rcv_time":"2016-07-05T00:38:10.304Z"}]
     */

    private int total;
    /**
     * _id : 577afc1df2f14d334201fe40
     * lon : 121.3684
     * lat : 31.25134
     * direct : 335
     * rcv_time : 2016-07-05T00:15:25.824Z
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
        private String _id;
        private double lon;
        private double lat;
        private int direct;
        private String rcv_time;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public int getDirect() {
            return direct;
        }

        public void setDirect(int direct) {
            this.direct = direct;
        }

        public String getRcv_time() {
            return rcv_time;
        }

        public void setRcv_time(String rcv_time) {
            this.rcv_time = rcv_time;
        }
    }
}
