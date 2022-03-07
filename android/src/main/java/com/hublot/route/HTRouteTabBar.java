package com.hublot.route;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.react.uimanager.PixelUtil;

public class HTRouteTabBar extends RelativeLayout {

    public HTRouteTabBarDelegate delegate;

    public int selectedIndex = -1;

    public Boolean isShow = true;


    public HTRouteTabBar(Context context, HTRouteTabBarDelegate delegate) {
        super(context);
        this.delegate = delegate;
        this.reloadData();
        if (this.delegate.itemCount() > 0) {
            this.reloadSelectedIndex(0);
        }
    }

    public int imageContainerId(int index) {
        return 100 + index;
    }

    public int textViewId(int index) {
        return 200 + index;
    }

    public int containerId(int index) {
        return 300 + index;
    }

    public int buttonId(int index) {
        return 400 + index;
    }

    public void reloadItemIndex(int index) {
        if (index >= 0 && index < delegate.itemCount()) {
            Boolean isSelected = index == selectedIndex;
            ViewGroup container = (ViewGroup) findViewById(containerId(index));
            TextView button = (TextView) findViewById(buttonId(index));
            ViewGroup imageContainer = (ViewGroup) findViewById(imageContainerId(index));
            TextView textView = (TextView) findViewById(textViewId(index));
            ImageView imageView = (ImageView) imageContainer.findViewById(0);
            delegate.cellForIndex(container, button, imageContainer, imageView, textView, index, isSelected);
        }
    }

    public void reloadSelectedIndex(int index) {
        if (index == selectedIndex) {
            return;
        }
        if (!delegate.shouldItemSelected(index)) {
            return;
        }

        int lastSelectedIndex = selectedIndex;
        selectedIndex = index;
        reloadItemIndex(lastSelectedIndex);
        reloadItemIndex(index);
        delegate.didItemSelected(index);
    }

    public void initSeparatorLine(ViewGroup viewGroup) {
        View view = new View(HTRouteGlobal.activity);
        view.setBackgroundColor(Color.LTGRAY);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, HTRouteGlobal.dp2px(0.5f)));
        viewGroup.addView(view);
    }

    public void reloadData() {
        this.removeAllViews();
        this.initSeparatorLine(this);
        LinearLayout linearLayout = new LinearLayout(HTRouteGlobal.activity);
        this.addView(linearLayout, HTRouteGlobal.matchParent);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TypedValue typedValue = new TypedValue();
        int imageId = android.R.attr.selectableItemBackground;
        HTRouteGlobal.activity.getTheme().resolveAttribute(imageId, typedValue, true);
        int[] attribute = new int[]{imageId};
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(typedValue.resourceId, attribute);

        for (int index = 0; index < delegate.itemCount(); index ++) {
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            relativeLayout.setId(containerId(index));

            TextView button = new TextView(getContext());
            button.setId(buttonId(index));
            button.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ((LayoutParams)(button.getLayoutParams())).addRule(CENTER_IN_PARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Drawable drawable = typedArray.getDrawable(0);
                button.setForeground(drawable);
            }
            relativeLayout.addView(button);

            relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));


            RelativeLayout imageContainer = new RelativeLayout(getContext());
            imageContainer.setId(imageContainerId(index));
            imageContainer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ImageView imageView = new ImageView(getContext());
            imageView.setId(0);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageContainer.addView(imageView);

            relativeLayout.addView(imageContainer);

            TextView textView = new TextView(getContext());
            textView.setId(textViewId(index));
            textView.setTextSize(10);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            relativeLayout.addView(textView);

            ((RelativeLayout.LayoutParams)imageContainer.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL);
            ((RelativeLayout.LayoutParams)textView.getLayoutParams()).addRule(RelativeLayout.CENTER_HORIZONTAL);
            ((RelativeLayout.LayoutParams)textView.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            ((RelativeLayout.LayoutParams)imageContainer.getLayoutParams()).addRule(RelativeLayout.ABOVE, textView.getId());
            textView.setPadding(0, HTRouteGlobal.dp2px(3), 0, HTRouteGlobal.dp2px(1.5f));
            final int finalIndex = index;
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    reloadSelectedIndex(finalIndex);
                }
            });

            linearLayout.addView(relativeLayout);
            reloadItemIndex(index);

        }
    }



}
