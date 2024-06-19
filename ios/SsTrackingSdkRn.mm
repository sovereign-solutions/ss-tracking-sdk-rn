#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(SsTrackingSdkRn, NSObject)

RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}
- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}
RCT_EXTERN_METHOD(configData:(NSString)apiUrl trackingUrl:(NSString)trackingUrl
                  token:(NSString)token
                  driver:(NSString)driver
                  trackingStatus:(NSString)trackingStatus
                  )

RCT_EXTERN_METHOD(setDeviceId:(NSString)id)

RCT_EXTERN_METHOD(startTracking)

RCT_EXTERN_METHOD(stopTracking)

RCT_EXTERN_METHOD(setTrackingStatus:(NSInteger)jobStatus)

RCT_EXTERN_METHOD(setTrackingFrequency:(int)miliseconds)

RCT_EXTERN_METHOD(setAuthenInfo:(NSString)url refreshToken:(NSString)refreshToken expiresIn:(NSString)expiresIn)

RCT_EXTERN_METHOD(enableEmitter)

RCT_EXTERN_METHOD(disableEmitter)

RCT_EXTERN_METHOD(
                  isTrackingSdk:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(setUseMotionSensor:(BOOL)enable)

RCT_EXTERN_METHOD(requestPermissionAndStartMotionActivitySensor)

RCT_EXTERN_METHOD(motionActivityAuthorizationStatus:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getLastKnownLocation:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(sendOfflineWP:(NSString)user url:(NSString)url)

RCT_EXTERN_METHOD(getMetadata:(RCTPromiseResolveBlock)resolve
                  withRejecter:(RCTPromiseRejectBlock)reject)
@end
