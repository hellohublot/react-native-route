//
//  HTRouteEventManager.h
//  CocoaAsyncSocket
//
//  Created by hublot on 2020/10/21.
//

#import <Foundation/Foundation.h>
#import <React/RCTEventEmitter.h>

NS_ASSUME_NONNULL_BEGIN

RCT_EXTERN NSString *const HTRouteEventNotificationKey;

@interface HTRouteEventManager : RCTEventEmitter <RCTBridgeModule>

@end

NS_ASSUME_NONNULL_END
