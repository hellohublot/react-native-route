//
//  HTRouteRootViewManager.h
//  ReactNativeProject
//
//  Created by hublot on 2020/9/17.
//

#import <Foundation/Foundation.h>
#import <React/RCTViewManager.h>

NS_ASSUME_NONNULL_BEGIN

@interface HTRouteViewManager : RCTViewManager

+ (void)handlerRouteDataWithController:(UIViewController *)controller routeData:(NSDictionary *)routeData;

+ (void)handlerRouteDataWithController:(UIViewController *)controller toController:(UIViewController *)toController routeData:(NSDictionary *)routeData;

@end

NS_ASSUME_NONNULL_END
