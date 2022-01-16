package com.hublot.route;

import androidx.fragment.app.Fragment;;

public class HTRouteTabBarModel {

    public String title;

    public int image;

    public int selectedImage;

    public HTRouteFragment fragment;

    public HTRouteTabBarModel(String title, int image, int selectedImage, HTRouteFragment fragment) {
        this.title = title;
        this.image = image;
        this.selectedImage = selectedImage;
        this.fragment = fragment;
    }

}
