# ss-tracking-sdk-rn

tracking

## Installation
add this line in package.json dependencies use `yarn` command to fetch the package:
```
"ss-tracking-sdk-rn": "git://github.com/sovereign-solutions/ss-tracking-sdk-rn.git"`
```
### Android
In file /app/build.gradle under dependencies add:
```
implementation files('libs/trackingsdk-release.aar')
implementation 'com.google.android.gms:play-services-location:20.0.0'
implementation 'androidx.room:room-runtime:2.3.0'
implementation 'com.google.code.gson:gson:2.8.6'
```

Open AndroidManifest.xml and add this under Application tag:
```
<service android:name="com.sovereign.trackingsdk.TrackingService"
    android:foregroundServiceType="location"/>
<service android:name="com.sovereign.trackingsdk.ARIntentService"/>
```

and some permissions:
```
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

### iOS
In your info.plist add UIBackgroundModes location and these keys and descriptions: NSLocationWhenInUseUsageDescription, NSLocationAlwaysAndWhenInUseUsageDescription, NSLocationAlwaysUsageDescription
add this line in Podfile
```
pod 'ss-tracking-sdk-rn', :path => "../node_modules/ss-tracking-sdk-rn"
```

### user and token.
use the LoginUrl with your username and password to get the token, refreshToken 
```
curl --location 'https://accounts.skedulomatic.com/oauth/token'
--header 'Content-Type: application/x-www-form-urlencoded'
--data-urlencode 'grant_type=password'
--data-urlencode 'username='
--data-urlencode 'password=
```

## Usage

```js
import {
  initTracking,
  isTrackingSdk,
  setAuthenInfo,
  setLocationDistanceFilter,
  setLocationUpdateFrequency,
  setTrackerId,
  setTrackingFrequency,
  startTracking,
  stopTracking,
} from 'ss-tracking-sdk-rn';

// ...
export default function App() {
  const [trackingRunning, setIsTracking] = React.useState<boolean>();

  React.useEffect(() => {
    initialTrackingSDK();
    getIsTracking();
  }, []);
  const getIsTracking = async () => {
    setIsTracking(await isTrackingSdk());
  };
  //init
  const initialTrackingSDK = () => {
      const username = 'sales_testing_1';
      const token = 'bearer pJDEr8Q-wfmzZVFlt6WJxqQE...';
      const refreshToken = 'dfWI_9JQNwcpNJOjwez5XBc5...';
      initTracking(
        'https://sales.grow-matic.com',
        'https://sales.grow-matic.com/api/app-base/vdms-tracking/push',
        token,
        username,
        2,
        'tracking running',
        'tracking',
        true
      );
  
      let expired = 0;
      if (expired === 0) {
        expired = Date.now();
      }
      setAuthenInfo(
        'https://accounts.skedulomatic.com/oauth/token',
        refreshToken,
        expired + ''
      );
      setTrackingFrequency(5000);
  
      setLocationUpdateFrequency(5000);
  
      setLocationDistanceFilter(0);
  
      setTrackerId(DeviceInfo.getUniqueId() + '@' + username);
    }
  // start tracking
  const startTrackingHandler = () => {
    // check permssion android.permission.ACCESS_BACKGROUND_LOCATION grant before start tracking.
    // tracking will not start if android.permission.ACCESS_BACKGROUND_LOCATION persmission is not granted
    startTracking();
    getIsTracking();
  };

  // stop tracking
  const stopTrackingHandler = () => {
    stopTracking();
    getIsTracking();
  };

  return (
    <View style={styles.container}>
      <Text>Tracking: {trackingRunning ? 'yes' : 'no'}</Text>
      <Switch
        value={trackingRunning}
        onValueChange={() => {
          if (!trackingRunning) {
            startTrackingHandler();
          } else {
            stopTrackingHandler();
          }
        }}
      />
      <Button title="Start tracking" onPress={() => startTrackingHandler()} />
      <Button title="Pause tracking" onPress={() => stopTrackingHandler()} />
    </View>
  );
}

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
