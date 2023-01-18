package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bird extends Actor {
    private Animation<TextureRegion> birdAnimation;
    private Vector2 position;

    private float stateTime;

    public Bird(Animation<TextureRegion> animation, Vector2 position){
        this.birdAnimation = animation;
        this.position = position;

        this.stateTime = 0f;
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(this.birdAnimation.getKeyFrame(stateTime, true), position.x, position.y, 0.8f,0.5f );

        stateTime += Gdx.graphics.getDeltaTime();
    }
}














