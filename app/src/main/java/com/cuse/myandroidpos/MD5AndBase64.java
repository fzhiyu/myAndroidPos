package com.cuse.myandroidpos;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5AndBase64 {

    public static String md5(String data) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] md5 = md.digest(data.getBytes(StandardCharsets.UTF_8));

            // 将字节数据转换为十六进制
            for (byte b : md5) {
                sb.append(Integer.toHexString(b & 0xff));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String sMD5 = sb.toString();

        //String sBase64 = Base64.encodeToString(sMD5.getBytes(StandardCharsets.UTF_8),Base64.DEFAULT);

//        String sBase64 = Base64.encodeToString(sMD5.getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP);
        return sMD5;
    }
}
