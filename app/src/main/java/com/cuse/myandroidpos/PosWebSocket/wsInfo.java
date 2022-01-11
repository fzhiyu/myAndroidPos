package com.cuse.myandroidpos.PosWebSocket;

public class wsInfo {
    private String sta;
    private String type;

    public wsInfo() {
    }

    public wsInfo(String sta, String type) {
        this.sta = sta;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSta() {
        return sta;
    }

    public void setSta(String sta) {
        this.sta = sta;
    }
}
