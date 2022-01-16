//
//  HTRouteManager.h
//  RNReactNativeRoute
//
//  Created by hublot on 2020/10/25.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

NS_ASSUME_NONNULL_BEGIN

@interface HTRouteManager : NSObject <RCTBridgeModule>

+ (void)handlerRoute:(NSDictionary *)routeData;

+ (void)handlerRouteDataToController:(UIViewController *)toController routeData:(NSDictionary *)routeData;

@end

NS_ASSUME_NONNULL_END
