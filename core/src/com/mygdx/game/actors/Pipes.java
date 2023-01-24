package com.mygdx.game.actors;



import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.extra.Utils;

public class Pipes extends Actor {

    //Todo 5. Para crear las tuberías debemos fijar un ancho y alto
    private static final float PIPE_WIDTH = 1f;
    private static final float PIPE_HEIGHT = 4f;

    //Todo 6. Creamos Texturas, Body, fixture y mundo
    private TextureRegion pipeDownTR;

    private Body bodyDown;

    private Fixture fixtureDown;

    private World world;

    //Todo 7 Constructor con mundo textura y posicion
    public Pipes(World world, TextureRegion trpDown, Vector2 position) {
        this.world = world;
        this.pipeDownTR = trpDown;

        createBodyPipeDown(position);
        createFixture();
    }


    //Todo 8. creamos metodo para body con parametro
    private void createBodyPipeDown(Vector2 position) {
        BodyDef def = new BodyDef();
        def.position.set(position);
        def.type = BodyDef.BodyType.KinematicBody;
        bodyDown = world.createBody(def);
        bodyDown.setUserData(Utils.USER_PIPE_DOWN);

    }

    //Todo 9 Creamos método para la fixture
    private void createFixture() {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(PIPE_WIDTH/2, PIPE_HEIGHT/2);

        this.fixtureDown = bodyDown.createFixture(shape, 8);
        shape.dispose();
    }

    //Todo 10. Sobrecargamos métodos act y draw
    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setPosition(this.bodyDown.getPosition().x - (PIPE_WIDTH/2), this.bodyDown.getPosition().y - (PIPE_HEIGHT/2) );
        batch.draw(this.pipeDownTR, getX(),getY(),PIPE_WIDTH,PIPE_HEIGHT);
    }

    //Todo 11. Creamos detach para liberar recursos
    public void detach(){
        bodyDown.destroyFixture(fixtureDown);
        world.destroyBody(bodyDown);
    }
}
