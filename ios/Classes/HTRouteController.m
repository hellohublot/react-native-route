//
//  HTRouteController.m
//  ReactNativeProject
//
//  Created by hublot on 2020/9/17.
//

#import "HTRouteController.h"
#import <React/RCTRootView.h>
#import <React/RCTRootViewDelegate.h>
#import <React/RCTViewManager.h>
#import <React/RCTUIManager.h>
#import "HTRouteBridgeManager.h"
#import <UINavigationController+FDFullscreenPopGesture.h>
#import "NSDictionary+HTDictionaryPlaceholder.h"
#import "HTRouteEventManager.h"

NSInteger idCount = 0;

@interface HTRouteController ()

@property (nonatomic, strong) RCTRootView *rootView;

@property (nonatomic, strong) UIActivityIndicatorView *indicatorView;

@property (nonatomic, assign) BOOL isSecondAppear;

@end

@implementation HTRouteController

+ (instancetype)controllerWithComponentName:(NSString *)componentName componentRouteOptionList:(NSDictionary *)componentRouteOptionList {
    HTRouteController *routeController = [[HTRouteController alloc] init];
    routeController.componentName = componentName ?: @"";
    NSDictionary *_componentRouteOptionList = componentRouteOptionList ?: @{};
    NSMutableDictionary *reloadComponentRouteOptionList = [_componentRouteOptionList mutableCopy];
    routeController.componentRouteOptionList = reloadComponentRouteOptionList;
    return routeController;
}

- (void)reloadView {
    if (self.rootView) {
        return;
    }
    HTRouteBridgeManager *manager = [HTRouteBridgeManager shareManager];

    NSMutableDictionary *initialProperties = [@{} mutableCopy];
    [initialProperties setValue:self.componentName forKey:NSStringFromSelector(@selector(componentName))];
    [initialProperties setValue:self.componentPropList forKey:NSStringFromSelector(@selector(componentPropList))];
    [initialProperties setValue:self.componentRouteOptionList forKey:NSStringFromSelector(@selector(componentRouteOptionList))];

    self.rootView = [[RCTRootView alloc] initWithBridge:manager.bridge moduleName:manager.moduleName initialProperties:initialProperties];
    self.rootView.backgroundColor = [UIColor clearColor];
    self.rootView.frame = self.view.bounds;
    [self.view addSubview:self.rootView];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.automaticallyAdjustsScrollViewInsets = false;
    self.fd_prefersNavigationBarHidden = true;
    self.fd_interactivePopDisabled = ![RCTConvert BOOL:[self.componentRouteOptionList valueForKey:@"gestureEnabled" defultValue:@(true)]];
    self.fd_interactiveDistance = [RCTConvert CGFloat:[self.componentRouteOptionList valueForKey:@"gestureResponseDistance" defultValue:@(30)]];

    self.view.backgroundColor = [RCTConvert UIColor:[self.componentRouteOptionList valueForKey:@"backgroundColor"]];
    if ([RCTConvert BOOL:[self.componentRouteOptionList valueForKey:@"showLoading" defultValue:@(true)]]) {
        [self.view addSubview:self.indicatorView];
        self.indicatorView.center = self.view.center;
        [self.indicatorView startAnimating];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handlerRootViewDidAppear:) name:RCTContentDidAppearNotification object:nil];

    BOOL lazyRender = [RCTConvert CGFloat:[self.componentRouteOptionList valueForKey:@"lazyRender" defultValue:@(false)]];
    if (!lazyRender) {
        [self reloadView];
    }
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    self.rootView.frame = self.view.bounds;
}

- (void)handlerRootViewDidAppear:(NSNotification *)notification {
    if (self.rootView != notification.object) {
        return;
    }
    [self.indicatorView stopAnimating];
}


- (UIActivityIndicatorView *)indicatorView {
    if (!_indicatorView) {
        _indicatorView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
        _indicatorView.hidesWhenStopped = true;
    }
    return _indicatorView;
}

- (void)dealloc {
    [self sendNotificationWithActionName:@"dealloc" valueList:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    if (!self.isSecondAppear) {
        [self reloadView];
    }
    [self sendNotificationWithActionName:@"componentDidAppear" valueList:@{@"isSecondAppear": @(self.isSecondAppear)}];
    self.isSecondAppear = true;
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self sendNotificationWithActionName:@"componentDidDisappear" valueList:nil];
}

- (void)sendNotificationWithActionName:(NSString *)actionName valueList:(NSDictionary *)valueList {
    NSMutableDictionary *userInfo = [self.componentRouteOptionList mutableCopy];
    [userInfo setValue:self.componentName forKey:@"componentName"];
    [userInfo setValue:actionName forKey:@"actionName"];
    [userInfo addEntriesFromDictionary:valueList];
    NSNotification *notification = [NSNotification notificationWithName:HTRouteEventNotificationKey object:nil userInfo:userInfo];
    [[NSNotificationCenter defaultCenter] postNotification:notification];
}

@end
