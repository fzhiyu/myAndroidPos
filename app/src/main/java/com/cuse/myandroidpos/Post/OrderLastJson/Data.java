
package com.cuse.myandroidpos.Post.OrderLastJson;
import java.util.List;


public class Data {

    private String stationName;
    private float todayMoney;
    private int todayCount;
    private List<OilOrderList> oilOrderList;
    public void setStationName(String stationName) {
         this.stationName = stationName;
     }
     public String getStationName() {
         return stationName;
     }

    public void setTodayMoney(float todayMoney) {
         this.todayMoney = todayMoney;
     }
     public float getTodayMoney() {
         return todayMoney;
     }

    public void setTodayCount(int todayCount) {
         this.todayCount = todayCount;
     }
     public int getTodayCount() {
         return todayCount;
     }

    public void setOilOrderList(List<OilOrderList> oilOrderList) {
         this.oilOrderList = oilOrderList;
     }
     public List<OilOrderList> getOilOrderList() {
         return oilOrderList;
     }

}