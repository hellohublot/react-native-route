package com.hublot.route;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.*;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.Map;

import static android.view.View.ALPHA;
import static android.view.View.TRANSLATION_Y;

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
        ViewGroup rootView = (ViewGroup) HTRouteGlobal.activity.getWindow().getDecorView();
        int id = 1000001;
        RelativeLayout presentView = (RelativeLayout)rootView.findViewById(id);
        if (presentView == null) {
            presentView = new RelativeLayout(HTRouteGlobal.activity);
            presentView.setId(id);
            rootView.addView(presentView, HTRouteGlobal.matchParent);
        }
        return presentView;
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

        Double presentEdgeTop = (Double) componentRouteOptionList.get("presentEdgeTop");
        if (presentEdgeTop == null) {
            presentEdgeTop = (double) 0;
        }
        presentEdgeTop = (double) HTRouteGlobal.dp2px(presentEdgeTop.floatValue());
        Double presentAnimatedDuration = (Double) componentRouteOptionList.get("presentAnimatedDuration");
        if (presentAnimatedDuration == null) {
            presentAnimatedDuration = (double)250;
        }

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
            final HTRouteNavigationController presentNavigationController = new HTRouteNavigationController(routeController);
            RelativeLayout presentBackgroundView = new RelativeLayout(HTRouteGlobal.activity);
            Double presentBackgroundColor = (Double) componentRouteOptionList.get("presentBackgroundColor");
            if (presentBackgroundColor == null) {
                presentBackgroundColor = (double)0;
            }
            presentBackgroundView.setBackgroundColor(presentBackgroundColor.intValue());

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, presentEdgeTop.intValue(), 0, 0);
            presentBackgroundView.addView(presentNavigationController.getView(), layoutParams);
            presentView.addView(presentBackgroundView, HTRouteGlobal.matchParent);
            translatePresentAnimation(presentBackgroundView, presentNavigationController.getView(), presentEdgeTop, true, presentAnimatedDuration, new Callback() {
                @Override
                public void invoke(Object... args) {
                    presentNavigationController.viewDidAppear();
                }
            });
        } else if (action.equals("dismiss")) {
            final RelativeLayout presentView = rootPresentViewController();
            for (int i = 0; i < presentView.getChildCount(); i ++) {
                final ViewGroup presentBackgroundView = (ViewGroup) presentView.getChildAt(i);
                View navigationControllerView = presentBackgroundView.getChildAt(0);
                final HTRouteNavigationController presentNavigationController = HTRouteGlobal.nextController(navigationControllerView, HTRouteNavigationController.class);
                HTRouteController routeController = presentNavigationController.childControllerList.get(0);
                if (routeController.componentName.equals(componentName)) {
                    translatePresentAnimation(presentBackgroundView, presentNavigationController.getView(), presentEdgeTop, false, presentAnimatedDuration, new Callback() {
                        @Override
                        public void invoke(Object... args) {
                            presentView.removeView(presentBackgroundView);
                            presentNavigationController.viewDidDisappear();
                            presentNavigationController.dealloc();
                        }
                    });
                }
            }
        }
    }

    private static void translatePresentAnimation(View presentBackgroundView, View navigationControllerView, double presentEdgeTop, final Boolean isPresent, double animatedDuration, final Callback complete) {
        float height = (float) (HTRouteGlobal.activity.getWindow().getDecorView().getHeight() - presentEdgeTop);
        float fromYValue = isPresent ? height : 0;
        float toYValue = isPresent ? 0 : height;
        float duration = (float) animatedDuration;
        int yDuration = (int) (duration * 0.7f);
        float fromOpacityValue = isPresent ? 1f : 1;
        float toOpacityValue = isPresent ? 1 : 0f;
        int opacityDuration = (int) (duration * 0.9f);
        if (duration <= 0 || Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            if (complete != null) {
                complete.invoke();
            }
            return;
        }

        ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(navigationControllerView, TRANSLATION_Y, fromYValue, toYValue);
        translateAnimation.setDuration(yDuration);
        translateAnimation.start();

        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(presentBackgroundView, ALPHA, fromOpacityValue, toOpacityValue);
        alphaAnimation.setDuration(opacityDuration);
        alphaAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                if (complete != null) {
                    complete.invoke();
                }
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        alphaAnimation.start();

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
    public void setRouteData(HTRouteView routeView, ReadableMap routeDataMap) {
        final Map<String, Object> routeData = routeDataMap == null ? null : routeDataMap.toHashMap();
        if (routeData != null && routeData.size() > 0) {
            routeView.setClickable(true);
            final WeakReference<HTRouteView> weakRouteView = new WeakReference<>(routeView);
            routeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    touchRouteData(weakRouteView.get(), routeData);
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
