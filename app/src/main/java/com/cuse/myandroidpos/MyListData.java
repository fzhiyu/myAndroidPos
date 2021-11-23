package com.cuse.myandroidpos;

import java.util.ArrayList;

public class MyListData {
    private String oilOrderId ;
    private String oilOrderTime;
    private String user;
    private Integer oil;
    private Float money;


    public MyListData(String oilOrderId, String oilOrderTime, String user, Integer oil, Float money) {
        this.oilOrderId = oilOrderId;
        this.oilOrderTime = oilOrderTime;
        this.user = user;
        this.oil = oil;
        this.money = money;
    }

    public String getOilOrderId() {
        return oilOrderId;
    }

    public void setOilOrderId(String oilOrderId) {
        this.oilOrderId = oilOrderId;
    }

    public String getOilOrderTime() {
        return oilOrderTime;
    }

    public void setOilOrderTime(String oilOrderTime) {
        this.oilOrderTime = oilOrderTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOil() {
        return oil.toString();
    }

    public void setOil(Integer oil) {
        this.oil = oil;
    }

    public String getMoney() {
        return String.valueOf(money);
    }

    public Float getFloatMoney() {
        return  money;
    }

    public void setMoney(Float money) {
        this.money = money;
    }
}


