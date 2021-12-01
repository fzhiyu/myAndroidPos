/**
  * Copyright 2021 bejson.com 
  */
package com.cuse.myandroidpos.Post.RefundAllJson;
import java.util.List;

/**
 * Auto-generated: 2021-11-29 16:49:35
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
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