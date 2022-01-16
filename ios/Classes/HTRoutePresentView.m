//
//  HTRoutePresentView.m
//  CocoaAsyncSocket
//
//  Created by hublot on 2020/9/18.
//

#import "HTRoutePresentView.h"
#import <React/RCTRootContentView.h>

@implementation HTRoutePresentView

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *view = [super hitTest:point withEvent:event];
    if (view == self) {
        return nil;
    }
    if ([view isKindOfClass:[RCTRootContentView class]]) {
        return nil;
    }
    return view;
}

@end
