package com.sstrackingsdkrn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sovereign.trackingsdk.LocationSendCallBack;
import com.sovereign.trackingsdk.LocationUtils.LocationEngine;
import com.sovereign.trackingsdk.LocationUtils.LocationEngineCallback;
import com.sovereign.trackingsdk.LocationUtils.LocationEngineProvider;
import com.sovereign.trackingsdk.LocationUtils.LocationEngineResult;
import com.sovereign.trackingsdk.Tracking;
import com.sovereign.trackingsdk.TrackingService;
import com.sstrackingsdkreactnative.BuildConfig;

import java.util.Map;

@ReactModule(name = SsTrackingSdkRnModule.NAME)
public class SsTrackingSdkRnModule extends ReactContextBaseJavaModule {
  public static final String NAME = "SsTrackingSdkRn";
  private Tracking mTrackingSDK;
  private SharedPreferences mSharePref;
  public SsTrackingSdkRnModule(ReactApplicationContext reactContext) {
    super(reactContext);
    mSharePref = reactContext.getSharedPreferences("SDK_Permisions", Context.MODE_PRIVATE);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  public boolean setCurrentActivityAndTracking() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      mTrackingSDK = Tracking.newInstance(currentActivity);
      return true;
    }
    return false;
  }

  private long oldExpired = 0;
  private String oldUrl = "";
  private String oldRefreshToken = "";
  @ReactMethod
  public void init() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity == null) {
      return;
    }
    Tracking.Builder builder = new Tracking.Builder(currentActivity);
    builder.setDialogRequestBackgroundLocationTitle("access background location dialog title")
      .setDialogRequestBackgroundLocationMessage("access background location dialog message")
      .setNotificationActionCloseLabel("");
    oldExpired = builder.getOldTokenExpired();
    oldUrl = builder.getOldAuthenUrl();
    oldRefreshToken = builder.getOldRefreshToken();
    mTrackingSDK = builder.build();
  }

  @ReactMethod
  public void startTracking() {
    if (BuildConfig.DEBUG) {
      Log.d("mTrackingSDK", "start tracking");
    }
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }

    mTrackingSDK.setCallback(new LocationSendCallBack() {
        @Override
        public void onSendLocationResult(String resultString) {
          // Log.d("TEST", "Main " + resultString);
          getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit("Location-Event", resultString);
        }

        @Override
        public void onFailed(String errorString) {

        }

        @Override
        public void trackingStarted() {
          Map<String, ?> map = mTrackingSDK.getConfig();
          for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (BuildConfig.DEBUG) {
              Log.d("config", entry.getKey() + ":" + entry.getValue().toString());
            }
          }
        }

        @Override
        public void trackingStopped() {

        }
      })
      .startTracking();
  }
  @ReactMethod
  public void setToken(String newToken) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setAuthen(newToken);
  }

  @ReactMethod
  public void setApiUrl(String newApiUrl) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setApiUrl(newApiUrl);
  }

  @ReactMethod
  public void setTitleAndContentTracking(String newTitle, String newContent) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setNotificationContentTitle(newTitle);
    mTrackingSDK.setNotificationContentText(newContent);
  }

  @ReactMethod
  public void setLocationUpdateFrequency(int miliseconds) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setLocationUpdateFrequency(miliseconds);
  }

  @ReactMethod
  public void setTrackingInterval(int miliseconds) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setTrackingInterval(miliseconds);
  }

  @ReactMethod
  public void setLocationDistanceFilter(int meters) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setLocationDistanceFilter(meters);
  }
  @ReactMethod
  public void setLocationFastestInterval(int miliseconds) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setLocationFastestInterval(miliseconds);
  }
  @ReactMethod
  public void setLocationMaxWaitTime(int miliseconds) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setLocationMaxWaitTime(miliseconds);
  }
  @ReactMethod
  public void isTrackingStoppedUnexpectedly(Promise promise) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    if (mTrackingSDK.isUserOnDuty()) {
      long lastPing = mTrackingSDK.getLastPingTimeStamp();
      promise.resolve(!mTrackingSDK.isTracking() && System.currentTimeMillis() - lastPing > 15 * 60 * 1000);
    } else {
      promise.resolve(false);
    }
  }
  @ReactMethod
  public void isTrackingSdk(Promise promise) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    promise.resolve(mTrackingSDK.isTracking());
  }
  @ReactMethod
  public void getEventMetaData(Promise promise) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    promise.resolve(mTrackingSDK.getEventMetaData());
  }

  @ReactMethod
  public void getEMetaData(Promise promise) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    promise.resolve(mTrackingSDK.getMetaData());
  }

  @ReactMethod
  public void stopTracking() {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        ReactApplicationContext context = getReactApplicationContext();
        if (context != null) {
          context.stopService(new Intent(context, TrackingService.class));
          context.getSharedPreferences("pref_tracking", Context.MODE_PRIVATE).edit().putBoolean(TrackingService.TRACKING_STATS, false).apply();
        }
        return;
      }
    }
    if (mTrackingSDK != null) {
      mTrackingSDK.stopTracking();
    }
  }
  @ReactMethod
  public void setTrackingDriver(String driver) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setTrackingDriver(driver);
  }

  @ReactMethod
  public void setTrackerId(String trackerId) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setTrackerId(trackerId);
  }

  @ReactMethod
  public void setTrackingStatus(int status) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setTrackingStatus(status);
  }

  @ReactMethod
  public void setAuthenURL(String url) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setAuthenURL(url);
  }

  @ReactMethod
  public void setRefreshToken(String refreshToken) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setRefreshToken(refreshToken);
  }

  @ReactMethod
  public void getRefreshToken(Promise promise) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    promise.resolve(mTrackingSDK.getRefreshToken());
  }

  @ReactMethod
  public void setTokenExpired(String expired) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    long tmp = 0;
    try {
      tmp = Long.parseLong(expired);
    } catch (Exception e) {

    }
    mTrackingSDK.setTokenExpired(tmp);
  }

  @ReactMethod
  public void setSession(String session) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    long tmp = 0;
    try {
      tmp = Long.parseLong(session);
    } catch (Exception e) {

    }
    mTrackingSDK.setSession(tmp);
  }

  @ReactMethod
  public void setAuthenInfo(String url, String refreshToken, String expired) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    long exp = 0;
    try {
      exp = Long.parseLong(expired);
    } catch (Exception e) {
    }
    if (TextUtils.isEmpty(oldUrl) || TextUtils.isEmpty(oldRefreshToken)) {
      mTrackingSDK.setTokenExpired(exp);
      mTrackingSDK.setAuthenURL(url);
      mTrackingSDK.setRefreshToken(refreshToken);
    } else {
      if (oldUrl.equals(url)) {
        if (exp >= oldExpired) {
          mTrackingSDK.setAuthenURL(url);
          mTrackingSDK.setTokenExpired(exp);
          mTrackingSDK.setRefreshToken(refreshToken);
        } else {
          mTrackingSDK.setAuthenURL(url);
          mTrackingSDK.setTokenExpired(oldExpired);
          mTrackingSDK.setRefreshToken(oldRefreshToken);
        }
      } else {
        mTrackingSDK.setTokenExpired(exp);
        mTrackingSDK.setAuthenURL(url);
        mTrackingSDK.setRefreshToken(refreshToken);
      }
    }
  }
  @ReactMethod
  public void isIgnoreBatteryOptimization(Promise promise) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Activity activity = getCurrentActivity();
      if (activity == null) {
        return;
      }
      PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
      boolean isIgnored = pm.isIgnoringBatteryOptimizations(activity.getPackageName());
      promise.resolve(isIgnored);
    }
    promise.resolve(true);
  }

  @ReactMethod
  public void requestIgnoreBatteryOptimization() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Intent intent = new Intent();
      Activity activity = getCurrentActivity();
      if (activity == null) {
        return;
      }
//            PowerManager pm = (PowerManager) activity.getSystemService(POWER_SERVICE);
//            if (!pm.isIgnoringBatteryOptimizations(activity.getPackageName())) {
      intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
      intent.setData(Uri.parse("package:" + activity.getPackageName()));
      activity.startActivity(intent);
//            }
    }
  }

  @ReactMethod
  public void setUseMotionSensor(boolean enabled) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setUseMotionSensor(enabled);
  }

  @ReactMethod
  public void setUseActivityRegconition(boolean enabled) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.setUseActivityRegconition(enabled);
    // if (enabled) {
    //     requestARPermission();
    // }
  }
  private void requestARPermission() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      ActivityCompat.requestPermissions(
        getCurrentActivity(),
        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
        1);
    }
  }

  private static final Intent[] POWERMANAGER_INTENTS = {
    new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
    new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
    new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
    new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
    new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
    new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
    new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
    new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
    new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
    new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
    new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
//        new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
//        new Intent().setComponent(new ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
    new Intent().setComponent(new ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
    new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity")),
    new Intent().setComponent(new ComponentName("com.transsion.phonemanager", "com.itel.autobootmanager.activity.AutoBootMgrActivity"))
  };

  private void requestAutoStartOppo(Activity activity) {

    //com.coloros.safecenter.permission.singlepage.PermissionSinglePageActivity     listpermissions
    //com.coloros.privacypermissionsentry.PermissionTopActivity                     privacypermissions
    // getPackageManager().getLaunchIntentForPackage("com.coloros.safecenter");

    try {
      activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.FakeActivity")));
    } catch (Exception e) {
      try {
        activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.StartupAppListActivity")));
      } catch (Exception e1) {
        try {
          activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.StartupAppListActivity")));
        } catch (Exception e2) {
          try {
            activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startup.StartupAppListActivity")));
          } catch (Exception e3) {
            try {
              activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupapp.StartupAppListActivity")));
            } catch (Exception e4) {
              try {
                activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safe", "com.coloros.safe.permission.startupmanager.StartupAppListActivity")));
              } catch (Exception e5) {
                try {
                  activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startsettings")));
                } catch (Exception e6) {
                  try {
                    activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupapp.startupmanager")));
                  } catch (Exception e7) {
                    try {
                      activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startupmanager.startupActivity")));
                    } catch (Exception e8) {
                      try {
                        activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.startupapp.startupmanager")));
                      } catch (Exception e9) {
                        try {
                          activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity.Startupmanager")));
                        } catch (Exception e10) {
                          try {
                            activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.privacypermissionsentry.PermissionTopActivity")));
                          } catch (Exception e11) {
                            try {
                              activity.startActivity(new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.FakeActivity")));
                            } catch (Exception e12) {
                              e12.printStackTrace();
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  @ReactMethod
  public void requestAutoStartPermission() {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      if (Build.MANUFACTURER.equals("OPPO")) {
        requestAutoStartOppo(currentActivity);
      }
      else {
        for (Intent intent : POWERMANAGER_INTENTS) {
          if (currentActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
              currentActivity.startActivity(intent);
              break;
            } catch (Exception e) {
            }
          }
        }
      }
    }
  }
  @ReactMethod
  public void openPowerSaveModeXiaomi() {
    try {
      Activity currentActivity = getCurrentActivity();
      Intent intent = new Intent();
      intent.setComponent(new ComponentName(
        "com.miui.securitycenter",
        "com.miui.powercenter.savemode.PowerSaveActivity"));
      currentActivity.startActivityForResult(intent, 0);
    } catch (ActivityNotFoundException anfe) {
    }
  }

  @ReactMethod
  public void isPowerSaveModeXiaomi(Promise promise) {
    if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
      Activity currentActivity = getCurrentActivity();
      if (currentActivity != null) {
        try {
          int value = android.provider.Settings.System.getInt(currentActivity.getContentResolver(), "POWER_SAVE_MODE_OPEN");
          if(value == 0){
            promise.resolve(false);
          }else {
            promise.resolve(true);
          }
        } catch (Settings.SettingNotFoundException e) {
          promise.reject("setting_not_found", "Setting not found");
          Log.d("Pin mode value:", "Error");
        } catch (SecurityException e) {
          promise.reject("security_exception", "Security exception");
          Log.d("Pin mode value:", "Security Exception");
        }
      } else {
        promise.reject("activity_null", "Current activity is null");
        Log.d("Pin mode value:", "Current activity is null");
      }
    } else {
      promise.reject("not_xiaomi_device", "Not a Xiaomi device");
      Log.d("Pin mode value:", "Not a Xiaomi device");
    }
  }
  @ReactMethod
  public void isAutoStartAvailable(Promise promise) {
    Activity currentActivity = getCurrentActivity();
    boolean avail = false;
    if (currentActivity != null) {
      for (Intent intent : POWERMANAGER_INTENTS) {
        if (currentActivity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
          avail = true;
          break;
        }
      }
    }
    promise.resolve(avail);
  }
  @ReactMethod
  public void openSetting(String action) {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      currentActivity.startActivity(new Intent(action));
    }
  }

  @ReactMethod
  public void isGPSEnabled(Promise promise)
  {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      LocationManager lm = (LocationManager)
        currentActivity.getSystemService(Context.LOCATION_SERVICE);
      promise.resolve(lm.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }
    promise.resolve(false);
  }

  @ReactMethod
  public void isWifiEnabled(Promise promise)
  {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      WifiManager wifi = (WifiManager) currentActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
      promise.resolve(wifi.isWifiEnabled());
    }
    promise.resolve(false);
  }
  @SuppressLint("MissingPermission")
  @ReactMethod
  public void getLastLocation(Promise promise)
  {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(currentActivity);
      locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
        @Override
        public void onSuccess(LocationEngineResult result) {
          Location loc = result.getLastLocation();
          if (loc != null) {
            String tmp = "{ \"lat\": " + loc.getLatitude() + ", \"lng\": " + loc.getLongitude() + "}";
            promise.resolve(tmp);
          } else {
            promise.resolve("");
          }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
          promise.resolve("");
        }
      });
    }
  }

  @ReactMethod
  public void checkStoragePermissionsSDK33(Promise promise)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Activity currentActivity = getCurrentActivity();
      if (currentActivity != null) {
        int result = 1;
        if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
          result = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_MEDIA_IMAGES) ? 0 : -1;
          promise.resolve(result);
        } else if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
          result = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_MEDIA_VIDEO) ? 0 : -1;
          promise.resolve(result);
        } else if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
          result = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_MEDIA_AUDIO) ? 0 : -1;
          promise.resolve(result);
        }
        promise.resolve(result);
//                promise.resolve(ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED);
      }
    }
    promise.resolve(1);
  }

  @ReactMethod
  public void checkStoragePermissionsNoAudioSDK33(Promise promise)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Activity currentActivity = getCurrentActivity();
      if (currentActivity != null) {
        int result = 1;
        if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
          result = 0;
          if (mSharePref.getBoolean(Manifest.permission.READ_MEDIA_IMAGES, false)) {
            result = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_MEDIA_IMAGES) ? 0 : -1;
          }
          promise.resolve(result);
        } else if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
          result = 0;
          if (mSharePref.getBoolean(Manifest.permission.READ_MEDIA_VIDEO, false)) {
            result = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_MEDIA_VIDEO) ? 0 : -1;
          }
          promise.resolve(result);
        }  else if (ActivityCompat.checkSelfPermission(currentActivity, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, Manifest.permission.READ_MEDIA_AUDIO);
        }
        promise.resolve(result);
      }
    }
    promise.resolve(1);
  }

  @ReactMethod
  public void requestStoragePermissionsSDK33()
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Activity currentActivity = getCurrentActivity();
      if (currentActivity != null) {
        mSharePref.edit().putBoolean(Manifest.permission.READ_MEDIA_IMAGES, true)
          .putBoolean(Manifest.permission.READ_MEDIA_VIDEO, true)
          .putBoolean(Manifest.permission.READ_MEDIA_AUDIO, true)
          .apply();
        ActivityCompat.requestPermissions(currentActivity, new String[] {
          Manifest.permission.READ_MEDIA_IMAGES,
          Manifest.permission.READ_MEDIA_VIDEO,
          Manifest.permission.READ_MEDIA_AUDIO
        },123);
      }
    }
  }

  @ReactMethod
  public void shouldShowRequestPermissionRationale(String permission, Promise promise) {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      promise.resolve(ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, permission));
    }
    promise.resolve(false);
  }

  @ReactMethod
  public void checkPermission(String permission, Promise promise)
  {
    Activity currentActivity = getCurrentActivity();
    if (currentActivity != null) {
      int result = 1;
      if (ActivityCompat.checkSelfPermission(currentActivity, permission) != PackageManager.PERMISSION_GRANTED) {
        result = 0;
        if (mSharePref.getBoolean(permission, false)) {
          result = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, permission) ? 0 : -1;
        }
        promise.resolve(result);
      }
      promise.resolve(result);
    }
    promise.resolve(1);
  }

  @ReactMethod
  public void requestPermissions(ReadableArray permissions)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      try {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
          int size = permissions.size();
          String[] p = new String[size];
          for (int i = 0; i < size; i++) {
            p[i] = permissions.getString(i);
            mSharePref.edit().putBoolean(p[i], true).apply();
          }
          ActivityCompat.requestPermissions(currentActivity, p, 123);
        }
      } catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  @ReactMethod
  public void getLastTimeUpdate(Promise promise) {
    String timestamp = String.valueOf(BuildConfig.TIMESTAMP);
    promise.resolve(timestamp);
  }

  @ReactMethod
  public void pushOfflineData(String userName, String trackingurl) {
    if (mTrackingSDK == null) {
      if (!setCurrentActivityAndTracking()) {
        return;
      }
    }
    mTrackingSDK.pushOfflineData(userName, trackingurl);
  }
}
