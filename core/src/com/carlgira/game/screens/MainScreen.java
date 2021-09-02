package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.carlgira.game.BLECadenceTest;
import com.carlgira.game.base.BaseGame;
import com.carlgira.game.base.BaseScreen;
import com.carlgira.game.ble.IBleManager;


public class MainScreen extends BaseScreen {

    public MainScreen(IBleManager bleManager){
        this.bleManager = bleManager;
    }

    private IBleManager bleManager;

    private Table uiTable;

    public void initialize() {
        TextButton startButton = new TextButton( "Start", BaseGame.textButtonStyle);

        startButton.setPosition(150,150);
        stage.addActor(startButton);

        startButton.addListener(e -> {
                    if (!(e instanceof InputEvent))
                        return false;

                    if (!((InputEvent) e).getType().equals(Type.touchDown))
                        return false;

                    BLECadenceTest.setActiveScreen( new SensorScreen(bleManager) );
                    return true;
                }
        );

        TextButton quitButton = new TextButton( "Quit", BaseGame.textButtonStyle );
        quitButton.setPosition(500,150);
        stage.addActor(quitButton);

        quitButton.addListener(e -> {
            if (!(e instanceof InputEvent))
                return false;

            if (!((InputEvent) e).getType().equals(Type.touchDown))
                return false;

            Gdx.app.exit();
            return true;
        });

        uiTable = new Table();
        uiTable.row();

        uiTable.add(startButton);
        uiTable.row();
        uiTable.add(quitButton);

        uiTable.setFillParent(true);
        stage.addActor(uiTable);
    }

    public void update(float dt) {

    }

    public boolean keyDown(int keyCode) {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
            Gdx.app.exit();
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
