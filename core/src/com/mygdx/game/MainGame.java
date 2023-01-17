package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.screens.GameScreen;

public class MainGame extends Game {

	private GameScreen gameScreen;

	@Override
	public void create() {
		this.gameScreen = new GameScreen(this);

		setScreen(this.gameScreen);
	}
}
