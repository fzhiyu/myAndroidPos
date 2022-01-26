package com.cuse.myandroidpos;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SavaOrderLists {
    private static SharedPreferences sPref;

    private static SharedPreferences getPreference(Context ctx) {
        if (sPref == null) {
            sPref = ctx.getApplicationContext()
                    .getSharedPreferences("OrderLists", Context.MODE_PRIVATE);
        }
        return sPref;
    }

    private static SharedPreferences.Editor getEditor(Context ctx) {
        return getPreference(ctx).edit();
    }

    private static void writeObject(Context ctx, String key, Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            String objBase64 = new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));
            getEditor(ctx).putString(key, objBase64).commit();
        } catch (Exception e) {
            Log.e("test", "saveObject error", e);
        }
    }

    private static Object readObject(Context ctx, String key){
        try {
            String objBase64 = getPreference(ctx).getString(key, null);
            if (TextUtils.isEmpty(objBase64))
                return null;
            byte[] base64 = Base64.decode(objBase64, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64);
            ObjectInputStream bis = new ObjectInputStream(bais);
            return bis.readObject();
        }catch (Exception e){
            Log.e("test", "readObject error", e);
        }

        return null;
    }
}
