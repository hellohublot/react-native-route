//
//  HTRouteManager.m
//  RNReactNativeRoute
//
//  Created by hublot on 2020/10/25.
//

#import "HTRouteManager.h"
#import "HTRouteViewManager.h"

@implementation HTRouteManager

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

+ (UIViewController *)findController:(UIViewController *)controller controllerClass:(Class)controllerClass {
    if ([controller isKindOfClass:controllerClass]) {
        return controller;
    }
    for (UIViewController *childController in controller.childViewControllers) {
        UIViewController *findController = [self findController:childController controllerClass:controllerClass];
        if (findController) {
            return findController;
        }
    }
    return nil;
}

+ (void)handlerRouteDataToController:(UIViewController *)toController routeData:(NSDictionary *)routeData {
    RCTExecuteOnMainQueue(^{
        UIViewController *rootController = [UIApplication sharedApplication].keyWindow.rootViewController;
        UITabBarController *tabBarController = (UITabBarController *)[[self class] findController:rootController controllerClass:[UITabBarController class]];
        UINavigationController *navigationController = tabBarController.selectedViewController;
        if (tabBarController == nil) {
            navigationController = (UINavigationController *)[[self class] findController:rootController controllerClass:[UINavigationController class]];
        }
        UIViewController *controller = navigationController.visibleViewController;
        if (!controller) {
            return;
        }
        [HTRouteViewManager handlerRouteDataWithController:controller toController:toController routeData:routeData];
    });
}

+ (void)handlerRoute:(NSDictionary *)routeData {
    RCTExecuteOnMainQueue(^{
        [self handlerRouteDataToController:nil routeData:routeData];
    });
}

RCT_EXPORT_METHOD(route:(NSDictionary *)routeData) {
    [[self class] handlerRoute:routeData];
}

@end
