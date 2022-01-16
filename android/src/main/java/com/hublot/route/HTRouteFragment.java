package com.hublot.route;

import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.facebook.react.ReactRootView;
import com.facebook.react.uimanager.ReactRoot;

public abstract class HTRouteFragment extends Fragment {

    protected abstract ViewGroup createViewGroup();

    private ViewGroup _view;

    @Nullable
    @Override
    public ViewGroup getView() {
        if (_view == null) {
            _view = createViewGroup();
            _view.setTag(this);
        }
        return _view;
    }

    public void dealloc() {
        try {
            this.getView().setTag(null);
            _view = null;
            this.finalize();
        } catch (Throwable throwable) {
            System.out.println("");
        }
    }

    public void viewDidAppear() {

    }

    public void viewDidDisappear() {

    }

}
