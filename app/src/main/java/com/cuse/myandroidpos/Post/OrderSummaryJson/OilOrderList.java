
package com.cuse.myandroidpos.Post.OrderSummaryJson;


public class OilOrderList {

    private String oilId;
    private String oilName;
    private int oilCount;
    private float oilMoney;
    public void setOilId(String oilId) {
         this.oilId = oilId;
     }
     public String getOilId() {
         return oilId;
     }

    public void setOilName(String oilName) {
         this.oilName = oilName;
     }
     public String getOilName() {
         return oilName;
     }

    public void setOilCount(int oilCount) {
         this.oilCount = oilCount;
     }
     public int getOilCount() {
         return oilCount;
     }

    public String getStringOilCount() {
        return String.valueOf(oilCount);
    }

    public void setOilMoney(float oilMoney) {
        this.oilMoney = oilMoney;
    }
    public float getOilMoney() {
         return oilMoney;
    }

    public String getStringOilMoney() {
        return String.valueOf(oilMoney);
    }

}