package com.hublot.route;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.*;

public class HTRouteManager extends ReactContextBaseJavaModule {

    public HTRouteManager(@NonNull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "HTRouteManager";
    }

    private HTRouteTabBarController findTabBarController(ViewGroup viewGroup) {
        if (viewGroup.getTag() instanceof HTRouteTabBarController) {
            return (HTRouteTabBarController) viewGroup.getTag();
        }
        for (int index = 0; index < viewGroup.getChildCount(); index ++) {
            View view = viewGroup.getChildAt(index);
            if (view instanceof ViewGroup) {
                HTRouteTabBarController tabBarController = findTabBarController((ViewGroup) view);
                if (tabBarController != null) {
                    return tabBarController;
                }
            }
        }
        return null;
    }

    @ReactMethod()
    public void route(final ReadableMap routeData) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup rootView = (ViewGroup) HTRouteGlobal.activity.getWindow().getDecorView();
                HTRouteTabBarController tabBarController = findTabBarController(rootView);
                HTRouteNavigationController navigationController = (HTRouteNavigationController) tabBarController.findSelectedFragment();
                HTRouteController controller = navigationController.childControllerList.get(0);
                HTRouteViewManager.handlerRouteDataWithController(controller, routeData.toHashMap());
            }
        });
    }



}
