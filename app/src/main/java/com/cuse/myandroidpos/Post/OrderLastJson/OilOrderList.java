
package com.cuse.myandroidpos.Post.OrderLastJson;

import android.util.Log;

import com.cuse.myandroidpos.Tools;

import java.io.Serializable;


public class OilOrderList implements Serializable, Comparable<OilOrderList> {

    private String oilOrderId;
    private String oilOrderTime;
    private String oilName;
    private String user;
    private float money;
    private float discount;
    private float coupon;
    private float balance;
    private float cash;

    public OilOrderList(){ }

    public OilOrderList(String oilOrderTime) {
        this.oilOrderId = "";
        this.oilOrderTime = oilOrderTime;
        this.oilName = "";
        this.user = "";
        this.money = 0;
        this.discount = 0;
        this.coupon = 0;
        this.balance = 0;
        this.cash = 0;
    }


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

    @Override
    public int compareTo(OilOrderList oilOrderList) {
        String thisOilOrderTime = Tools.NoT(this.getOilOrderTime());
        String putOilOrderTime = Tools.NoT(oilOrderList.getOilOrderTime());

        long thisOilOrderTimeToStamp = Tools.TimeToStamp(new StringBuffer(thisOilOrderTime));
        long putOilOrderTimeToStamp = Tools.TimeToStamp(new StringBuffer(putOilOrderTime));
        Log.i("hejun", "compareTo: " + "thisOilOrderTime " + thisOilOrderTime);
        Log.i("hejun", "compareTo: " + "thisOilOrderTimeToStamp " + thisOilOrderTimeToStamp);
        Log.i("hejun", "compareTo: " + "put " + putOilOrderTime);
        Log.i("hejun", "compareTo: " + "putOilOrderTimeToStamp " + putOilOrderTimeToStamp);
        if ((putOilOrderTimeToStamp > thisOilOrderTimeToStamp) )
            return 1;
        else if ((putOilOrderTimeToStamp == thisOilOrderTimeToStamp) && !(this.user).equals(oilOrderList.getUser()))
            return 0;
        else
            return -1;


    }
}