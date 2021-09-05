package com.carlgira.game.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public abstract class BaseScreen implements Screen, InputProcessor
{
    protected Stage stage;
    public static final TextureAtlas textureAtlas  = new TextureAtlas("ui-dark-blue.atlas");
    protected final static int width = 360;
    protected final static int height = 720;
    protected Skin skin = new Skin(Gdx.files.internal("skin/comic-ui.json"));

    private Viewport viewport;
    private Camera camera;

    public BaseScreen() {
        initialize();
    }

    public void initialize(){
        camera = new PerspectiveCamera();
        viewport = new FitViewport(width, height);
        stage = new Stage(viewport);
    }

    public abstract void update(float dt);

    public void render(float dt) {
        stage.act(dt);
        update(dt);

        ScreenUtils.clear(255, 255, 255, 1);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void pause()   {  }

    public void resume()  {  }

    public void dispose() {  }


    public void show() {
        InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
        im.addProcessor(this);
        im.addProcessor(stage);
    }

    public void hide() {
        InputMultiplexer im = (InputMultiplexer)Gdx.input.getInputProcessor();
        im.removeProcessor(this);
        im.removeProcessor(stage);
    }

    public boolean keyDown(int keycode)
    {  return false;  }

    public boolean keyUp(int keycode)
    {  return false;  }

    public boolean keyTyped(char c)
    {  return false;  }

    public boolean mouseMoved(int screenX, int screenY)
    {  return false;  }

    public boolean scrolled(int amount)
    {  return false;  }

    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {  return false;  }

    public boolean touchDragged(int screenX, int screenY, int pointer)
    {  return false;  }

    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {  return false;  }

    public static int scale(int value){
        return (int)(0.11*value);
    }
}

