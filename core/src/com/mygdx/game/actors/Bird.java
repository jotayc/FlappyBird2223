package com.mygdx.game.actors;

import static com.mygdx.game.extra.Utils.USER_BIRD;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Bird extends Actor {



    public static final int STATE_NORMAL = 0;
    public static final int STATE_DEAD = 1;
    private static final float JUMP_SPEED = 5f;

    private int state;

    private Animation<TextureRegion> birdAnimation;
    private Vector2 position;

    private float stateTime;

    private World world;

    private Body body;
    private Fixture fixture;

    private Sound jumpSound;

    public Bird(World world, Animation<TextureRegion> animation, Sound sound, Vector2 position){
        this.birdAnimation = animation;
        this.position = position;
        this.world = world;
        this.stateTime = 0f;
        this.state = STATE_NORMAL;
        this.jumpSound = sound;

        createBody();
        createFixture();

    }

    private void createBody(){
        BodyDef bodyDef = new BodyDef();

        bodyDef.position.set(this.position);

        bodyDef.type = BodyDef.BodyType.DynamicBody;

       this.body = this.world.createBody(bodyDef);
    }

    private void createFixture(){
        CircleShape circle = new CircleShape();
        circle.setRadius(0.30f);

        this.fixture = this.body.createFixture(circle, 8);
        this.fixture.setUserData(USER_BIRD);

        circle.dispose();

    }



    public int getState(){
        return this.state;
    }



    @Override
    public void act(float delta) {
        boolean jump  = Gdx.input.justTouched();

        if(jump && this.state == STATE_NORMAL){
            this.jumpSound.play();
            this.body.setLinearVelocity(0,JUMP_SPEED);

        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(body.getPosition().x-0.4f, body.getPosition().y-0.25f);
        batch.draw(this.birdAnimation.getKeyFrame(stateTime, true), getX(), getY(), 0.8f,0.5f );

        stateTime += Gdx.graphics.getDeltaTime();
    }

    public void detach(){
        this.body.destroyFixture(this.fixture);
        this.world.destroyBody(this.body);
    }
}














