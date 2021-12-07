/**
  * Copyright 2021 bejson.com 
  */
package com.cuse.myandroidpos.Post.OrderLastJson;

import java.io.Serializable;

/**
 * Auto-generated: 2021-11-29 16:26:31
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OilOrderList implements Serializable {

    private String oilOrderId;
    private String oilOrderTime;
    private String oilName;
    private String user;
    private float money;
    private float discount;
    private float coupon;
    private float balance;
    private float cash;
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

    public void setMoney(float money) {
         this.money = money;
     }
     public float getMoney() {
         return money;
     }

    public void setDiscount(float discount) {
         this.discount = discount;
     }
     public float getDiscount() {
         return discount;
     }

    public void setCoupon(float coupon) {
         this.coupon = coupon;
     }
     public float getCoupon() {
         return coupon;
     }

    public void setBalance(float balance) {
         this.balance = balance;
     }
     public float getBalance() {
         return balance;
     }

    public void setCash(float cash) {
         this.cash = cash;
     }
     public float getCash() {
         return cash;
     }

}