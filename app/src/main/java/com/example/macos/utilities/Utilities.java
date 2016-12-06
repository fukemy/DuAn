package com.example.macos.utilities;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by admin2 on 12/6/16.
 */

public class Utilities {
    public static void refreshAlphaViewChild(ViewGroup v){
        v.setAlpha(1);
        for(int i = 0; i < v.getChildCount(); i++){
            v.getChildAt(i).setAlpha(1);
        }
    }
}
