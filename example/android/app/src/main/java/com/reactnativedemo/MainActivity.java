package com.reactnativedemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.react.ReactActivity;
import com.hublot.route.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ReactActivity {

  private HTRouteTabBarController tabBarController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
      finish();
      return;
    }
    setReactNativeContentView();
  }

  private void setReactNativeContentView() {
    HTRouteGlobal.activity = this;
    this.tabBarController = new HTRouteTabBarController() {
      private Map createComponentRouteOption() {
        final int count = modelList.size();
        Map<String, Serializable> componentRouteOption = new HashMap() {{
          put("id", count);
        }};
        return componentRouteOption;
      }
      @Override
      public void initDataSource() {
        modelList.clear();
        modelList.add(
            new HTRouteTabBarModel("Home", R.mipmap.tabbar_home, R.mipmap.tabbar_home_selected,
                new HTRouteNavigationController(new HTRouteController("Home", createComponentRouteOption())))
        );
        modelList.add(
            new HTRouteTabBarModel("Mine", R.mipmap.tabbar_mine, R.mipmap.tabbar_mine_selected,
                new HTRouteNavigationController(new HTRouteController("Mine", createComponentRouteOption())))
        );
      }
      @Override
      public void cellForIndex(ViewGroup imageContainer, ImageView imageView, TextView textView, int index, boolean isSelected) {
        super.cellForIndex(imageContainer, imageView, textView, index, isSelected);
        HTRouteTabBarModel model = modelList.get(index);
        textView.setText(model.title);
        textView.setTextColor(isSelected ? 0xFF383C46 : 0xFF7E828A);
        imageView.setImageResource(isSelected ? model.selectedImage : model.image);
      }
    };
    setContentView(this.tabBarController.getView());
  }

  @Override
  public void invokeDefaultOnBackPressed() {
    HTRouteNavigationController selectedFragment = (HTRouteNavigationController) tabBarController.findSelectedFragment();
    if (selectedFragment.childControllerList.size() > 1) {
      selectedFragment.popViewController(true);
    } else {
      moveTaskToBack(true);
//    super.invokeDefaultOnBackPressed();
    }
  }

}
