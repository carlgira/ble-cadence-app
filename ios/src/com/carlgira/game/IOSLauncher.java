package com.carlgira.game;

import org.robovm.apple.corebluetooth.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.foundation.Foundation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.badlogic.gdx.utils.Timer;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.exception.ConnectException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class IOSLauncher extends IOSApplication.Delegate implements CBCentralManagerDelegate, CBPeripheralDelegate {

    CBCentralManager centralManager;
    CBCharacteristic characteristicCadence;
    List<CBPeripheral> peripherals;
    NSArray<CBUUID> serviceUUIDs;
    private BleManager bleManager;
    CBPeripheral blePeripheral;

    HashMap<String, IBleDevice> devices = new HashMap<>();

    @Override
    protected IOSApplication createApplication() {

        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        serviceUUIDs = new NSMutableArray<>();
        centralManager = new CBCentralManager(this, null);
        bleManager = new BleManager();
        bleManager.setIOSApp(this);
        config.orientationPortrait = true;

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

    private BleScanCallback callback;
    public void checkPermissions(String uuid, BleScanCallback callback){
        this.callback = callback;
        serviceUUIDs.clear();
        serviceUUIDs.add(new CBUUID(uuid));
        this.callback.onScanStarted(true);
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

    private BleGattCallback connectCallback;
    String charUUid = "";

    public void connectToDevice (CBPeripheral peripheral, BleGattCallback callback) {
        connectCallback = callback;
        centralManager.connectPeripheral(peripheral, null);

    }

    int prevCumCrankRev = 0;
    int prevCrankTime = 0;
    int prevCrankStaleness = 0;
    double prevRPM = 0;
    double rpm = 0.0;

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
        BleDevice device = new BleDevice(peripheral);
        devices.put(device.getName(), device);
        peripheral.setDelegate(this);
        this.callback.onScanning(new BleDevice(peripheral));
        if (blePeripheral != null) {
            Foundation.log("Found new pheripheral devices with services");
            Foundation.log("Peripheral name: " + peripheral.getName());
            Foundation.log("**********************************");
            Foundation.log("Advertisement Data : " + advertisementData.getLocalName());
        }
    }


    @Override
    public void didConnectPeripheral(CBCentralManager central, CBPeripheral peripheral) {
        centralManager.stopScan();
        peripheral.setDelegate(this);
        //Only look for services that matches transmit uuid
        NSArray<CBUUID> serviceUUIDs = new NSMutableArray<>();
        serviceUUIDs.add(new CBUUID("00001816-0000-1000-8000-00805F9B34FB"));
        peripheral.discoverServices(serviceUUIDs);

        connectCallback.onConnectSuccess(devices.get(peripheral.getName()), peripheral, 0);
    }

    @Override
    public void didFailToConnectPeripheral(CBCentralManager central, CBPeripheral peripheral, NSError error) {
        connectCallback.onConnectFail(devices.get(peripheral.getName()), new ConnectException<>(error.getLocalizedDescription()));
    }

    @Override
    public void didDisconnectPeripheral(CBCentralManager central, CBPeripheral peripheral, NSError error) {
        connectCallback.onDisConnected(true, devices.get(peripheral.getName()), peripheral, 0);
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
            Gdx.app.log("BLEAPP", "Error discovering services: " + error.getLocalizedDescription());
        }

        for(CBService service : peripheral.getServices()) {
            peripheral.discoverCharacteristics(null, service);

        }
        Gdx.app.log("BLEAPP", "Discovered Services: ");
    }

    @Override
    public void didDiscoverIncludedServices(CBPeripheral peripheral, CBService service, NSError error) {

    }

    @Override
    public void didDiscoverCharacteristics(CBPeripheral peripheral, CBService service, NSError error) {
        if ((error) != null) {
            Gdx.app.log("BLEAPP", "Error discovering services " + error.getLocalizedDescription());
            return;
        }

        NSArray<CBCharacteristic> characteristics = service.getCharacteristics();

        Gdx.app.log("BLEAPP", "Found " + characteristics.size() + " characteristics");

        for(CBCharacteristic characteristic : characteristics) {
            Gdx.app.log("BLEAPP", "Found " + characteristic.getUUID().getUUIDString());
            if(characteristic.getUUID().equals(new CBUUID("00002a5b-0000-1000-8000-00805F9B34FB"))){
                characteristicCadence = characteristic;
                peripheral.setNotifyValue(true, characteristicCadence);

                Gdx.app.log("BLEAPP", "Characteristic " + characteristicCadence.getUUID().getUUIDString());
            }

            //peripheral.discoverDescriptors(for: characteristic)
        }
    }

    @Override
    public void didUpdateValue(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {
        Gdx.app.log("BLEIOS", "didUpdateValue " + notifyCallback);
        if(notifyCallback != null){
            notifyCallback.onCharacteristicChanged(characteristic.getValue().getBytes());
        }
    }

    @Override
    public void didWriteValue(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {

    }

    @Override
    public void didUpdateNotificationState(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {

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

    private BleNotifyCallback notifyCallback;
    public void subscribeToCharacteristic(String servUuid, String charUuid, BleNotifyCallback callback) {
        this.notifyCallback = callback;
    }
}