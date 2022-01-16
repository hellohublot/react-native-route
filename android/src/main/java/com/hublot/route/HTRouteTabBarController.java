package com.hublot.route;


import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class HTRouteTabBarController extends HTRouteFragment implements HTRouteTabBarDelegate {

    public List<HTRouteTabBarModel> modelList = new ArrayList<>();

    public ViewGroup fragmentContainer;

    public HTRouteTabBar tabBar;

    @Override
    protected ViewGroup createViewGroup() {
        initDataSource();
        RelativeLayout relativeLayout = new RelativeLayout(HTRouteGlobal.activity);
        relativeLayout.setClipChildren(false);
        fragmentContainer = initFragmentContainer(relativeLayout);
        initTabBar(relativeLayout);
        return relativeLayout;
    }

    protected void initDataSource() {

    }

    protected ViewGroup initFragmentContainer(ViewGroup viewGroup) {
        FrameLayout frameLayout = new FrameLayout(HTRouteGlobal.activity);
        viewGroup.addView(frameLayout, HTRouteGlobal.matchParent);
        return frameLayout;
    }

    protected void initTabBar(ViewGroup viewGroup) {
        tabBar = new HTRouteTabBar(HTRouteGlobal.activity, this);
        tabBar.setBackgroundColor(Color.WHITE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HTRouteGlobal.tabBarHeight());
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        viewGroup.addView(tabBar, layoutParams);
    }

    public void reloadSelectedViewController(HTRouteFragment fragment) {
        for (int i = 0; i < modelList.size(); i ++) {
            HTRouteTabBarModel model = modelList.get(i);
            if (model.fragment == fragment) {
                tabBar.reloadSelectedIndex(i);
                return;
            }
        }
    }

    public void reloadShowTabBar(Boolean showTabbar) {
        if (tabBar.isShow == showTabbar) {
            return;
        }
    	tabBar.isShow = showTabbar;
        getView().bringChildToFront(showTabbar ? tabBar : fragmentContainer);
    }

    public HTRouteFragment findSelectedFragment() {
        if (fragmentContainer.getChildCount() <= 0) {
            return null;
        }
        return (HTRouteFragment) fragmentContainer.getChildAt(0).getTag();
    }


    @Override
    public int itemCount() {
        return modelList.size();
    }

    @Override
    public void cellForIndex(ViewGroup imageContainer, ImageView imageView, TextView textView, int index, boolean isSelected) {

    }

    @Override
    public boolean shouldItemSelected(int index) {
        return true;
    }

    @Override
    public void didItemSelected(int index) {
        HTRouteFragment fragment = modelList.get(index).fragment;

        if (fragmentContainer.getChildCount() > 0) {
            HTRouteFragment lastFragment = (HTRouteFragment) fragmentContainer.getChildAt(0).getTag();
            lastFragment.viewDidDisappear();
        }


        View view = fragment.getView();
        fragmentContainer.removeAllViews();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        int bottom = 0;
        if (!TextUtils.isEmpty(HTRouteGlobal.getProp("ro.build.version.emui"))) {
            bottom = 1;
        }
        layoutParams.setMargins(0, 0, 0, bottom);
        fragmentContainer.addView(view, layoutParams);

        fragment.viewDidAppear();

    }

    @Override
    public void dealloc() {
        super.dealloc();
        for (HTRouteTabBarModel model: modelList) {
            model.fragment.dealloc();
        }
    }

}
