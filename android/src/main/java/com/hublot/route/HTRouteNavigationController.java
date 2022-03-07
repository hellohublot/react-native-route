package com.hublot.route;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.telecom.Call;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.RelativeLayout;
import com.facebook.react.bridge.Callback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.ALPHA;
import static android.view.View.TRANSLATION_Y;

public class HTRouteNavigationController extends HTRouteFragment {

    public List<HTRouteController> childControllerList = new ArrayList<>();

    private Boolean lock = false;

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
        if (this.lock) {
            return;
        }
        this.lock = true;
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
                lastController.getView().setVisibility(View.GONE);
                if (complete != null) {
                    complete.invoke();
                }
                lock = false;
            }
        });
    }

    public void replaceViewController(HTRouteController controller, Boolean animated) {
        if (childControllerList.size() <= 0) {
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
        if (this.lock) {
            return;
        }
        this.lock = true;
        final int index = childControllerList.indexOf(controller);
        final HTRouteController animatedController = childControllerList.get(childControllerList.size() - 1);
        if (index == 0) {
            HTRouteTabBarController tabBarController = HTRouteGlobal.nextController(getView(), HTRouteTabBarController.class);
            if (tabBarController != null) {
                tabBarController.reloadShowTabBar(true);
            }
        }
        controller.getView().setVisibility(View.VISIBLE);
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
                lock = false;
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

    private void translateAnimation(HTRouteController controller, View view, Boolean isPush, final Boolean animated, final Callback complete) {
        float height = getView().getHeight();
        float fromYValue = isPush ? height : 0;
        float toYValue = isPush ? 0 : height;
        long duration = 350;
        float fromOpacityValue = isPush ? 0f : 1;
        float toOpacityValue = isPush ? 1 : 0;

        if (!animated || Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            if (complete != null) {
                complete.invoke();
            }
            return;
        }

        final AnimatorSet animationList = new AnimatorSet();
        if (isPush) {
            animationList.setInterpolator(new DecelerateInterpolator());
        }

        ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(view, TRANSLATION_Y, fromYValue, toYValue);
        translateAnimation.setDuration(duration);

        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, ALPHA, fromOpacityValue, toOpacityValue);
        alphaAnimation.setDuration(duration);
        animationList.playTogether(translateAnimation, alphaAnimation);
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

        animationList.setDuration((long) duration);
        animationList.start();

    }

}
