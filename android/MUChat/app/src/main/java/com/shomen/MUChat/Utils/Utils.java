package com.shomen.MUChat.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by USER on 1/31/2016.
 */
public class Utils {

    public Utils() {
    }

    public static void showToast(Context context,String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

}
