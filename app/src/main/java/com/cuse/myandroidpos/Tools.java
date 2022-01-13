package com.cuse.myandroidpos;



import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Tools {
    public static String NoT(String time){
        return time.replace("T"," ");
    }

    //时间转成unix秒
    public static long TimeToStamp(StringBuffer sb){
        long unixTimestamp = 0l;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = df.parse(sb.toString());
            unixTimestamp = date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTimestamp;
    }

    //unix秒转化为"yyyy-MM-dd HH:mm:ss"格式字符串
    public static String StampToTime(long stamp){
        String s;

        SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        s = df.format(new Date(stamp));

        return s;
    }

    //显示code不为0时候的错误
    public static void codeError(Context context, int code) {
        switch (code) {
            case 1: {
                Toast.makeText(context, "时间戳错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 2: {
                Toast.makeText(context, "签名错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 3: {
                Toast.makeText(context, "参数错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 4: {
                Toast.makeText(context, "油站不存在", Toast.LENGTH_SHORT).show();
            }
            break;
            case 5: {
                Toast.makeText(context, "密码错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 6: {
                Toast.makeText(context, "token错误", Toast.LENGTH_SHORT).show();
            }
            break;
            case 999: {
                Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    public static void hideBottomUIMenu(View v) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY ;
            v.setSystemUiVisibility(uiOptions);
        }

    }

    //调起输入法 用于隐藏输入法后隐藏导航栏
    public static void hideWhenSystemUiVisible(View v){
        v.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==View.SYSTEM_UI_FLAG_VISIBLE){
                hideBottomUIMenu(v);
            }
        });
    }

    public static class md5 {
        public static String md5(String plainText) {
            String re_md5 = new String();
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(plainText.getBytes());
                byte b[] = md.digest();

                int i;

                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");
                    buf.append(Integer.toHexString(i));
                }

                re_md5 = buf.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return re_md5;
        }
    }
}
