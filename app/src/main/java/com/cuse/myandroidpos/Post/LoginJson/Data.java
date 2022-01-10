
package com.cuse.myandroidpos.Post.LoginJson;

public class Data {

    private Integer result;
    private String message;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Integer getResult() {
        return result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}