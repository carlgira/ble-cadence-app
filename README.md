# ble-cadence-app
App with libgdx to test connectivity to Bluetooth low energy (BLE) devices. IOS and Android app.


## Run

### Desktop

```shell
./gradlew desktop:run
```
### Android

- Emulator (Start an android emulator first)
```shell
./gradlew android:installDebug android:run
```

- Install manually APK
```shell
./gradlew android:packageDebug # Create apk
adb devices # List connected devices
adb -s <device-id> install -r <path-to-apk> # Install apk manually to device
```

### IOS

- Emulator
```shell
./gradlew ios:launchIPhoneSimulator
```

- To install on device use robovm plugin for Android Studio
[Deploying your libGDX game to iOS in 2020](https://medium.com/@bschulte19e/deploying-your-libgdx-game-to-ios-in-2020-4ddce8fff26c)


