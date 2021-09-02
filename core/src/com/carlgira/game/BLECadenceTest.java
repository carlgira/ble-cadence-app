package com.carlgira.game;

import com.carlgira.game.base.BaseGame;
import com.carlgira.game.ble.IBleManager;
import com.carlgira.game.screens.MainScreen;

public class BLECadenceTest extends BaseGame {

	private IBleManager bleManager;
	public BLECadenceTest(IBleManager bleManager){
		this.bleManager = bleManager;
	}

	public void create() {
		super.create();
		setActiveScreen( new MainScreen(bleManager) );
	}
}