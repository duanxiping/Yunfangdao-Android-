package com.vrd.tech.yfd.entity;

/**
 * Created by tsang on 16/7/5.
 */
public class WiVehicle {
    /**
     * obj_name : 粤123456
     * cust_id : 1513
     * obj_id : 3244
     * create_time : 2016-07-01T04:47:43.226Z
     * device_id : 1716
     */

    private String obj_name;
    private String cust_id;
    private String obj_id;
    private String create_time;
    private String device_id;
    private String insurance_date;
    private String annual_inspect_date;
    private String maintain_next_mileage;

    public String getInsurance_date() {
        return insurance_date;
    }

    public void setInsurance_date(String insurance_date) {
        this.insurance_date = insurance_date;
    }

    public String getAnnual_inspect_date() {
        return annual_inspect_date;
    }

    public void setAnnual_inspect_date(String annual_inspect_date) {
        this.annual_inspect_date = annual_inspect_date;
    }

    public String getMaintain_next_mileage() {
        return maintain_next_mileage;
    }

    public void setMaintain_next_mileage(String maintain_next_mileage) {
        this.maintain_next_mileage = maintain_next_mileage;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
