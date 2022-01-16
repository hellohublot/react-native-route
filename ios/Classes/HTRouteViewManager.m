//
//  HTRouteRootViewManager.m
//  ReactNativeProject
//
//  Created by hublot on 2020/9/17.
//

#import "HTRouteViewManager.h"
#import <React/RCTUIManager.h>
#import "HTRouteController.h"
#import <objc/runtime.h>
#import "NSDictionary+HTDictionaryPlaceholder.h"
#import "HTRoutePresentView.h"
#import "HTRouteView.h"


@interface HTRouteViewManager ()

@end

@implementation HTRouteViewManager

RCT_EXPORT_MODULE()

- (UIView *)view {
    return [[HTRouteView alloc] init];
}

RCT_EXPORT_METHOD(touchRouteData:(nonnull NSNumber *)reactTag routeData:(NSDictionary *)routeData) {
    RCTExecuteOnMainQueue(^{
        HTRouteView *view = (HTRouteView *)[self.bridge.uiManager viewForReactTag:reactTag];
        UIViewController *controller = view.reactViewController;
        [[self class] handlerRouteDataWithController:controller routeData:routeData];
    });
}

+ (void)handlerRouteDataWithController:(UIViewController *)controller routeData:(NSDictionary *)routeData {
    [self handlerRouteDataWithController:controller toController:nil routeData:routeData];
}

+ (void)handlerRouteDataWithController:(UIViewController *)controller toController:(UIViewController *)toController routeData:(NSDictionary *)routeData {
    NSString *action = [RCTConvert NSString:[routeData valueForKey:@"action" defultValue:@"push"]];
    NSString *componentName = [RCTConvert NSString:[routeData valueForKey:@"componentName"]];
    BOOL animated = [RCTConvert BOOL:[routeData valueForKey:@"animated" defultValue:@(true)]];
    NSDictionary *componentRouteOptionList = [RCTConvert NSDictionary:[routeData valueForKey:@"componentRouteOptionList"]];

    UINavigationController *navigationController = controller.navigationController;
    UITabBarController *tabBarController = controller.tabBarController;


    if ([action isEqualToString:@"push"] || [action isEqualToString:@"navigate"]) {
        if ([action isEqualToString:@"navigate"]) {
            for (UINavigationController *tabNavigationController in tabBarController.childViewControllers) {
                HTRouteController *routeController = tabNavigationController.childViewControllers.firstObject;
                if (![routeController isKindOfClass:[HTRouteController class]]) {
                    continue;
                }
                if ([routeController.componentName isEqualToString:componentName]) {
                    [navigationController popToRootViewControllerAnimated:false];
                    [tabNavigationController popToRootViewControllerAnimated:false];
                    [tabBarController setSelectedViewController:tabNavigationController];
                    return;
                }
            }
            for (UIViewController *viewController in navigationController.childViewControllers) {
                if (![viewController isKindOfClass:[HTRouteController class]]) {
                    continue;
                }
                HTRouteController *routeController = (HTRouteController *)viewController;
                if ([routeController.componentName isEqualToString:componentName]) {
                    [controller.navigationController popToViewController:routeController animated:animated];
                    return;
                }
            }
        }
        UIViewController *routeController = toController;
        if (!routeController) {
            routeController = [HTRouteController controllerWithComponentName:componentName componentRouteOptionList:componentRouteOptionList];
        }
        if (navigationController.childViewControllers.count >= 1) {
            routeController.hidesBottomBarWhenPushed = true;
        }
        [controller.navigationController pushViewController:routeController animated:animated];
    } else if ([action isEqualToString:@"replace"]) {
        NSMutableArray *childControllerList = [navigationController.childViewControllers mutableCopy];
        NSInteger count = childControllerList.count;
        if (count <= 1) {
            return;
        }
        UIViewController *routeController = toController;
        if (!routeController) {
            routeController = [HTRouteController controllerWithComponentName:componentName componentRouteOptionList:componentRouteOptionList];
        }
        if (navigationController.childViewControllers.count >= 1) {
            routeController.hidesBottomBarWhenPushed = true;
        }
        [childControllerList replaceObjectAtIndex:count - 1 withObject:routeController];
        [navigationController setViewControllers:childControllerList animated:animated];
    } else if ([action isEqualToString:@"pop"] || [action isEqualToString:@"goBack"] || [action isEqualToString:@"back"]) {
        if (navigationController.childViewControllers.count <= 1) {
            return;
        }
        [navigationController popViewControllerAnimated:animated];
    } else if ([action isEqualToString:@"popToRoot"] || [action isEqualToString:@"popToTop"]) {
        [navigationController popToRootViewControllerAnimated:animated];
    } else if ([action isEqualToString:@"present"]) {
        UIViewController *rootPresentViewController = [self rootPresentViewController];
        UIViewController *routeController = toController;
        if (!routeController) {
            routeController = [HTRouteController controllerWithComponentName:componentName componentRouteOptionList:componentRouteOptionList];
        }
        UINavigationController *presentNavigationController = [[UINavigationController alloc] initWithRootViewController:routeController];
        id componentEdgeValue = [componentRouteOptionList valueForKey:@"componentEdge"];
        UIEdgeInsets componentEdge = componentEdgeValue ? [RCTConvert UIEdgeInsets:componentEdgeValue] : UIEdgeInsetsZero;
        CGRect frame = [UIScreen mainScreen].bounds;
        presentNavigationController.view.frame = CGRectMake(
            componentEdge.left,
            componentEdge.top,
            frame.size.width - componentEdge.left - componentEdge.right,
            frame.size.height - componentEdge.top - componentEdge.bottom
        );

        [rootPresentViewController addChildViewController:presentNavigationController];
        [rootPresentViewController.view addSubview:presentNavigationController.view];
        [presentNavigationController didMoveToParentViewController:rootPresentViewController];
    } else if ([action isEqualToString:@"dismiss"]) {
        UIViewController *rootPresentViewController = [self rootPresentViewController];
        for (UINavigationController *presentNavigationController in rootPresentViewController.childViewControllers) {
            if (![presentNavigationController isKindOfClass:[UINavigationController class]]) {
                continue;
            }
            UIViewController *viewController = presentNavigationController.childViewControllers.firstObject;
            if (![viewController isKindOfClass:[HTRouteController class]]) {
                continue;
            }
            HTRouteController *routeController = (HTRouteController *)viewController;
            if ([routeController.componentName isEqualToString:componentName]) {
                [presentNavigationController removeFromParentViewController];
                [presentNavigationController.view removeFromSuperview];
            }
        }
    }
}


+ (UIViewController *)rootPresentViewController {
    UIViewController *rootControler = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *presentedViewController = rootControler.childViewControllers.lastObject;
    if (![presentedViewController.view isKindOfClass:[HTRoutePresentView class]]) {
        presentedViewController = [[UIViewController alloc] init];
        presentedViewController.view = [[HTRoutePresentView alloc] initWithFrame:rootControler.view.bounds];
        [rootControler addChildViewController:presentedViewController];
        [rootControler.view addSubview:presentedViewController.view];
        [presentedViewController didMoveToParentViewController:rootControler];
    }
    return presentedViewController;
}








- (void)handlerRouteDataGesture:(UITapGestureRecognizer *)gesture {
    HTRouteView *view = (HTRouteView *)gesture.view;
    if (view.alpha <= 0) {
        return;
    }
    NSDictionary *routeData = [view routeData];
    [self touchRouteData:view.reactTag routeData:routeData];
}


RCT_CUSTOM_VIEW_PROPERTY(routeData, NSDictionary, HTRouteView) {
    NSDictionary *routeData = [RCTConvert NSDictionary:json];
    view.routeData = routeData;
    
    
    if (view.routeData.count > 0) {
        if (!view.routeDataGesture) {
            UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handlerRouteDataGesture:)];
            [view addGestureRecognizer:gesture];
            view.routeDataGesture = gesture;
        }
    } else {
        if (view.routeDataGesture) {
            [view removeGestureRecognizer:view.routeDataGesture];
            [view setRouteDataGesture:nil];
        }
    }
};

@end





