package com.carlgira.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.carlgira.game.BLECadenceTest;
import com.carlgira.game.base.BaseScreen;
import com.clj.fastble.data.IBleManager;


public class MainScreen extends BaseScreen {

    public MainScreen(IBleManager bleManager){
        this.bleManager = bleManager;
    }

    private IBleManager bleManager;

    private Table table;
    private Label cadenceLabel;


    public void initialize() {
        super.initialize();

        TextButton configButton = new TextButton( "CONFIG", skin);


        configButton.addListener(e -> {
                if (!(e instanceof InputEvent))
                    return false;

                if (!((InputEvent) e).getType().equals(Type.touchDown))
                    return false;

                BLECadenceTest.setActiveScreen( new SensorScreen(bleManager) );
                return true;
            }
        );

        float labelScalar = (2.0f / 360f) * width;

        cadenceLabel = new Label("0.00", skin, "title");
        skin.getFont("title").getData().setScale(1.5f, 1.5f);

        Label unitsLabel = new Label("RPM", skin);
        skin.getFont("font").getData().setScale(1.5f, 1.5f);


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

    public void update(float dt) {

    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
