
package com.cuse.myandroidpos.Post.OrderAllJson;


public class OrderAllJson {

    private int code;
    private String message;
    private Data data;
    public void setCode(int code) {
         this.code = code;
     }
     public int getCode() {
         return code;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setData(Data data) {
         this.data = data;
     }
     public Data getData() {
         return data;
     }

}