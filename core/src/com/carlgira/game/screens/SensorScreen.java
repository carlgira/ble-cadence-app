package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.carlgira.game.base.BaseGame;
import com.carlgira.game.base.BaseScreen;
import com.carlgira.game.base.Callback;
import com.carlgira.game.ble.IBleDevice;
import com.carlgira.game.ble.IBleManager;

import java.util.List;

public class SensorScreen extends BaseScreen {
    private Table uiTable;

    public SensorScreen(IBleManager bleManager){
        this.bleManager = bleManager;
    }

    private IBleManager bleManager;

    public void initialize() {
        TextButton scanButton = new TextButton( "Scan", BaseGame.textButtonStyle);

        scanButton.setPosition(150,150);
        stage.addActor(scanButton);

        scanButton.addListener(e -> {
            bleManager.checkPermissions(new Callback() {
                @Override
                public void call(List devices) {
                    for(int i=0;i<devices.size();i++){
                        IBleDevice device = (IBleDevice)devices.get(0);
                        TextButton d = new TextButton( device.getName(), BaseGame.textButtonStyle);
                        uiTable.add(d);
                        uiTable.row();
                    }
                }

                @Override
                public void call(IBleDevice device) {
                    TextButton d = new TextButton( device.getName(), BaseGame.textButtonStyle);
                    uiTable.add(d);
                    uiTable.row();
                }
            });

                return true;
            }
        );

        uiTable = new Table();
        uiTable.row();

        uiTable.add(scanButton);
        uiTable.row();

        uiTable.setFillParent(true);
        stage.addActor(uiTable);
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

