package com.hublot.route;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;

import java.lang.ref.WeakReference;

public class HTRouteReactRootView extends ReactRootView {

    public Bundle bundle;

    private Boolean isStarted = false;

    public HTRouteReactRootView(Context context) {
        super(context);
    }

    public void startReactRootView() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        ReactInstanceManager instanceManager = ((ReactApplication)HTRouteGlobal.application).getReactNativeHost().getReactInstanceManager();
        startReactApplication(instanceManager, HTRouteGlobal.moduleName, bundle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return false;
    }

    public interface HTRouteReactRootViewEventListener {
        /** Called when the react context is attached to a ReactRootView. */
        void onContentAppearToReactInstance(HTRouteReactRootView rootView);
    }

    public void dealloc() {
        unmountReactApplication();
        routeEventListener = null;
    }

    public WeakReference<HTRouteReactRootViewEventListener> routeEventListener;

    private Boolean hadContentAppear = false;

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (!hadContentAppear) {
            if (routeEventListener != null) {
                routeEventListener.get().onContentAppearToReactInstance(this);
            }
            hadContentAppear = true;
        }
    }
}
