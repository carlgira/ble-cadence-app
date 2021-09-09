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

    private CBCentralManager centralManager;
    private CBCharacteristic characteristicCadence;
    private List<CBPeripheral> peripherals;
    private NSArray<CBUUID> serviceUUIDs;
    private BleController bleManager;
    private CBPeripheral blePeripheral;
    private HashMap<String, IBleDevice> devices = new HashMap<>();

    private BleNotifyCallback notifyCallback;
    private BleGattCallback connectCallback;

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();

        serviceUUIDs = new NSMutableArray<>();
        centralManager = new CBCentralManager(this, null);
        bleManager = new BleController();
        bleManager.setIOSApp(this);
        config.orientationPortrait = true;

        return new IOSApplication(new BLECadenceTest(bleManager), config);
    }

    @Override
    public void willTerminate(UIApplication application) {
        super.willTerminate(application);
        centralManager.stopScan();
        if(blePeripheral != null){
            centralManager.cancelPeripheralConnection(blePeripheral);
        }
        blePeripheral = null;
        characteristicCadence = null;
        peripherals.clear();
        devices.clear();
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
                callback.onScanFinished(new ArrayList<>());
            }
        }, 10);
    }

    public void cancelScan(){
        this.centralManager.stopScan();
        Foundation.log("Scan Stopped");
        Foundation.log("Number of Peripherals Found: (peripherals.count) " + this.peripherals.size());
    }

    public boolean isConnected(IBleDevice device) {
        for(CBPeripheral peripheral : centralManager.retrieveConnectedPeripherals(serviceUUIDs)){
            if(peripheral.getName().equals(device.getName())){
                return true;
            }
        }
        return false;
    }

    public void disconnectFromDevice(){
        if(blePeripheral != null){
            centralManager.cancelPeripheralConnection(blePeripheral);
        }
    }

    public void connectToDevice (CBPeripheral peripheral, BleGattCallback callback) {
        connectCallback = callback;
        centralManager.connectPeripheral(peripheral, null);
    }

    @Override
    public void didUpdateState(CBCentralManager central) {
        if (CBManagerState.PoweredOn.compareTo(central.getState()) == 0) {

            Foundation.log("Bluetooth Enabled");

        } else {
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
    }


    @Override
    public void didConnectPeripheral(CBCentralManager central, CBPeripheral peripheral) {
        centralManager.stopScan();
        peripheral.setDelegate(this);
        serviceUUIDs.add(new CBUUID(this.bleManager.getServiceUUID()));
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
            return;
        }
        NSArray<CBCharacteristic> characteristics = service.getCharacteristics();

        for(CBCharacteristic characteristic : characteristics) {
            if(characteristic.getUUID().equals(new CBUUID(this.bleManager.getCharacteristicUUID()))){
                characteristicCadence = characteristic;
                peripheral.setNotifyValue(true, characteristicCadence);
                notifyCallback.onNotifySuccess();
                break;
            }
        }
    }

    @Override
    public void didUpdateValue(CBPeripheral peripheral, CBCharacteristic characteristic, NSError error) {
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


    public void subscribeToCharacteristic(String servUuid, String charUuid, BleNotifyCallback callback) {
        this.notifyCallback = callback;
    }
}