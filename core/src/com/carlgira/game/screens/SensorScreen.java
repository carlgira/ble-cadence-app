package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.carlgira.game.BLECadenceTest;
import com.carlgira.game.base.BaseScreen;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleController;
import com.clj.fastble.exception.BleException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SensorScreen extends BaseScreen {

    private Table table;
    private final HashMap<String, IBleDevice> listDevices = new HashMap<>();
    private final HashMap<String,CheckBox> checkBoxes = new HashMap<>();
    private TextButton scanButton;
    private IBleController bleManager;
    private Label msgLabel;
    private MainScreen mainScreen;
    private IBleDevice conectedDevice;

    private int prevCumulativeCrankRev = 0;
    private int prevCrankTime = 0;
    private double rpm = 0;
    private double prevRPM = 0;
    private int prevCrankStaleness = 0;
    private int stalenessLimit = 4;

    private EventListener deviceConnectListener;
    private BleNotifyCallback notifyCallback;
    private BleGattCallback disconnectCallback;

    public SensorScreen(IBleController bleManager){
        this.bleManager = bleManager;
        this.mainScreen = new MainScreen();
        this.mainScreen.setSensorScreen(this);

        deviceConnectListener = e -> {

            if ( !(e instanceof InputEvent) ||  !((InputEvent)e).getType().equals(InputEvent.Type.touchDown) ){
                return false;
            }

            CheckBox checkBox = (CheckBox)e.getListenerActor();

            IBleDevice cd = listDevices.get(e.getListenerActor().getName());

            if(checkBox.isChecked()) {
                SensorScreen.this.disconnectDevice(checkBox.getName());
            }
            else{
                if(bleManager.isConnected(conectedDevice)){
                    SensorScreen.this.disconnectDevice(conectedDevice.getName());
                }

                bleManager.connect(cd, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                        setMsgLabel("Starting to connect to device ");

                    }

                    @Override
                    public void onConnectFail(IBleDevice bleDevice, BleException exception) {
                        setMsgLabel("Fail connect to device  " );
                        Gdx.app.log("BLEAPP", exception.getDescription());
                    }

                    @Override
                    public void onConnectSuccess(IBleDevice bleDevice, Object gatt, int status) {
                        setMsgLabel("Connected to device : " + bleDevice.getName());
                        conectedDevice = bleDevice;
                        bleManager.notify(cd, notifyCallback);

                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, IBleDevice device, Object gatt, int status) {
                        setMsgLabel("DisConnected to device : " + device.getName());
                        conectedDevice = null;
                    }
                });
            }

            return true;
        };


        disconnectCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(IBleDevice bleDevice, BleException exception) {
                conectedDevice = null;
            }

            @Override
            public void onConnectSuccess(IBleDevice bleDevice, Object gatt, int status) {

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, IBleDevice device, Object gatt, int status) {
                setMsgLabel("DisConnected from device : " + device.getName());
                if(checkBoxes.containsKey(device.getName())){
                   checkBoxes.get(device.getName()).setChecked(false);
                }
                conectedDevice = null;
                SensorScreen.this.clear();
            }
        };


        notifyCallback = new BleNotifyCallback() {
                @Override
                public void onNotifySuccess() {
                    Gdx.app.log("BLEAPP", "onNotifySuccess");
                    if(BLECadenceTest.getActiveScreen().equals(SensorScreen.this)){
                        BLECadenceTest.setActiveScreen(mainScreen);
                    }
                }

                @Override
                public void onNotifyFailure(BleException exception) {
                    Gdx.app.log("BLEAPP", "onNotifyFailure " + exception.getDescription());
                }

                @Override
                public void onCharacteristicChanged(byte[] data) {
                    int value = (int)(getCadenceValue(data));
                    Timer.post(new Timer.Task() {
                        @Override
                        public void run() {
                            mainScreen.setCadence(value);
                        }
                    });


                }
            };
    }

    public void initialize() {
        super.initialize();

        TextButton backButton = new TextButton( "<-", skin);
        backButton.setWidth(50);
        backButton.setPosition(width*0.05f, height*0.075f);

        backButton.addListener(e -> {
            if (!(e instanceof InputEvent))
                return false;

            if (!((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;

            this.clear();
            BLECadenceTest.setActiveScreen(mainScreen);

            return true;
        });

        scanButton = new TextButton( "SCAN", skin);
        scanButton.setPosition(width/2.0f - scanButton.getWidth()/2, height*0.075f);

        scanButton.addListener(e -> {
            if (!(e instanceof InputEvent))
                return false;

            if (!((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;

            scanButton.setTouchable(Touchable.disabled);

            listDevices.clear();
            checkBoxes.clear();

            bleManager.scan(new BleScanCallback() {
                @Override
                public void onScanStarted(boolean success) {
                    setMsgLabel("Scan Started");
                }

                @Override
                public void onScanning(IBleDevice bleDevice) {
                    setMsgLabel("New Device : "  + bleDevice.getName() + " " + bleDevice.getDevice());
                    newDeviceDiscovered(bleDevice);
                }

                @Override
                public void onScanFinished(List<IBleDevice> scanResultList) {
                    setMsgLabel("Scan Finished");
                    scanButton.setTouchable(Touchable.enabled);
                }
            });

                return true;
            }
        );

        Label titleLabel = new Label("Ble Cadence App", skin, "title");
        titleLabel.setPosition((int)(BaseScreen.width/2.0f - titleLabel.getWidth()/2), (int)(BaseScreen.height*0.8));

        msgLabel = new Label("", skin);
        this.setMsgLabel("Scan for devices");

        table = new Table();
        table.setWidth((int)(BaseScreen.width*0.9));
        table.setHeight((int)(BaseScreen.height*0.6));
        table.setPosition((int)(BaseScreen.width*0.05), (int)(BaseScreen.height*0.15));

        stage.addActor(backButton);
        stage.addActor(scanButton);
        stage.addActor(table);
        stage.addActor(msgLabel);
        stage.addActor(titleLabel);
    }

    public void setMsgLabel(String message){
        msgLabel.setText(message);
        msgLabel.setPosition(width/2.0f - msgLabel.getWidth(), height*0.03f);
    }

    public void newDeviceDiscovered(IBleDevice device){
        if(!listDevices.containsKey(device.getName())){

            CheckBox d = new CheckBox(device.getName(), skin);
            d.setName(device.getName());
            d.addListener(deviceConnectListener);

            listDevices.put(device.getName(), device);
            checkBoxes.put(device.getName(), d);

            table.add(d);
            table.row();
        }
    }

    public void update(float dt) {

    }

    public boolean keyDown(int keyCode) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public double getCadenceValue(byte[] byteArray) {

        Gdx.app.log("BLEAPP", Arrays.toString(byteArray));

        int crankRevIndex = 1;
        int crankTimeIndex = 3;

        int[] data = new int[byteArray.length];

        for (int i = 0; i < byteArray.length; data[i] = byteArray[i++] & 0xff);

        int cumulativeCrankRev = ((data[crankRevIndex + 1] << 8) + data[crankRevIndex]);
        int lastCrankTime = ((data[crankTimeIndex + 1] << 8) + data[crankTimeIndex]);

        int deltaRotations = cumulativeCrankRev - prevCumulativeCrankRev;
        if (deltaRotations < 0) {
            deltaRotations += 65535;
        }

        int timeDelta = lastCrankTime - prevCrankTime;
        if (timeDelta < 0) {
            timeDelta += 65535;
        }

        if (timeDelta != 0) {
            prevCrankStaleness = 0;
            double timeMins = ((double)timeDelta) / 1024.0 / 60.0;
            rpm = ((double)deltaRotations) / timeMins;
            prevRPM = rpm;

        }
        else if (prevCrankStaleness < stalenessLimit) {
            rpm = prevRPM;
            prevCrankStaleness += 1;
        }
        else {
            rpm = 0.0;
        }

        prevCumulativeCrankRev = cumulativeCrankRev;
        prevCrankTime = lastCrankTime;

        return rpm;
    }

    public void clear(){
        this.table.clear();
        listDevices.clear();
        checkBoxes.clear();

        if(conectedDevice!= null && bleManager.isConnected(conectedDevice)){
            newDeviceDiscovered(conectedDevice);
            checkBoxes.get(conectedDevice.getName()).setChecked(true);
        }
        else {
            conectedDevice = null;
        }

    }

    public void disconnectDevice(String name){
        if(checkBoxes.containsKey(name)){
            checkBoxes.get(name).setChecked(false);
        }

        if(listDevices.containsKey(name)){
            bleManager.disconnect(listDevices.get(name), disconnectCallback);
            bleManager.stopNotify(conectedDevice);
            mainScreen.setCadence(0);
        }
        conectedDevice = null;
    }
}

