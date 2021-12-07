
package com.cuse.myandroidpos.Post.RefundAllJson;

import java.io.Serializable;


public class OilOrder implements Serializable {

    private String refundId;
    private String refundRequestTime;
    private String refundStatus;
    private String refundReason;
    private String refundTime;
    private String oilOrderId;
    private String oilOrderTime;
    private String oilName;
    private String user;
    private int money;
    private int discount;
    private int coupon;
    private int balance;
    private int cash;

    public void setRefundId(String refundId) { this.refundId = refundId; }
    public String getRefundId() { return refundId;}

    public void setRefundRequestTime(String refundRequestTime) { this.refundRequestTime = refundRequestTime; }
    public String getRefundRequestTime() { return refundRequestTime; }

    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }
    public String getRefundStatus() { return refundStatus; }

    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    public String getRefundReason() { return refundReason; }

    public void setRefundTime(String refundTime) { this.refundTime = refundTime; }
    public String getRefundTime() { return refundTime; }

    public void setOilOrderId(String oilOrderId) {
         this.oilOrderId = oilOrderId;
     }
     public String getOilOrderId() {
         return oilOrderId;
     }

    public void setOilOrderTime(String oilOrderTime) {
         this.oilOrderTime = oilOrderTime;
     }
     public String getOilOrderTime() {
         return oilOrderTime;
     }

    public void setOilName(String oilName) {
         this.oilName = oilName;
     }
     public String getOilName() {
         return oilName;
     }

    public void setUser(String user) {
         this.user = user;
     }
     public String getUser() {
         return user;
     }

    public void setMoney(int money) {
         this.money = money;
     }
     public int getMoney() {
         return money;
     }

    public void setDiscount(int discount) {
         this.discount = discount;
     }
     public int getDiscount() {
         return discount;
     }

    public void setCoupon(int coupon) {
         this.coupon = coupon;
     }
     public int getCoupon() {
         return coupon;
     }

    public void setBalance(int balance) {
         this.balance = balance;
     }
     public int getBalance() {
         return balance;
     }

    public void setCash(int cash) {
         this.cash = cash;
     }
     public int getCash() {
         return cash;
     }

}