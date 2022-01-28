
package com.cuse.myandroidpos.Post.OrderSummaryJson;
import java.util.List;


public class Data {

    private float totalMoney;
    private int totalCount;
    private int size;
    private List<OilOrderList> oilOrderList;
    public void setTotalMoney(float totalMoney) {
         this.totalMoney = totalMoney;
     }
     public float getTotalMoney() {
         return totalMoney;
     }

    public void setTotalCount(int totalCount) {
         this.totalCount = totalCount;
     }
     public int getTotalCount() {
         return totalCount;
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