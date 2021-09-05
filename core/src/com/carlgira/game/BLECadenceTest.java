package com.carlgira.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.carlgira.game.base.BaseScreen;
import com.carlgira.game.screens.MainScreen;
import com.clj.fastble.data.IBleManager;

public class BLECadenceTest extends Game {

	private IBleManager bleManager;
	private static Game game;

	public BLECadenceTest(IBleManager bleManager){
		this.bleManager = bleManager;
		game = this;
	}

	public void create() {
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor( im );

		setActiveScreen( new MainScreen(bleManager) );
	}

	public static void setActiveScreen(BaseScreen s) {
		game.setScreen(s);
	}
}