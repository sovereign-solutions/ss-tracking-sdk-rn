import * as React from 'react';
import DeviceInfo from 'react-native-device-info';
import { StyleSheet, View, Text, Button, Switch } from 'react-native';
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

export default function App() {
  const [trackingRunning, setIsTracking] = React.useState<boolean>();

  React.useEffect(() => {
    initialTrackingSDK();
    getIsTracking();
  }, []);
  const getIsTracking = async () => {
    setIsTracking(await isTrackingSdk());
  };
  const initialTrackingSDK = () => {
    const username = 'sales_testing_1';
    const token =
      'bearer pJDEr8Q-wfmzZVFlt6WJxqQEoqjbDyMzv3x_NKkj1Oouc24eQKGsH4bVVveWSji4dpY6WTBxjg4VutgvmuNpHnVu1gyn0g8oMXQ7X-vu9SiAHF9eX4T0msUgkPvXppqxjWm2DQzicTRVC2qN3uYEDW6CsFbhqFVe4wakbU5NXgYN2qBIN2zYXkvyfObBDijSIUA9E30C992-ffA3HItmWV51FRKlX-R6bsIqsM1CePITQB0FOzBXPPH0NmjCp6G69h-Gi9OKvGmCYHcwcB3K_OstNnIZ9nueORJSbKtEt4McJ5ywsOiUzs_E3DdehN4-HukJTwIeOw7-3C4gP9FCB7p2UUQc3ZoKIs3V-2KvkcVC2yaq1F5PRafzrLQ5YItT95uK8PTEzVEMyGOeO3wmL3yvCekFegKWP2SFNdl6H6Yzf5mDt01BRW67Yvhrc-wBtMiPRa11JsRbi4kX_RGcq_0Aoxg';
    const refreshToken =
      'dfWI_9JQNwcpNJOjwez5XBc5UPJYLJ6DBdgAVfNL_LH1C7qru4v6I3DlX1ZboxZ08uNsL6BfecixN40XS65Bd0tGtWg5ed9yHvGli9k2sNHidQ1IreWwkF4IwT9oPutBoS2PFQrjcdjPe1oF97bNaS9cecS9kTtM27zKUaJ0n0-b03uYCshWDm-0pbCn_TAB7-8ecGu2DfHym474laq4OJ-GwHA12qFj2ExuY7sq0ghU9NzqDHqjsDA_oNauPDO5d7buhJxAOq0i3GKJ4s9xTU3YkA9qC4zJc3z470Rnrr71YT_SxejPd6PTdVNRTTMuSzLBiOxZa6vBL5G4znvVrkQMyL4E9q1FXxai9SStOH6uEodqDpzv1K4KwNk9zbjus-1TTpj5Hn-77nQ3XRqZeEQX7_tcBEtsM7QCpV_EMM8RQbbGhTzPaT-UtT3YGr3_w26de6iAtChKxuAzkPOpFplACBk';
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
  const startTrackingHandler = () => {
    // check permssion android.permission.ACCESS_BACKGROUND_LOCATION grant before start tracking.
    // tracking will not start if android.permission.ACCESS_BACKGROUND_LOCATION persmission is not granted

    
    startTracking();
    getIsTracking();
  };
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

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
