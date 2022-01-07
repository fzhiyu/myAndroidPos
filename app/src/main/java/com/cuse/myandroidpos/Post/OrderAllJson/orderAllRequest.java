package com.cuse.myandroidpos.Post.OrderAllJson;

public class orderAllRequest {
    private String token;
    private String startTime;
    private String endTime;
    private String start;
    private String count;
    private String timestamp;
    private String signature;

    public orderAllRequest() {
    }

    public orderAllRequest(String token, String startTime, String endTime, String start, String count, String timestamp, String signature) {
        this.token = token;
        this.startTime = startTime;
        this.endTime = endTime;
        this.start = start;
        this.count = count;
        this.timestamp = timestamp;
        this.signature = signature;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
