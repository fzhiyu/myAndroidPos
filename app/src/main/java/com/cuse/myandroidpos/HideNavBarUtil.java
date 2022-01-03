package com.cuse.myandroidpos;

import android.os.Build;
import android.view.View;

public class HideNavBarUtil {
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

}
