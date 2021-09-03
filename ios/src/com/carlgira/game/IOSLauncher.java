package com.carlgira.game;

import org.robovm.apple.corebluetooth.CBAdvertisementData;
import org.robovm.apple.corebluetooth.CBCentralManager;
import org.robovm.apple.corebluetooth.CBCentralManagerDelegate;
import org.robovm.apple.corebluetooth.CBCentralManagerOptions;
import org.robovm.apple.corebluetooth.CBCentralManagerRestoredState;
import org.robovm.apple.corebluetooth.CBCentralManagerScanOptions;
import org.robovm.apple.corebluetooth.CBCharacteristic;
import org.robovm.apple.corebluetooth.CBConnectionEvent;
import org.robovm.apple.corebluetooth.CBDescriptor;
import org.robovm.apple.corebluetooth.CBL2CAPChannel;
import org.robovm.apple.corebluetooth.CBManagerState;
import org.robovm.apple.corebluetooth.CBPeripheral;
import org.robovm.apple.corebluetooth.CBPeripheralDelegate;
import org.robovm.apple.corebluetooth.CBService;
import org.robovm.apple.corebluetooth.CBUUID;
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.foundation.NSMutableArray;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSTimer;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.utils.Timer;
import com.carlgira.game.base.Callback;

import java.util.ArrayList;
import java.util.List;


public class IOSLauncher extends IOSApplication.Delegate implements CBCentralManagerDelegate, CBPeripheralDelegate {

    CBCentralManager centralManager;
    CBCharacteristic characteristic;
    List<CBPeripheral> peripherals;
    NSArray<CBUUID> serviceUUIDs;
    private BleManager bleManager;
    CBPeripheral blePeripheral;

    @Override
    protected IOSApplication createApplication() {

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        serviceUUIDs = new NSMutableArray<>();
        serviceUUIDs.add(new CBUUID("00001816-0000-1000-8000-00805F9B34FB"));
        centralManager = new CBCentralManager(this, null);
        bleManager = new BleManager();
        bleManager.setIOSApp(this);

        return new IOSApplication(new BLECadenceTest(bleManager), config);
    }

    @Override
    public void willTerminate(UIApplication application) {
        super.willTerminate(application);
        Foundation.log("Stop Scanning");
        centralManager.stopScan();
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    private Callback<BleDevice> callback;
    public void checkPermissions(Callback callback){
        this.callback = callback;
        this.starScan();
    }

    public void starScan(){
        peripherals = new ArrayList<>();
        Foundation.log("Now Scanning...");
        CBCentralManagerScanOptions options = new CBCentralManagerScanOptions();
        options.setAllowsDuplicates(false);

        this.centralManager.scanForPeripherals(serviceUUIDs, options);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                cancelScan();
            }
        }, 17);

    }

    public void cancelScan(){
        this.centralManager.stopScan();
        Foundation.log("Scan Stopped");
        Foundation.log("Number of Peripherals Found: (peripherals.count) " + this.peripherals.size());
    }

    public void disconnectFromDevice(){
        if(blePeripheral != null){
            centralManager.cancelPeripheralConnection(blePeripheral);
        }

    }

    public void connectToDevice () {
        centralManager.connectPeripheral(blePeripheral, null);
    }

    int prevCumCrankRev = 0;
    int prevCrankTime = 0;
    int prevCrankStaleness = 0;
    double prevRPM = 0;
    double rpm = 0.0;

    public double onCSC(CBCharacteristic characteristic){
        return 0.0;
    }

    @Override
    public void didUpdateState(CBCentralManager central) {
        if (CBManagerState.PoweredOn.compareTo(central.getState()) == 0) {

            Foundation.log("Bluetooth Enabled");
            //starScan();

        } else {
            //If Bluetooth is off, display a UI alert message saying "Bluetooth is not enable" and "Make sure that your bluetooth is turned on"
            Foundation.log("Bluetooth Disabled- Make sure your Bluetooth is turned on");

        }
    }

    @Override
    public void willRestoreState(CBCentralManager central, CBCentralManagerRestoredState dict) {

    }

    @Override
    public void didDiscoverPeripheral(CBCentralManager central, CBPeripheral peripheral, CBAdvertisementData advertisementData, NSNumber rssi) {
        blePeripheral = peripheral;
        this.peripherals.add(peripheral);
        peripheral.setDelegate(this);
        this.callback.call(new BleDevice(peripheral));
        if (blePeripheral != null) {
            Foundation.log("Found new pheripheral devices with services");
            Foundation.log("Peripheral name: " + peripheral.getName());
            Foundation.log("**********************************");
            Foundation.log("Advertisement Data : " + advertisementData.getLocalName());
        }
    }


    @Override
    public void didConnectPeripheral(CBCentralManager central, CBPeripheral peripheral) {

    }

    @Override
    public void didFailToConnectPeripheral(CBCentralManager central, CBPeripheral peripheral, NSError error) {

    }

    @Override
    public void didDisconnectPeripheral(CBCentralManager central, CBPeripheral peripheral, NSError error) {

    }

    @Override
    public void connectionEventDidOccur(CBCentralManager central, CBConnectionEvent event, CBPeripheral peripheral) {

    }

    @Override
    public void didUpdateANCSAuthorization(CBCentralManager central, CBPeripheral peripheral) {

    }

    @Override
    public void didUpdateName(CBPeripheral peripheral) {

    }

    @Override
    public void didModifyServices(CBPeripheral peripheral, NSArray<CBService> invalidatedServices) {

    }

    @Override
    public void didUpdateRSSI(CBPeripheral peripheral, NSError error) {

    }

    @Override
    public void didReadRSSI(CBPeripheral peripheral, NSNumber RSSI, NSError error) {

    }

    @Override
    public void didDiscoverServices(CBPeripheral peripheral, NSError error) {
        Foundation.log("*******************************************************");

        if ((error) != null) {
            Foundation.log("Error discovering services: " + error.getLocalizedDescription());
        }

        //FIX We need to discover the all characteristic

        //Foundation.log("Discovered Services: \(services)");
    }

    @Override
    public void didDiscoverIncludedServices(CBPeripheral peripheral, CBService service, NSError error) {

    }

    @Override
    public void didDiscoverCharacteristics(CBPeripheral peripheral, CBService service, NSError error) {

    }

    @Override
    public void didUpdateValue(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {

    }

    @Override
    public void didWriteValue(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {

    }

    @Override
    public void didUpdateNotificationState(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {
        Foundation.log("Value Recieved2: " + characteristic.getValue());
    }

    @Override
    public void didDiscoverDescriptors(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {

    }

    @Override
    public void didUpdateValue(CBPeripheral peripheral, CBDescriptor descriptor, NSError error) {

    }

    @Override
    public void didWriteValue(CBPeripheral peripheral, CBDescriptor descriptor, NSError error) {

    }

    @Override
    public void peripheralIsReadyToSendWrite(CBPeripheral peripheral) {

    }

    @Override
    public void didOpenL2CAPChannel(CBPeripheral peripheral, CBL2CAPChannel channel, NSError error) {

    }
}