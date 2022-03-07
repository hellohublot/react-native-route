package com.hublot.route;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public interface HTRouteTabBarDelegate {


    public int itemCount();

    public void cellForIndex(ViewGroup container, TextView button, ViewGroup imageContainer, ImageView imageView, TextView textView, int index, boolean isSelected);

    public boolean shouldItemSelected(int index);

    public void didItemSelected(int index);

}
