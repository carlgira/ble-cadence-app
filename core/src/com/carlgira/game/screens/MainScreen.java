package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.carlgira.game.BLECadenceTest;
import com.carlgira.game.base.BaseScreen;
import com.clj.fastble.data.IBleController;

public class MainScreen extends BaseScreen {

    private Table table;
    private Label cadenceLabel;
    private int cadence = 0;
    private SensorScreen sensorScreen;

    public void initialize() {
        super.initialize();

        TextButton configButton = new TextButton( "CONFIG", skin);

        configButton.addListener(e -> {
                if (!(e instanceof InputEvent))
                    return false;

                if (!((InputEvent) e).getType().equals(Type.touchDown))
                    return false;


                BLECadenceTest.setActiveScreen( sensorScreen);
                return true;
            }
        );

        skin.getFont("title").getData().setScale(2f, 2f);
        skin.getFont("font").getData().setScale(2f, 2f);

        cadenceLabel = new Label("0", skin, "title");

        Label unitsLabel = new Label("RPM", skin);

        table = new Table();
        table.setWidth((int)(BaseScreen.width*0.9));
        table.setHeight((int)(BaseScreen.height*0.9));
        table.setPosition((int)(BaseScreen.width*0.05), (int)(BaseScreen.height*0.05));
        table.padTop(100);

        table.row().expandY();
        table.add(cadenceLabel).colspan(2);
        table.row();
        table.add(unitsLabel).colspan(2);
        table.row().expandY();
        table.row().expandX();
        table.add(configButton).bottom().right();

        table.setSkin(skin);
        stage.addActor(table);
    }

    @Override
    public void update(float dt) {
    }

    public void setCadence(int cadence){
        this.cadence = cadence;
        this.cadenceLabel.setText("" + this.cadence);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void setSensorScreen(SensorScreen sensorScreen) {
        this.sensorScreen = sensorScreen;
    }
}
