import React
import CoreMotion
@objc(SsTrackingSdkRn)
class SsTrackingSdkRn: RCTEventEmitter, OnResultListener {
  private var emitterEnable = false
  override
  static func moduleName() -> String! {
      return "SsTrackingSdkRn"
  }
  override
  static func requiresMainQueueSetup() -> Bool {
      return false
  }
  func onResult(result: Any) {
      if (emitterEnable) {
          self.sendEvent(withName:"Location-Event", body: result)
      }
  }

  override func supportedEvents() -> [String]! {
      return ["Location-Event"];
  }
  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }
  @objc(configData:trackingUrl:token:driver:trackingStatus:)
  func configData(apiUrl: String,
                  trackingUrl: String,
                  token: String,
                  driver: String,
                  trackingStatus: Int) -> Void {
      SVTrackingManager.shareInstance.configData(driverName: driver, accessToken: token, trackingURL: trackingUrl, backendURL: trackingUrl, apiVersion: "2.7.2", jobStatus: trackingStatus)
  }
  
  @objc(setDeviceId:)
  func setDeviceId(id: String) -> Void {
      SVTrackingManager.shareInstance.setDeviceId(id: id)
  }
  
  @objc(startTracking)
  func startTracking() -> Void {
//        SVTrackingManager.shareInstance.configData(driverName: "Đào Trọng Thắng", accessToken: "ApxW5eO8WpD-6LTnwPQ7DG3ZXQ7Mk9f7_ud-qeKHRh3URnT_XXbC0tNpUhx_wa0HF4mqOK9jz-jdjQPyeP14sBlYK-ocERwM4bJxksXsMD3TvEK0kikm9_TKfUC7n0S5xyTNA6WV0CfNLTZdKlorXUIhnl_7WyIKBL322fTeMOmCJrl8k--NQbysgwImIG6-HwRsmqzmKqSHWq5D-bp5M_IRR6HsZNTfim_LKLZ547Sjgre1jeml7-FL6iOGZfs8LUmexLrDqXQV008zXnHczJAaCy28vuC7xvb-6drE3OCxlC9ovi6MximzZoHBvmUJprrOeMbjS6-JUZMPuPPmoFdDHwiMW_WHTcJ9tbFKfHCZi8PcKBX2IN_PnMvCXhWaUjEBc7wJ8MZ2mdXkZm25kqsJULrO-XV324qysQyuRX4Fe2I1WJm1Eq70hMiYGS092vgqClkSOceZ-wSh8HmWYNrIu2s", trackingURL: "https://dev.dcms.gisx.vn", backendURL: "https://dev.dcms.gisx.vn", apiVersion: "2.7.2", jobStatus: 2)
      SVTrackingManager.shareInstance.setListener(listener: self)
      SVTrackingManager.shareInstance.enableService()
  }
  
  @objc(stopTracking)
  func stopTracking() -> Void {
      SVTrackingManager.shareInstance.disableService()
  }
  
  @objc(setTrackingStatus:)
  func setTrackingStatus(jobStatus: NSInteger) -> Void {
      SVTrackingManager.shareInstance.setTrackingStatus(jobStatus: jobStatus)
  }
  
  @objc(setTrackingFrequency:)
  func setTrackingFrequency(miliseconds: Int) -> Void {
      SVTrackingManager.shareInstance.setTrackingFrequency(miliseconds: miliseconds)
  }
  
  @objc(setAuthenInfo:refreshToken:expiresIn:)
  func setAuthenInfo(url: String, refreshToken: String, expiresIn: String) -> Void {
      SVTrackingManager.shareInstance.setAuthenInfo(url: url, refreshToken: refreshToken, expiresIn: expiresIn)
  }

  @objc(enableEmitter)
  func enableEmitter() -> Void {
      emitterEnable = true
  }
  
  @objc(disableEmitter)
  func disableEmitter() -> Void {
      emitterEnable = false
  }
  
  @objc(isTrackingSdk:withRejecter:)
  func isTrackingSdk(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
      resolve(SVTrackingManager.shareInstance.isTracking())
  }
  
  @objc(setUseMotionSensor:)
  func setUseMotionSensor(enable: Bool) -> Void {
      SVTrackingManager.shareInstance.setUseMotionSensor(enable: enable)
  }
  
  @objc(requestPermissionAndStartMotionActivitySensor)
  func requestPermissionAndStartMotionActivitySensor() -> Void {
      SVTrackingManager.shareInstance.startMotionActivitySensor()
  }
  
  @available(iOS 11.0, *)
  @objc(motionActivityAuthorizationStatus:withRejecter:)
  func motionActivityAuthorizationStatus(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
      if (!CMMotionActivityManager.isActivityAvailable()) {
          resolve("granted");
      }
      else {
          let auth = CMMotionActivityManager.authorizationStatus()
          var retString : String = "unknown"
          switch auth {
              case CMAuthorizationStatus.authorized:
                  retString = "granted"
              case CMAuthorizationStatus.denied:
                  retString = "denied"
              case CMAuthorizationStatus.restricted:
                  retString = "restricted"
              case CMAuthorizationStatus.notDetermined:
                  retString = "notDetermined"
              default:
                  retString = "unknown"
          }
          resolve(retString);
      }
  }
  @objc(getLastKnownLocation:withRejecter:)
  func getLastKnownLocation(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
      let locationManager = CLLocationManager()
      let loc = locationManager.location
      if (loc != nil) {
          let tmp = String(format: "{ \"lat\": %f, \"lng\": %f }", loc!.coordinate.latitude, loc!.coordinate.longitude)
          resolve(tmp)
      } else {
          resolve("")
      }
  }
  
  @objc(sendOfflineWP:url:)
  func sendOfflineWP(user: String, url: String) -> Void {
      SVTrackingManager.shareInstance.sendOfflineWP()
  }
  
  @objc(getMetadata:withRejecter:)
  func getMetadata(resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
      let metaString = SVTrackingManager.shareInstance.getMetadata()
      resolve(metaString)
  }
}
