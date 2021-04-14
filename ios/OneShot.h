#import <React/RCTBridgeModule.h>
#import "OneShotConfig.h"

@interface OneShot : NSObject <RCTBridgeModule> {
    OneShotConfig *communication;
}
//startConfig(ssid: String, psw: String, timeoutSecond: number): Promise<any>;
//stopConfig(): void;


@end
