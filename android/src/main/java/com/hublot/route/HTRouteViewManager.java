package com.hublot.route;

import android.content.Intent;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

import java.io.Serializable;
import java.util.Map;

public class HTRouteViewManager extends ReactViewManager {

    @NonNull
    @Override
    public String getName() {
        return "HTRouteView";
    }

    @NonNull
    @Override
    public HTRouteView createViewInstance(@NonNull ThemedReactContext reactContext) {
        HTRouteView routeView = new HTRouteView(reactContext);
        return routeView;
    }

    private static RelativeLayout rootPresentViewController() {
        ViewGroup rootView = (ViewGroup) HTRouteGlobal.activity.getWindow().getDecorView().findViewById(android.R.id.content);
        int id = 1000001;
        RelativeLayout presentView = (RelativeLayout)rootView.findViewById(id);
        if (presentView == null) {
            presentView = new RelativeLayout(HTRouteGlobal.activity);
            presentView.setId(id);
            rootView.addView(presentView, HTRouteGlobal.matchParent);
        }
        return presentView;
    }

    private static Rect packComponentEdgeList(Map<String, Serializable> componentEdgeList) {
        if (componentEdgeList == null) {
            return new Rect(0, 0, 0, 0);
        }
        return new Rect(
                componentEdgeList.containsKey("left")
                        ? (int) PixelUtil.toPixelFromDIP((Double) componentEdgeList.get("left"))
                        : 0,
                componentEdgeList.containsKey("top") ? (int) PixelUtil.toPixelFromDIP((Double) componentEdgeList.get("top")) : 0,
                componentEdgeList.containsKey("right")
                        ? (int) PixelUtil.toPixelFromDIP((Double) componentEdgeList.get("right"))
                        : 0,
                componentEdgeList.containsKey("bottom")
                        ? (int) PixelUtil.toPixelFromDIP((Double) componentEdgeList.get("bottom"))
                        : 0);
    }

    public static void handlerRouteDataWithController(HTRouteController controller, @Nullable Map<String, Object> routeData) {
        String action = (String) routeData.get("action");
        if (action == null) {
            action = "push";
        }
        String componentName = (String) routeData.get("componentName");
        Boolean animated = (Boolean)routeData.get("animated");
        if (animated == null) {
            animated = true;
        }
        Map<String, Serializable> componentRouteOptionList = (Map<String, Serializable>) routeData.get("componentRouteOptionList");
        HTRouteNavigationController navigationController = HTRouteGlobal.nextController(controller.getView(), HTRouteNavigationController.class);
        HTRouteTabBarController tabBarController = HTRouteGlobal.nextController(controller.getView(), HTRouteTabBarController.class);
        if (action.equals("push") || action.equals("navigate")) {
            if (action.equals("navigate")) {
                for (HTRouteTabBarModel model: tabBarController.modelList) {
                    if (!(model.fragment instanceof HTRouteNavigationController)) {
                        continue;
                    }
                    HTRouteNavigationController tabNavigationController = (HTRouteNavigationController) model.fragment;
                    HTRouteController routeController = tabNavigationController.childControllerList.get(0);
                    if (routeController.componentName.equals(componentName)) {
                        navigationController.popToRootViewControllerAnimated(false);
                        tabNavigationController.popToRootViewControllerAnimated(false);
                        tabBarController.reloadSelectedViewController(tabNavigationController);
                        return;
                    }
                }
                for (HTRouteController childController: navigationController.childControllerList) {
                    if (childController.componentName.equals(componentName)) {
                        navigationController.popToViewController(childController, animated);
                        return;
                    }
                }
            }
            HTRouteController routeController = new HTRouteController(componentName, componentRouteOptionList);
            navigationController.pushViewController(routeController, animated, null);
        } else if (action.equals("replace")) {
            HTRouteController routeController = new HTRouteController(componentName, componentRouteOptionList);
            navigationController.replaceViewController(routeController, animated);
        } else if (action.equals("pop") || action.equals("back") || action.equals("goBack")) {
            navigationController.popViewController(animated);
        } else if (action.equals("popToTop") || action.equals("popToRoot")) {
            navigationController.popToRootViewControllerAnimated(animated);
        } else if (action.equals("present")) {
            RelativeLayout presentView = rootPresentViewController();
            HTRouteController routeController = new HTRouteController(componentName, componentRouteOptionList);
            routeController.hidesBottomBarWhenPushed = true;
            HTRouteNavigationController presentNavigationController = new HTRouteNavigationController(routeController);
            Map<String, Serializable> componentEdgeList = (Map<String, Serializable>) componentRouteOptionList.get("componentEdge");
            Rect rect = packComponentEdgeList(componentEdgeList);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(rect.left, rect.top, rect.right, rect.bottom);
            presentView.addView(presentNavigationController.getView(), layoutParams);
            presentNavigationController.viewDidAppear();
        } else if (action.equals("dismiss")) {
            RelativeLayout presentView = rootPresentViewController();
            for (int i = 0; i < presentView.getChildCount(); i ++) {
                View navigationControllerView = presentView.getChildAt(i);
                HTRouteNavigationController presentNavigationController = HTRouteGlobal.nextController(navigationControllerView, HTRouteNavigationController.class);
                HTRouteController routeController = presentNavigationController.childControllerList.get(0);
                if (routeController.componentName.equals(componentName)) {
                    presentView.removeView(navigationControllerView);
                    presentNavigationController.viewDidDisappear();
                    presentNavigationController.dealloc();
                }
            }
        }
    }

    private void touchRouteData(@NonNull final HTRouteView routeView, @Nullable final Map<String, Object> routeData) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HTRouteController routeController = HTRouteGlobal.nextController(routeView, HTRouteController.class);
                handlerRouteDataWithController(routeController, routeData);
            }
        });
    }

    @ReactProp(name = "routeData")
    public void setRouteData(final HTRouteView routeView, ReadableMap routeDataMap) {
        final Map<String, Object> routeData = routeDataMap == null ? null : routeDataMap.toHashMap();
        if (routeData != null && routeData.size() > 0) {
            routeView.setClickable(true);
            routeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    touchRouteData(routeView, routeData);
                }
            });
        } else {
            routeView.setClickable(false);
            routeView.setOnClickListener(null);
        }
    }

    @Override
    public void receiveCommand(ReactViewGroup routeView, String commandId, @Nullable ReadableArray args) {
        super.receiveCommand(routeView, commandId, args);
        switch (commandId) {
            case "touchRouteData": {
                touchRouteData((HTRouteView) routeView, args.getMap(0).toHashMap());
                break;
            }
        }
    }

}
