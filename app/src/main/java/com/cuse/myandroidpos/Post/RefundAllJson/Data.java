
package com.cuse.myandroidpos.Post.RefundAllJson;
import java.util.List;


public class Data {

    private int totalCount;
    private int current;
    private List<OilOrder> oilOrder;
    public void setTotalCount(int totalCount) {
         this.totalCount = totalCount;
     }
     public int getTotalCount() {
         return totalCount;
     }

    public void setCurrent(int current) {
         this.current = current;
     }
     public int getCurrent() {
         return current;
     }

    public void setOilOrder(List<OilOrder> oilOrder) {
         this.oilOrder = oilOrder;
     }
     public List<OilOrder> getOilOrder() {
         return oilOrder;
     }
}