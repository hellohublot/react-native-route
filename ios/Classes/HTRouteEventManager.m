//
//  HTRouteEventManager.m
//  CocoaAsyncSocket
//
//  Created by hublot on 2020/10/21.
//

#import "HTRouteEventManager.h"

NSString *const HTRouteEventNotificationKey = @"HTRouteEventNotificationKey";

NSString *HTRouteEventOnChangeKey = @"onHTRouteEventChange";

@implementation HTRouteEventManager

RCT_EXPORT_MODULE()

- (NSArray <NSString *> *)supportedEvents {
    return @[HTRouteEventOnChangeKey];
}

- (void)startObserving {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(routeDidReceiveNotification:) name:HTRouteEventNotificationKey object:nil];
}

- (void)stopObserving {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)routeDidReceiveNotification:(NSNotification *)notification {
    [self sendEventWithName:HTRouteEventOnChangeKey body:notification.userInfo];
}

@end
