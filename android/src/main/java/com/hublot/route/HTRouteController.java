package com.hublot.route;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;


import java.io.Serializable;
import java.lang.ref.WeakReference;
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
        relativeLayout.setClipChildren(false);
        Double backgroundColor = (Double) componentRouteOptionList.get("backgroundColor");
        if (backgroundColor != null) {
            relativeLayout.setBackgroundColor(backgroundColor.intValue());
        }

        String backgroundImage = (String)componentRouteOptionList.get("backgroundImage");
        if (backgroundImage != null) {
            relativeLayout.setBackgroundResource(HTRouteGlobal.activity.getResources().getIdentifier(backgroundImage, "mipmap", HTRouteGlobal.activity.getPackageName()));
        }

        rootView = new HTRouteReactRootView(HTRouteGlobal.activity);
        rootView.routeEventListener = new WeakReference<HTRouteReactRootView.HTRouteReactRootViewEventListener>(this);
        Bundle bundle = new Bundle();
        bundle.putString("componentName", componentName);
        bundle.putBundle("componentRouteOptionList", createBundleFromMap(componentRouteOptionList));
        rootView.bundle = bundle;

        rootView.setLayoutParams(HTRouteGlobal.matchParent);
        rootView.startReactRootView();

        Boolean lazyRenderValue = (Boolean)componentRouteOptionList.get("lazyRender");
        if (lazyRenderValue == null) {
            lazyRenderValue = false;
        }
        if (!lazyRenderValue) {
            relativeLayout.addView(rootView);
        }

        Boolean showLoadingValue = (Boolean) componentRouteOptionList.get("showLoading");
        if (showLoadingValue == null) {
            showLoadingValue = true;
        }
        progressBar = new ProgressBar(HTRouteGlobal.activity);
        if (showLoadingValue) {
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

        return relativeLayout;
    }

    private Boolean isSecondAppear = false;

    @Override
    public void dealloc() {
        this.sendNotificationWithActionName("dealloc", null);
        super.dealloc();
        rootView.dealloc();
        rootView = null;
    }

    @Override
    public void viewDidAppear() {
        if (!isSecondAppear) {
            if (rootView.getParent() == null) {
                getView().addView(rootView, 0);
            }
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
    }
}
