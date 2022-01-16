//
//  NSDictionary+HTDictionaryPlaceholder.m
//  ReactNativeProject
//
//  Created by hublot on 2020/9/18.
//

#import "NSDictionary+HTDictionaryPlaceholder.h"

@implementation NSDictionary (HTDictionaryPlaceholder)

- (id)valueForKey:(NSString *)key defultValue:(id)defultValue {
    id value = [self valueForKey:key];
    if (value == nil || value == NULL) {
        return defultValue;
    }
    return value;
}

@end
