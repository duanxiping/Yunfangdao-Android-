package com.vrd.tech.yfd.entity;

import java.util.List;

/**
 * Created by tsang on 16/5/3.
 */
public class AlertInfo {

    /**
     * total : 1
     * page_total : 1
     * data : [{"rev_lat":22.67147827396723,"rev_lon":113.8016800974404,"_id":"5662f32fb9155ce40c001791","speed":"0","max_speed":"null","lat":22.67437,"lon":113.79659,"alert_time":"2015-12-05T14:22:39.847Z","alert_type":"12293","obj_id":"9579","alert_id":20}]
     */

    private int total;
    private int page_total;
    /**
     * rev_lat : 22.67147827396723
     * rev_lon : 113.8016800974404
     * _id : 5662f32fb9155ce40c001791
     * speed : 0
     * max_speed : null
     * lat : 22.67437
     * lon : 113.79659
     * alert_time : 2015-12-05T14:22:39.847Z
     * alert_type : 12293
     * obj_id : 9579
     * alert_id : 20
     */

    private List<DataBean> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage_total() {
        return page_total;
    }

    public void setPage_total(int page_total) {
        this.page_total = page_total;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private double rev_lat;
        private double rev_lon;
        private String _id;
        private String speed;
        private String max_speed;
        private double lat;
        private double lon;
        private String alert_time;
        private String alert_type;
        private String obj_id;
        private int alert_id;

        public double getRev_lat() {
            return rev_lat;
        }

        public void setRev_lat(double rev_lat) {
            this.rev_lat = rev_lat;
        }

        public double getRev_lon() {
            return rev_lon;
        }

        public void setRev_lon(double rev_lon) {
            this.rev_lon = rev_lon;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getMax_speed() {
            return max_speed;
        }

        public void setMax_speed(String max_speed) {
            this.max_speed = max_speed;
        }

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

        public String getAlert_time() {
            return alert_time;
        }

        public void setAlert_time(String alert_time) {
            this.alert_time = alert_time;
        }

        public String getAlert_type() {
            return alert_type;
        }

        public void setAlert_type(String alert_type) {
            this.alert_type = alert_type;
        }

        public String getObj_id() {
            return obj_id;
        }

        public void setObj_id(String obj_id) {
            this.obj_id = obj_id;
        }

        public int getAlert_id() {
            return alert_id;
        }

        public void setAlert_id(int alert_id) {
            this.alert_id = alert_id;
        }
    }
}
