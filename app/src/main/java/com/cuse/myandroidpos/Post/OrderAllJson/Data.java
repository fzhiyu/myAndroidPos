
package com.cuse.myandroidpos.Post.OrderAllJson;
import com.cuse.myandroidpos.Post.OrderLastJson.OilOrderList;

import java.util.List;

public class Data {

    private int totalCount;
    private int current;
    private List<OilOrderList> oilOrder;
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

    public void setOilOrder(List<OilOrderList> oilOrder) {
         this.oilOrder = oilOrder;
     }
     public List<OilOrderList> getOilOrder() {
         return oilOrder;
     }
}