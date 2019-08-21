package com.t.pausi.Bean;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nitin on 12/12/2017.
 */

public class MySharedPref {
    SharedPreferences sp;

    public void saveData(Context context, String key, String value)
    {
        sp = context.getSharedPreferences("shipit",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public String getData(Context context, String key, String value)
    {
        sp = context.getSharedPreferences("shipit",context.MODE_PRIVATE);
        return sp.getString(key,value);
    }
    public void DeleteData(Context context)
    {
        sp = context.getSharedPreferences("shipit",context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }


    public void NullData(Context context , String key)
    {
        sp = context.getSharedPreferences("shipit",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,null);
        editor.commit();
    }
    public  void removeFromSharedPreferences(Context context, String key) {

        sp=context.getSharedPreferences("shipit",context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
     /*  if (mContext != null) {
            SharedPreferences mSharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);
            if (mSharedPreferences != null)
                mSharedPreferences.edit().remove(key).commit();
        }  */
    }

}
