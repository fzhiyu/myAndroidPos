
package com.cuse.myandroidpos.Post.OrderAllJson;

import com.cuse.myandroidpos.Tools;

public class OilOrder {

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

        return Tools.NoT(this.oilOrderTime);
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
        //隐藏手机号的中间四位
        if (user != null || !user.equals("")){
            StringBuffer sb = new StringBuffer(this.user);
            sb.replace(3,7,"****");
            return sb.toString();
        }else
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