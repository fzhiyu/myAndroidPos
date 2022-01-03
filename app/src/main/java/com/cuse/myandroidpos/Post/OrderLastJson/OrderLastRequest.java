package com.cuse.myandroidpos.Post.OrderLastJson;

public class OrderLastRequest {

    private String stationId;
    private String timestamp;
    private String signature;

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
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
        return "OrderLastRequest{" +
                "stationId='" + stationId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
