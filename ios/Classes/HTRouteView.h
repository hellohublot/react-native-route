//
//  HTRouteView.h
//  CocoaAsyncSocket
//
//  Created by hublot on 2020/9/24.
//

#import <UIKit/UIKit.h>
#import <React/RCTView.h>

@interface HTRouteView : RCTView

@property (nonatomic, strong) NSDictionary *routeData;

@property (nonatomic, strong) UITapGestureRecognizer *routeDataGesture;

@end
