#import "AppDelegate.h"

#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#import <React/RCTRootView.h>

#ifdef FB_SONARKIT_ENABLED
#import <FlipperKit/FlipperClient.h>
#import <FlipperKitLayoutPlugin/FlipperKitLayoutPlugin.h>
#import <FlipperKitUserDefaultsPlugin/FKUserDefaultsPlugin.h>
#import <FlipperKitNetworkPlugin/FlipperKitNetworkPlugin.h>
#import <SKIOSNetworkPlugin/SKIOSNetworkAdapter.h>
#import <FlipperKitReactPlugin/FlipperKitReactPlugin.h>

#import <RNReactNativeRoute/HTRouteBridgeManager.h>
#import <RNReactNativeRoute/HTRouteController.h>
#import <RNReactNativeRoute/UINavigationController+FDFullscreenPopGesture.h>
#import <React/RCTConvert.h>

static void InitializeFlipper(UIApplication *application) {
  FlipperClient *client = [FlipperClient sharedClient];
  SKDescriptorMapper *layoutDescriptorMapper = [[SKDescriptorMapper alloc] initWithDefaults];
  [client addPlugin:[[FlipperKitLayoutPlugin alloc] initWithRootNode:application withDescriptorMapper:layoutDescriptorMapper]];
  [client addPlugin:[[FKUserDefaultsPlugin alloc] initWithSuiteName:nil]];
  [client addPlugin:[FlipperKitReactPlugin new]];
  [client addPlugin:[[FlipperKitNetworkPlugin alloc] initWithNetworkAdapter:[SKIOSNetworkAdapter new]]];
  [client start];
}
#endif

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
#ifdef FB_SONARKIT_ENABLED
  InitializeFlipper(application);
#endif

  [HTRouteBridgeManager loadBridgeWithURL:[self sourceURLForBridge:nil] moduleName:@"ReactNativeDemo" launchOptions:launchOptions];

  UITabBarController *tabBarController = [[UITabBarController alloc] init];
  UIColor *tintColor = [RCTConvert UIColor:@(0xFF383C46)];
  tabBarController.tabBar.backgroundColor = [UIColor whiteColor];
  tabBarController.tabBar.tintColor = tintColor;
  tabBarController.tabBar.translucent = true;

  NSString *titleKey = @"title";
  NSString *imageKey = @"image";
  NSString *selectedImageKey = @"selectedImageKey";
  NSString *componentKey = @"component";
  NSArray *keyValueList = @[
    @{ titleKey: @"Home", imageKey: @"tabbar_home", selectedImageKey: @"tabbar_home_selected", componentKey: @"Home" },
    @{ titleKey: @"Mine", imageKey: @"tabbar_mine", selectedImageKey: @"tabbar_mine_selected", componentKey: @"Mine" },
  ];
  [keyValueList enumerateObjectsUsingBlock:^(NSDictionary *dictionary, NSUInteger index, BOOL * _Nonnull stop) {
    HTRouteController *routeController = [HTRouteController controllerWithComponentName:dictionary[componentKey] componentRouteOptionList:@{@"id": [NSString stringWithFormat:@"%ld", index]}];
    UINavigationController *navigationController = [[UINavigationController alloc] initWithRootViewController:routeController];
    navigationController.fd_viewControllerBasedNavigationBarAppearanceEnabled = false;
    [tabBarController addChildViewController:navigationController];

    routeController.tabBarItem.title = dictionary[titleKey];
    routeController.tabBarItem.image = [[UIImage imageNamed:dictionary[imageKey]] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    routeController.tabBarItem.selectedImage = [[UIImage imageNamed:dictionary[selectedImageKey]] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
    UIFont *font = [UIFont systemFontOfSize:9];
    [routeController.tabBarItem setTitleTextAttributes:@{
        NSFontAttributeName: font,
        NSForegroundColorAttributeName: [RCTConvert UIColor:@(0xFF7E828A)]
    } forState:UIControlStateNormal];
    [routeController.tabBarItem setTitleTextAttributes:@{
        NSFontAttributeName: font,
        NSForegroundColorAttributeName: tintColor
    } forState:UIControlStateSelected];

  }];

  UIViewController *rootViewController = [[UIViewController alloc] init];
  rootViewController.view.backgroundColor = [UIColor whiteColor];
  [rootViewController addChildViewController:tabBarController];
  [rootViewController.view addSubview:tabBarController.view];
  [tabBarController didMoveToParentViewController:rootViewController];

  self.window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
  self.window.rootViewController = rootViewController;
  [self.window makeKeyAndVisible];
  return YES;
}

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index" fallbackResource:nil];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

@end
