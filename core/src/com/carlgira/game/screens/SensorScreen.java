package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.carlgira.game.base.BaseScreen;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleManager;

import java.util.HashMap;
import java.util.List;

public class SensorScreen extends BaseScreen {
    private Table table;
    private final HashMap<String, IBleDevice> listDevices = new HashMap<>();
    private final HashMap<String,CheckBox> checkBoxes = new HashMap<>();
    private TextButton scanButton;
    private IBleManager bleManager;
    private Label msgLabel;

    public SensorScreen(IBleManager bleManager){
        this.bleManager = bleManager;
    }

    public void initialize() {
        super.initialize();
        scanButton = new TextButton( "SCAN", skin);
        scanButton.align( Align.center);
        scanButton.setPosition(width/2.0f - scanButton.getWidth()/2, height*0.075f);

        scanButton.addListener(e -> {
            Gdx.app.log("BLEAPP", "b "  +e.getClass());
            if (!(e instanceof InputEvent))
                return false;

            if (!((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;

            Gdx.app.log("BLEAPP", "a "  +e.getClass());

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
                        // FIX disconnect
                        break;
                    }
                }

                if(!connectedDevice.equals(checkedDevice)){
                    // FIX connect
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
}

