package com.cuse.myandroidpos.Post.LoginJson;

public class LoginRequest {


    private String stationId;
    private String passWord;
    private String timestamp;
    private String signature;

    public LoginRequest(){

    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "stationId='" + stationId + '\'' +
                ", passWord='" + passWord + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
