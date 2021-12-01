/**
  * Copyright 2021 bejson.com 
  */
package com.cuse.myandroidpos.Post.OrderSummaryJson;
import java.util.List;

/**
 * Auto-generated: 2021-11-29 16:42:57
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Data {

    private float todayMoney;
    private int todayCount;
    private int size;
    private List<OilOrderList> oilOrderList;
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

    public void setSize(int size) {
         this.size = size;
     }
     public int getSize() {
         return size;
     }

    public void setOilOrderList(List<OilOrderList> oilOrderList) {
         this.oilOrderList = oilOrderList;
     }
     public List<OilOrderList> getOilOrderList() {
         return oilOrderList;
     }

}