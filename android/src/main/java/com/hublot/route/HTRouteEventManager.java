package com.hublot.route;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.*;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.Map;

public class HTRouteEventManager extends ReactContextBaseJavaModule {

    public HTRouteEventManager(ReactApplicationContext context) {
        super(context);
    }

    private static final String HTRouteEventOnChangeKey = "onHTRouteEventChange";

    @NonNull
    @Override
    public String getName() {
        return "HTRouteEventManager";
    }

    public static void sendEvent(ReadableMap valueList) {
        ReactContext context = HTRouteGlobal.application.getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
        if (context == null) {
            return;
        }
        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(
            HTRouteEventOnChangeKey,
            valueList
        );
    }

}
