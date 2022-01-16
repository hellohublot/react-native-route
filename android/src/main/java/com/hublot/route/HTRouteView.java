package com.hublot.route;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.facebook.react.views.view.ReactViewGroup;

public class HTRouteView extends ReactViewGroup {

	public HTRouteView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	    if (this.isClickable()) {
	        if (!inRangeOfView(this, ev)) {
	            return false;
            }

            float alpha = 1;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                alpha = getAlpha();
            }
            if (alpha > 0 && ev.getAction() == MotionEvent.ACTION_UP) {
	            performClick();
            }
	        return true;
        }
        return super.onTouchEvent(ev);
    }

    private boolean inRangeOfView(View view, MotionEvent ev){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if(ev.getRawX() < x || ev.getRawX() > (x + view.getWidth()) || ev.getRawY() < y || ev.getRawY() > (y + view.getHeight())){
            return false;
        }
        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isClickable()) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
