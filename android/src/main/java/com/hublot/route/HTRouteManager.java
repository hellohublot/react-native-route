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

    @ReactMethod()
    public void route(final ReadableMap routeData) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup rootView = (ViewGroup) HTRouteGlobal.activity.getWindow().getDecorView();
                HTRouteTabBarController tabBarController = HTRouteGlobal.lastController(rootView, HTRouteTabBarController.class);
                HTRouteNavigationController navigationController = null;
                if (tabBarController != null) {
                    navigationController = (HTRouteNavigationController) tabBarController.findSelectedFragment();
                } else {
                    navigationController = HTRouteGlobal.lastController(rootView, HTRouteNavigationController.class);
                }
                HTRouteController controller = null;
                if (navigationController != null) {
                    controller = navigationController.childControllerList.get(0);
                }
                HTRouteViewManager.handlerRouteDataWithController(controller, routeData.toHashMap());
            }
        });
    }



}
