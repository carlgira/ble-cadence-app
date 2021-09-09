package com.carlgira.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.carlgira.game.base.BaseScreen;
import com.carlgira.game.screens.MainScreen;
import com.carlgira.game.screens.SensorScreen;
import com.clj.fastble.data.IBleController;

public class BLECadenceTest extends Game {

	private IBleController bleManager;
	private static Game game;

	public BLECadenceTest(IBleController bleManager){
		this.bleManager = bleManager;
		this.bleManager.setServiceUUID("00001816-0000-1000-8000-00805F9B34FB");
		this.bleManager.setCharacteristicUUID("00002a5b-0000-1000-8000-00805F9B34FB");
		game = this;
	}

	public void create() {
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor( im );

		setActiveScreen( new SensorScreen(bleManager) );
	}

	public static void setActiveScreen(BaseScreen s) {
		game.setScreen(s);
	}

	public static Screen getActiveScreen(){
		return game.getScreen();
	}
}