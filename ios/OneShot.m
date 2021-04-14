#import "OneShot.h"
#import <SystemConfiguration/CaptiveNetwork.h>

@interface TaskInfo : NSObject
@property(nonatomic, copy) NSString *ssid;
@property(nonatomic, copy) NSString *password;
@end

@implementation TaskInfo

- (id)initWithParams:(NSString *)ssidName andPwd:(NSString *)pwd {
    self = [super init];
    if (self) {
        _ssid = ssidName;
        _password = pwd;
    }
    return self;
}

@end

@implementation OneShot

NSThread *smartConfigThread;
RCTPromiseRejectBlock currentReject;

RCT_EXPORT_MODULE()


//startConfig(ssid: String, psw: String, timeoutSecond: number): Promise<any>;
RCT_REMAP_METHOD(startConfig, startConfigWithSSID:
    (nonnull NSString*)ssid withPassword:(nonnull NSString*)password withTimeoutSecond:(nonnull NSNumber*)timeoutSecond withResolver:(RCTPromiseResolveBlock)resolve
        withRejecter:(RCTPromiseRejectBlock)reject) {
    communication = [[OneShotConfig alloc] init];
    currentReject = reject;
    TaskInfo *ti = [[TaskInfo alloc] initWithParams:ssid andPwd:password];
    smartConfigThread = [[NSThread alloc] initWithTarget:self selector:@selector(sendData:) object:ti];
    [smartConfigThread start];
    //option1 : use timer
    if (![NSThread isMainThread]){
        dispatch_async(dispatch_get_main_queue(), ^{
            [self startTimeoutTimer:[timeoutSecond doubleValue]];
        });
    }
    //option1 : just user perform function in current object
//    [self performSelector:@selector(afterTimout) withObject:nil afterDelay:[timeoutSecond doubleValue]];
}

- (void)startTimeoutTimer:(double)timeoutSecond {
    [NSTimer scheduledTimerWithTimeInterval:timeoutSecond
                                     target:self
                                   selector:@selector(afterTimout:)
                                   userInfo:nil repeats:NO];
}

- (void)afterTimout:(NSTimer *)timer {
    if (smartConfigThread != nil) {
        //represent smart-config is running
        [smartConfigThread cancel];
        smartConfigThread = nil;
        currentReject([NSString stringWithFormat:@"%d", 104], @"配对失败：连接超时", nil);
        [timer invalidate];
    }
}

- (void)sendData:(TaskInfo *)ti {
    while (1) {
        if ([NSThread currentThread].isCancelled) {
            [communication stopConfig];
            [NSThread exit];
        } else {
            @autoreleasepool {
                //send message to smart-config sdk for send wi-fi information package
                int status = [communication startConfig:ti.ssid pwd:ti.password];
                NSLog(@"startConfig ret %d", status);
                if (status != 0) {
                    currentReject([NSString stringWithFormat:@"%d", status], @"配对失败", nil);
                    NSLog(@"startConfig(return status code -1) ret %d", status);
                    [communication stopConfig];
                    break;
                } else {
                    //only represent this package sent successful , but not represent smart-config successful ,so don't deal this block
//                [communication stopConfig];
//                break;
                }
            }
        }
    }
}

//stopConfig(): void;
RCT_REMAP_METHOD(stopConfig, stopConfigFun) {
    if (smartConfigThread != nil) {
        [smartConfigThread cancel];
        smartConfigThread = nil;
    }
}


@end
