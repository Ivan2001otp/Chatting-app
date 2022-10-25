package com.example.clonechat.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Date;

public class Utility {

    public static void hideSoftKeyboard(View view){
        if(view!=null){
            InputMethodManager inputMethodManager =
                    (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    public static Date MillisToDateFormat(Long val){
        return val != null ? new Date(val) : null;
    }
}
