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

+ (UITabBarController *)findTabBarController:(UIViewController *)controller {
    UITabBarController *tabBarController = controller.tabBarController;
    if (tabBarController) {
        return tabBarController;
    }
    for (UIViewController *childController in controller.childViewControllers) {
        UITabBarController *findController = [self findTabBarController:childController];
        if (findController) {
            return findController;
        }
    }
    return nil;
}

+ (UIViewController *)findFromController {
    UIViewController *rootController = [UIApplication sharedApplication].keyWindow.rootViewController;
    UITabBarController *tabBarController = [[self class] findTabBarController:rootController];
    UINavigationController *navigationController = tabBarController.selectedViewController;
    UIViewController *controller = navigationController.visibleViewController;
    return controller;
}

+ (void)handlerRouteDataToController:(UIViewController *)toController routeData:(NSDictionary *)routeData {
    RCTExecuteOnMainQueue(^{
        UIViewController *fromController = [self findFromController];
        if (!fromController) {
            return;
        }
        [HTRouteViewManager handlerRouteDataWithController:fromController toController:toController routeData:routeData];
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
