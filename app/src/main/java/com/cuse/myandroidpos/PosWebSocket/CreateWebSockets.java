package com.cuse.myandroidpos.PosWebSocket;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import tech.gusavila92.websocketclient.WebSocketClient;

public class CreateWebSockets {
    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://paas.u-coupon.cn/wss");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        WebSocketClient webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
            }

            @Override
            public void onTextReceived(String message) {
                Log.i("received", "" + message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }
            @Override
            public void onPingReceived(byte[] data) {
//                Log.i("Ping", "" + Arrays.toString(data));
            }
            @Override
            public void onPongReceived(byte[] data) {
            }
            @Override
            public void onException(Exception e) {
//                System.out.println(e.getMessage());
            }
            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
//                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
    }
}
