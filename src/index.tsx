import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'ss-tracking-sdk-rn' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const SsTrackingSdkRn = NativeModules.SsTrackingSdkRn
  ? NativeModules.SsTrackingSdkRn
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return SsTrackingSdkRn.multiply(a, b);
}
export function initTracking(
  apiUrl: String,
  trackingUri: String,
  token: String,
  driver: String,
  trackingStatus: number,
  androidTitle?: String,
  androiContent?: String,
  useActivityRegconition?: Boolean
): void {
  const trackingUrl = trackingUri.startsWith('https://')
    ? trackingUri
    : apiUrl + '' + trackingUri;
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.init();
    SsTrackingSdkRn.setApiUrl(trackingUrl);
    SsTrackingSdkRn.setToken(token);
    SsTrackingSdkRn.setTrackingDriver(driver);
    SsTrackingSdkRn.setTrackingStatus(trackingStatus);
    SsTrackingSdkRn.setLocationUpdateFrequency(60000);
    SsTrackingSdkRn.setTrackingInterval(300000);
    SsTrackingSdkRn.setTitleAndContentTracking(androidTitle, androiContent);
    SsTrackingSdkRn.setUseActivityRegconition(useActivityRegconition === true);
  } else {
    SsTrackingSdkRn.configData(
      apiUrl,
      trackingUrl,
      token,
      driver,
      trackingStatus
    );
    SsTrackingSdkRn.setTrackingStatus(trackingStatus);
    SsTrackingSdkRn.setUseMotionSensor(useActivityRegconition === true);
  }
}

export function setAuthenInfo(
  authenURL: string,
  refreshToken: string,
  expiredIn: string
): void {
  SsTrackingSdkRn.setAuthenInfo(authenURL, refreshToken, expiredIn);
}

export function setDutySession(session: number): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setSession(session + '');
  }
}

export function setTrackingFrequency(miliseconds: number): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setTrackingInterval(miliseconds);
  } else {
    SsTrackingSdkRn.setTrackingFrequency(miliseconds);
  }
}

export function setLocationUpdateFrequency(miliseconds: number): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setLocationUpdateFrequency(miliseconds);
  } else {
  }
}

export function setLocationDistanceFilter(miliseconds: number): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setLocationDistanceFilter(miliseconds);
  } else {
  }
}

export function setLocationFastestInterval(miliseconds: number): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setLocationFastestInterval(miliseconds);
  } else {
  }
}

export function setLocationMaxWaitTime(miliseconds: number): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setLocationMaxWaitTime(miliseconds);
  } else {
  }
}

// export function setCurrentActivityAndTracking(): void {
//   if (Platform.OS === 'android') {
//     SsTrackingSdkRn.setCurrentActivityAndTracking();
//   }
// }

export function startTracking(): void {
  SsTrackingSdkRn.startTracking();
}

export function stopTracking(): void {
  SsTrackingSdkRn.stopTracking();
}

export function removeTracking(): void {
  SsTrackingSdkRn.removeTracking();
}

export function setTitleAndContentTracking(
  newTitle: String,
  newContent: String
): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setTitleAndContentTracking(newTitle, newContent);
  }
}

export function isTrackingSdk(): Promise<boolean> {
  return SsTrackingSdkRn.isTrackingSdk();
}

// export function setTrackingDriver(driver: String): void {
//   SsTrackingSdkRn.setTrackingDriver(driver);
// }

export function setTrackingStatus(status: number): void {
  SsTrackingSdkRn.setTrackingStatus(status);
}

export function setTrackerId(trackerId: String): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.setTrackerId(trackerId);
  } else {
    SsTrackingSdkRn.setDeviceId(trackerId);
  }
}

export function setEmitEvent(enable: boolean): void {
  if (Platform.OS === 'android') {
  } else {
    if (enable) {
      SsTrackingSdkRn.enableEmitter();
    } else {
      SsTrackingSdkRn.disableEmitter();
    }
  }
}

export function sendLocation(lat: number, lng: number) {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.sendLocation(lat, lng);
  }
}

export function isIgnoreBatteryOptimization(): boolean {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.isIgnoreBatteryOptimization();
  }
  return false;
}

export function requestIgnoreBatteryOptimization(): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.requestIgnoreBatteryOptimization();
  }
}

export function requestPermissionMotionActivity(): void {
  if (Platform.OS === 'ios') {
    SsTrackingSdkRn.requestPermissionAndStartMotionActivitySensor();
  }
}

export function motionActivityAuthorizationStatus(): Promise<String> {
  if (Platform.OS === 'ios') {
    return SsTrackingSdkRn.motionActivityAuthorizationStatus();
  }
  return Promise.resolve('');
}

export function checkIsAutoStartAvailable(): Promise<boolean> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.isAutoStartAvailable();
  } else {
    return Promise.resolve(false);
  }
}

export function checkPowerSaveModeXiaomi(): Promise<boolean> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.isPowerSaveModeXiaomi();
  } else {
    return Promise.resolve(false);
  }
}

export function openPowerSaveModeXiaomi() {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.openPowerSaveModeXiaomi();
  }
}

export function requestAutoStartPermission(): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.requestAutoStartPermission();
  }
}

export function openSettingFor(action: string): void {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.openSetting(action);
  }
}

export function checkLocationServiceEnable(): Promise<boolean> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.isGPSEnabled();
  } else {
    return Promise.resolve(true);
  }
}

export function checkWifiEnable(): Promise<boolean> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.isWifiEnabled();
  } else {
    return Promise.resolve(true);
  }
}

export function getLastLocation(): Promise<string> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.getLastLocation();
  } else {
    return SsTrackingSdkRn.getLastKnownLocation();
  }
}

export function isTrackingStoppedUnexpectedly(): Promise<boolean> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.isTrackingStoppedUnexpectedly();
  }
  return Promise.resolve(false);
}

export function getRefreshToken(): Promise<string> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.getRefreshToken();
  }
  return Promise.resolve('');
}

export function setRefreshToken(value: string | null): Promise<string> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.setRefreshToken(value);
  }
  return Promise.resolve('');
}

export function setToken(value: string | null): Promise<string | null> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.setToken(value);
  }
  return Promise.resolve('');
}

export function checkStoragePermissionsSDK33(): Promise<number> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.checkStoragePermissionsSDK33();
  }
  return Promise.resolve(1);
}

export function checkStoragePermissionsNoAudioSDK33(): Promise<number> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.checkStoragePermissionsNoAudioSDK33();
  }
  return Promise.resolve(1);
}

export function requestStoragePermissionsSDK33() {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.requestStoragePermissionsSDK33();
  }
}

export function shouldShowRequestPermissionRationale(
  permission: string
): Promise<boolean> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.shouldShowRequestPermissionRationale(permission);
  }
  return Promise.resolve(false);
}

export function checkPermission(permission: string): Promise<number> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.checkPermission(permission);
  }
  return Promise.resolve(1);
}

export function requestPermissions(permissions: string[]) {
  if (Platform.OS === 'android') {
    try {
      SsTrackingSdkRn.requestPermissions(permissions);
    } catch {}
  }
}

export function getLastTimeUpdate() {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.getLastTimeUpdate();
  } else {
    return Promise.resolve();
  }
}

export function getEventMetaData(): Promise<string> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.getEventMetaData();
  } else {
    return Promise.resolve('');
  }
}

export function getEMetaData(): Promise<string> {
  if (Platform.OS === 'android') {
    return SsTrackingSdkRn.getEMetaData();
  } else {
    return SsTrackingSdkRn.getMetadata();
  }
}

export function sendOfflineWP(user: string, url: string) {
  if (Platform.OS === 'android') {
    SsTrackingSdkRn.pushOfflineData(user, url);
  } else {
    SsTrackingSdkRn.sendOfflineWP(user, url);
  }
}
