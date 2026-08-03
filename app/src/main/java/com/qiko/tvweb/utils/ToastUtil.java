package com.qiko.tvweb.utils;

import android.widget.Toast;

import com.qiko.tvweb.QikoApp;

/**
 * <p/>
 * Toast工具类
 */
public class ToastUtil {
    public static void shortShow(String content) {
        Toast.makeText(QikoApp.getInstance(), content, Toast.LENGTH_SHORT).show();
    }

    public static void longShow(String content) {
        Toast.makeText(QikoApp.getInstance(), content, Toast.LENGTH_LONG).show();
    }
}
