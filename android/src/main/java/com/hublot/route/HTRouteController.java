package com.hublot.route;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.RootView;
import com.facebook.react.views.view.ReactViewBackgroundDrawable;
import com.facebook.react.views.view.ReactViewGroup;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HTRouteController extends HTRouteFragment implements HTRouteReactRootView.HTRouteReactRootViewEventListener {

    public String componentName;

    public Map<String, Serializable> componentRouteOptionList;

    public Boolean hidesBottomBarWhenPushed = false;

    public HTRouteReactRootView rootView;

    public ProgressBar progressBar;

    public HTRouteController(String componentName, Map<String, Serializable> componentRouteOptionList) {
        this.componentName = componentName;
        Map<String, Serializable> reloadComponentRouteOptionList = componentRouteOptionList != null ? componentRouteOptionList : new HashMap<String, Serializable>();
        this.componentRouteOptionList = reloadComponentRouteOptionList;
    }

    private Bundle createBundleFromMap(Map<String, Serializable> map) {
        Bundle bundle = new Bundle();
        if (map != null && map.size() > 0) {
            for (String key: map.keySet()) {
                Serializable value = map.get(key);
                bundle.putSerializable(key, value);
            }
        }
        return bundle;
    }

    @Override
    protected ViewGroup createViewGroup() {
        RelativeLayout relativeLayout = new RelativeLayout(HTRouteGlobal.activity);
        relativeLayout.setClipChildren(true);

        rootView = new HTRouteReactRootView(HTRouteGlobal.activity);
        rootView.routeEventListener = this;
        Bundle bundle = new Bundle();
        bundle.putString("componentName", componentName);
        bundle.putBundle("componentRouteOptionList", createBundleFromMap(componentRouteOptionList));
        rootView.bundle = bundle;

        rootView.setLayoutParams(HTRouteGlobal.matchParent);
        relativeLayout.addView(rootView);

        Object showLoadingValue = componentRouteOptionList.get("showLoading");
        if (showLoadingValue == null) {
            showLoadingValue = true;
        }
        progressBar = new ProgressBar(HTRouteGlobal.activity);
        if ((Boolean)showLoadingValue) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(Color.LTGRAY));
        }
        progressBar.setLayoutParams(new RelativeLayout.LayoutParams(100, 100));
        ((RelativeLayout.LayoutParams)(progressBar.getLayoutParams())).addRule(RelativeLayout.CENTER_IN_PARENT);
        relativeLayout.addView(progressBar);

        Object lazyRenderValue = componentRouteOptionList.get("lazyRender");
        if (lazyRenderValue == null) {
        	lazyRenderValue = false;
        }
        if (!((Boolean)lazyRenderValue)) {
        	rootView.startReactRootView();
        }

        return relativeLayout;
    }

    private Boolean isSecondAppear = false;

    @Override
    public void dealloc() {
        rootView.unmountReactApplication();
        rootView.routeEventListener = null;
        super.dealloc();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.sendNotificationWithActionName("dealloc", null);
    }

    @Override
    public void viewDidAppear() {
        if (!isSecondAppear) {
            rootView.startReactRootView();
        }
        Map<String, Object> valueList = new HashMap<>();
        valueList.put("isSecondAppear", isSecondAppear);
        this.sendNotificationWithActionName("componentDidAppear", valueList);
        this.isSecondAppear = true;
    }

    @Override
    public void viewDidDisappear() {
        this.sendNotificationWithActionName("componentDidDisappear", null);
    }

    private void sendNotificationWithActionName(String actionName, Map<String, Object> valueList) {
        WritableMap userInfo = Arguments.makeNativeMap(valueList);
        userInfo.merge(Arguments.makeNativeMap(createBundleFromMap(this.componentRouteOptionList)));
        userInfo.putString("componentName", this.componentName);
        userInfo.putString("actionName", actionName);
        HTRouteEventManager.sendEvent(userInfo);
    }


    @Override
    public void onContentAppearToReactInstance(HTRouteReactRootView rootView) {
        progressBar.setVisibility(View.INVISIBLE);
        getView().removeView(progressBar);
        if (componentRouteOptionList != null) {
            Double backgroundColor = (Double) componentRouteOptionList.get("backgroundColor");
            if (backgroundColor != null) {
                getView().setBackgroundColor(backgroundColor.intValue());
            }
        }
    }
}
