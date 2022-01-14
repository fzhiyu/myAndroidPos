package com.cuse.myandroidpos.PosWebSocket;

import static android.content.ContentValues.TAG;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.cuse.myandroidpos.activity.MainActivity;

import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClientService extends Service {
    private URI uri;
    public WebSocketClient client;
    public WebSocketClientBinder mBinder = new WebSocketClientBinder();

    class WebSocketClientBinder extends Binder {
        public WebSocketClientService getService() {
            return WebSocketClientService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private wsInfo wsInfo_login;
    private wsInfo wsInfo_logout;
    private wsInfo wsInfo_heart;
    private wsInfo wsInfo_newOrder;
    private wsInfo wsInfo_pushNew;
    private wsInfo wsInfo_checkOnline;
    private wsInfo wsInfo_isOnlineNo;
    private String json_login;
    private String json_logout;
    private String json_heart;
    private String json_newOrder;
    private String json_pushNew;
    private String json_checkOnline;
    private String json_isOnlineNo;
    private String token;


    //初始发送数据
    private void initData() {
        token = ((MainActivity) getApplicationContext()).getToken();
        wsInfo_login = new wsInfo(token, "login");
        json_login = JSON.toJSONString(wsInfo_login);

        wsInfo_checkOnline = new wsInfo("", "check_online");
        json_checkOnline = JSON.toJSONString(wsInfo_checkOnline);

        wsInfo_heart = new wsInfo(token, "heartbeat");
        json_heart = JSON.toJSONString(wsInfo_heart);

        wsInfo_newOrder = new wsInfo(token, "new_order");
        json_newOrder = JSON.toJSONString(wsInfo_newOrder);
    }

    private void example() {
        initData();
        URI uri;
        try {
            uri = new URI("ws://paas.u-coupon.cn/wss");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        com.cuse.myandroidpos.PosWebSocket.WebSocketClient client =
                new com.cuse.myandroidpos.PosWebSocket.WebSocketClient(uri) {
                    @Override
                    public void onMessage(String message) {
                        super.onMessage(message);
                        Log.e(TAG, "连接: " + message);
                    }
                };
        try {
            client.connectBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (client != null && client.isOpen()) {
            client.send(json_login);
            client.send(json_checkOnline);
        }
    }
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
    }
}
