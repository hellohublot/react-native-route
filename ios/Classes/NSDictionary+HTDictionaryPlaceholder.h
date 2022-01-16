//
//  NSDictionary+HTDictionaryPlaceholder.h
//  ReactNativeProject
//
//  Created by hublot on 2020/9/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSDictionary (HTDictionaryPlaceholder)

- (id)valueForKey:(NSString *)key defultValue:(id)defultValue;

@end

NS_ASSUME_NONNULL_END
