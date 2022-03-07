package com.hublot.route;

import android.app.Activity;
import android.app.Application;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.ReactApplication;
import com.facebook.react.uimanager.PixelUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HTRouteGlobal {

    public static ReactApplication application;

    public static Activity activity;

    public static String moduleName;

    public static ViewGroup.LayoutParams matchParent = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);




    public static int tabBarHeight() {
        return dp2px(66);
    }

    public static int dp2px(float dp) {
        return (int) PixelUtil.toPixelFromDIP(dp);
    }

    public static <T> T nextController(View target, Class<T> cls) {
        View view = target;
        while (cls.isInstance(view.getTag()) == false) {
            if (!(view.getParent() instanceof View)) {
                return null;
            }
            view = (View) view.getParent();
        }
        return (T) view.getTag();
    }

    public static <T> T lastController(ViewGroup target, Class<T> cls) {
        if (cls.isInstance(target.getTag())) {
            return (T) target.getTag();
        }
        for (int index = 0; index < target.getChildCount(); index ++) {
            View view = target.getChildAt(index);
            if (view instanceof ViewGroup) {
                T controller = lastController((ViewGroup) view, cls);
                if (controller != null) {
                    return controller;
                }
            }
        }
        return null;
    }

    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

}
