//
//  HTRouteController.h
//  ReactNativeProject
//
//  Created by hublot on 2020/9/17.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface HTRouteController : UIViewController

@property (nonatomic, strong) NSString *componentName;

@property (nonatomic, strong) NSDictionary *componentPropList;

@property (nonatomic, strong) NSDictionary *componentRouteOptionList;

+ (instancetype)controllerWithComponentName:(NSString *)componentName componentRouteOptionList:(NSDictionary *)componentRouteOptionList;

@end

NS_ASSUME_NONNULL_END
