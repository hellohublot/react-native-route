//
//  HTRouteBridgeManager.m
//  ReactNativeProject
//
//  Created by hublot on 2020/9/17.
//

#import "HTRouteBridgeManager.h"

@interface HTRouteBridgeManager () <RCTBridgeDelegate>

@end

@implementation HTRouteBridgeManager

+ (instancetype)shareManager {
    static dispatch_once_t onceToken;
    static HTRouteBridgeManager *manager;
    dispatch_once(&onceToken, ^{
        manager = [[HTRouteBridgeManager alloc] init];
    });
    return manager;
}

+ (void)loadBridgeWithURL:(NSURL *)url moduleName:(NSString *)moduleName launchOptions:(NSDictionary *)launchOptions {
    HTRouteBridgeManager *manager = [HTRouteBridgeManager shareManager];
    manager.url = url;
    manager.moduleName = moduleName;
    RCTBridge *bridge = [[RCTBridge alloc] initWithDelegate:manager launchOptions:launchOptions];
    if (manager.bridge) {
        [manager.bridge invalidate];
    }
    manager.bridge = bridge;
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge {
    return self.url;
}

@end
