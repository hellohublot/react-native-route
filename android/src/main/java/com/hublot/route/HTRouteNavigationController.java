package com.hublot.route;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.RelativeLayout;
import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.List;

public class HTRouteNavigationController extends HTRouteFragment {

    public List<HTRouteController> childControllerList = new ArrayList<>();

    public HTRouteNavigationController(HTRouteController rootController) {
        addChildController(rootController);
    }

    @Override
    protected ViewGroup createViewGroup() {
        RelativeLayout relativeLayout = new RelativeLayout(HTRouteGlobal.activity);
        relativeLayout.setClipChildren(true);
        return relativeLayout;
    }

    @Override
    public void dealloc() {
        super.dealloc();
        for (HTRouteController controller: childControllerList) {
            controller.dealloc();
        }
    }

    @Override
    public void viewDidAppear() {
        childControllerList.get(childControllerList.size() - 1).viewDidAppear();
    }

    @Override
    public void viewDidDisappear() {
        childControllerList.get(childControllerList.size() - 1).viewDidDisappear();
    }

    public void pushViewController(final HTRouteController controller, Boolean animated, final Callback complete) {
        final HTRouteController lastController = childControllerList.get(childControllerList.size() - 1);
        controller.hidesBottomBarWhenPushed = true;
        HTRouteTabBarController tabBarController = HTRouteGlobal.nextController(getView(), HTRouteTabBarController.class);
        if (tabBarController != null) {
            tabBarController.reloadShowTabBar(false);
        }
        addChildController(controller);
        translateAnimation(controller, controller.getView(), true, animated, new Callback() {
            @Override
            public void invoke(Object... args) {
                controller.viewDidAppear();
                lastController.viewDidDisappear();
                if (complete != null) {
                    complete.invoke();
                }
            }
        });
    }

    public void replaceViewController(final HTRouteController controller, Boolean animated) {
        if (childControllerList.size() <= 1) {
            return;
        }
        pushViewController(controller, animated, new Callback() {
            @Override
            public void invoke(Object... args) {
                HTRouteController removeController = childControllerList.get(childControllerList.size() - 2);
                removeChildController(removeController);
            }
        });
    }

    public void popViewController(Boolean animated) {
        if (childControllerList.size() <= 1) {
            return;
        }
        popToViewController(childControllerList.get(childControllerList.size() - 2), animated);
    }

    public void popToRootViewControllerAnimated(Boolean animated) {
        if (childControllerList.size() <= 1) {
            return;
        }
        popToViewController(childControllerList.get(0), animated);
    }

    public void popToViewController(final HTRouteController controller, Boolean animated) {
        if (childControllerList.size() <= 1) {
            return;
        }
        final int index = childControllerList.indexOf(controller);
        if (index == 0) {
            HTRouteTabBarController tabBarController = HTRouteGlobal.nextController(getView(), HTRouteTabBarController.class);
            if (tabBarController != null) {
                tabBarController.reloadShowTabBar(true);
            }
        }
        final HTRouteController animatedController = childControllerList.get(childControllerList.size() - 1);
        translateAnimation(animatedController, animatedController.getView(), false, animated, new Callback() {
            @Override
            public void invoke(Object... args) {
                controller.viewDidAppear();
                animatedController.viewDidDisappear();
                int size = childControllerList.size();
                List<HTRouteController> willRemoveControllerList = new ArrayList<>();
                for (int i = index + 1; i < size; i ++) {
                    HTRouteController removeController = childControllerList.get(i);
                    willRemoveControllerList.add(removeController);
                }
                for (HTRouteController removeController: willRemoveControllerList) {
                    removeChildController(removeController);
                }
            }
        });
    }



    private void addChildController(HTRouteController controller) {
        childControllerList.add(controller);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int bottom = controller.hidesBottomBarWhenPushed ? 0 : HTRouteGlobal.tabBarHeight();
        getView().addView(controller.getView(), layoutParams);
    }

    private void removeChildController(HTRouteController controller) {
        childControllerList.remove(controller);
        getView().removeView(controller.getView());
        controller.dealloc();
    }

    private void translateAnimation(final HTRouteController controller, final View view, final Boolean isPush, Boolean animated, final Callback complete) {
        float height = getView().getHeight() * ( isPush ? 0.3f : 0.7f );
        float fromYValue = isPush ? height : 0;
        float toYValue = isPush ? 0 : height;
        float duration = 250f;
        int yDuration = (int) (duration * 0.7f);
        float fromOpacityValue = isPush ? 1f : 1;
        float toOpacityValue = isPush ? 1 : 0f;
        int opacityDuration = (int) (duration * 0.9f);
        if (!animated) {
            if (complete != null) {
                complete.invoke();
            }
            return;
        }
        AnimationSet animationList = new AnimationSet(true);
        if (isPush) {
            animationList.setInterpolator(new AccelerateDecelerateInterpolator());
        }



        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, fromYValue, toYValue);
        translateAnimation.setDuration(yDuration);
        animationList.addAnimation(translateAnimation);

        AlphaAnimation alphaAnimation = new AlphaAnimation(fromOpacityValue, toOpacityValue);
        alphaAnimation.setDuration(opacityDuration);
        animationList.addAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (complete != null) {
                    complete.invoke();
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        animationList.setDuration((long) duration);
        view.startAnimation(animationList);

    }

}
