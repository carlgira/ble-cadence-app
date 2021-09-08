package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.clj.fastble.data.IBleManager;
import com.clj.fastble.exception.BleException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SensorScreen extends BaseScreen {

    private Table table;
    private final HashMap<String, IBleDevice> listDevices = new HashMap<>();
    private final HashMap<String,CheckBox> checkBoxes = new HashMap<>();
    private TextButton scanButton;
    private IBleManager bleManager;
    private Label msgLabel;
    private MainScreen mainScreen;

    public SensorScreen(MainScreen mainScreen, IBleManager bleManager){
        this.bleManager = bleManager;
        this.mainScreen = mainScreen;
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
            bleManager.scan("00001816-0000-1000-8000-00805F9B34FB", new BleScanCallback() {
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

        msgLabel = new Label("", skin);
        this.setMsgLabel("Scan for devices");

        table = new Table();
        table.setWidth((int)(BaseScreen.width*0.9));
        table.setHeight((int)(BaseScreen.height*0.7));
        table.setPosition((int)(BaseScreen.width*0.05), (int)(BaseScreen.height*0.15));
        table.padTop(50);

        stage.addActor(backButton);
        stage.addActor(scanButton);
        stage.addActor(table);
        stage.addActor(msgLabel);
    }

    public void setMsgLabel(String message){
        msgLabel.setText(message);
        msgLabel.setPosition(width/2.0f - msgLabel.getWidth()/2, height*0.03f);
    }

    public void newDeviceDiscovered(IBleDevice device){
        if(!listDevices.containsKey(device.getName())){
            CheckBox d = new CheckBox(device.getName(), skin);
            d.setName(device.getName());
            d.addListener(e -> {

                if ( !(e instanceof InputEvent) ||  !((InputEvent)e).getType().equals(InputEvent.Type.touchDown) ){
                    return false;
                }

                String checkedDevice = e.getListenerActor().getName();

                IBleDevice cd = listDevices.get(checkedDevice);

                String connectedDevice = "";
                for(String key : listDevices.keySet()){
                    if(checkBoxes.get(key).isChecked()){
                        checkBoxes.get(key).setChecked(false);
                        connectedDevice = key;
                        bleManager.disconnect(listDevices.get(key), new BleGattCallback() {
                            @Override
                            public void onStartConnect() {

                            }

                            @Override
                            public void onConnectFail(IBleDevice bleDevice, BleException exception) {

                            }

                            @Override
                            public void onConnectSuccess(IBleDevice bleDevice, Object gatt, int status) {

                            }

                            @Override
                            public void onDisConnected(boolean isActiveDisConnected, IBleDevice device, Object gatt, int status) {
                                setMsgLabel("DisConnected from device : " + device.getName());
                            }
                        });
                        break;
                    }
                }

                if(!connectedDevice.equals(checkedDevice)){
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
                            bleManager.subscribeToCharacteristic(cd,
                                    "00001816-0000-1000-8000-00805F9B34FB",
                                    "00002a5b-0000-1000-8000-00805F9B34FB",
                                    new BleNotifyCallback() {
                                        @Override
                                        public void onNotifySuccess() {
                                            Gdx.app.log("BLEAPP", "onNotifySuccess");
                                        }

                                        @Override
                                        public void onNotifyFailure(BleException exception) {
                                            Gdx.app.log("BLEAPP", "onNotifyFailure " + exception.getDescription());
                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {

                                            int value = (int)(notifyCallback(data));
                                            Gdx.app.log("BLEAPP", "new data " + value);
                                            mainScreen.setCadence(20);
                                            Timer.post(new Timer.Task() {
                                                @Override
                                                public void run() {
                                                    mainScreen.setCadence(value);
                                                }
                                            });

                                        }
                                    });
                        }

                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, IBleDevice device, Object gatt, int status) {
                            setMsgLabel("DisConnected to device : " + device.getName());
                        }
                    });
                }

                return true;
            });


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

    int prevCumulativeCrankRev = 0;
    int prevCrankTime = 0;
    double rpm = 0;
    double prevRPM = 0;
    int prevCrankStaleness = 0;
    int stalenessLimit = 4;


    // Called when device sends update notification
    public double notifyCallback(byte[] byteArray) {

        int value_offset = 0;

        Gdx.app.log("BLEAPP", Arrays.toString(byteArray));

        int crankRevIndex = 1;
        int crankTimeIndex = 3;
        //if(hasWheel){crankRevIndex = 7;crankTimeIndex = 9;}

        int[] data = new int[byteArray.length];

        for (int i = 0; i < byteArray.length; data[i] = byteArray[i++] & 0xff);

        int cumulativeCrankRev = ((data[crankRevIndex + 1] << 8) + data[crankRevIndex]);
        int lastCrankTime = ((data[crankTimeIndex + 1] << 8) + data[crankTimeIndex]);

        int deltaRotations = cumulativeCrankRev - prevCumulativeCrankRev;
        if (deltaRotations < 0)
        {
            deltaRotations += 65535;
        }

        int timeDelta = lastCrankTime - prevCrankTime;
        if (timeDelta < 0)
        {
            timeDelta += 65535;
        }

        // In Case Cad Drops, we use PrevRPM
        // to substitute (up to 4 seconds before reporting 0)
        if (timeDelta != 0)
        {
            prevCrankStaleness = 0;
            double timeMins = ((double)timeDelta) / 1024.0 / 60.0;
            rpm = ((double)deltaRotations) / timeMins;
            prevRPM = rpm;

        }
        else if (prevCrankStaleness < stalenessLimit)
        {
            rpm = prevRPM;
            prevCrankStaleness += 1;
        }
        else
        {
            rpm = 0.0;
        }

        prevCumulativeCrankRev = cumulativeCrankRev;
        prevCrankTime = lastCrankTime;

        return rpm;
    }
}

