//
//  HTRouteBridgeManager.h
//  ReactNativeProject
//
//  Created by hublot on 2020/9/17.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridge.h>

NS_ASSUME_NONNULL_BEGIN

@interface HTRouteBridgeManager : NSObject

@property (nonatomic, strong) RCTBridge *bridge;

@property (nonatomic, strong) NSURL *url;

@property (nonatomic, strong) NSString *moduleName;


+ (instancetype)shareManager;

+ (void)loadBridgeWithURL:(NSURL *)url moduleName:(NSString *)moduleName launchOptions:(NSDictionary *)launchOptions;

@end

NS_ASSUME_NONNULL_END
