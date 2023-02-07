package com.mygdx.game.screens;

import static com.mygdx.game.extra.Utils.SCREEN_HEIGTH;
import static com.mygdx.game.extra.Utils.SCREEN_WIDTH;
import static com.mygdx.game.extra.Utils.USER_BIRD;
import static com.mygdx.game.extra.Utils.USER_COUNTER;
import static com.mygdx.game.extra.Utils.USER_FLOOR;
import static com.mygdx.game.extra.Utils.USER_ROOF;
import static com.mygdx.game.extra.Utils.WORLD_HEIGHT;
import static com.mygdx.game.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.MainGame;
import com.mygdx.game.actors.Bird;
import com.mygdx.game.actors.Pipes;
//Todo 4. Implementamos en GameScreen la interfaz ContactListener
public class GameScreen  extends BaseScreen implements ContactListener {


    private  static final float TIME_TO_SPAWN_PIPES = 1.5f;
    private float timeToCreatePipe;
    private Stage stage;
    private Bird bird;
    private Body bodyFloor;
    private Body bodyRoof;
    private Fixture fixtureFloor;
    private Fixture fixtureRoof;

    //Todo 3. Creamos una valiabre contador....
    private int scoreNumber;

    private Image background;

    private World world;
    private Array<Pipes> arrayPipes;

    private Music musicbg;
    private Sound hitSound;
    private Sound gameOverSound;

    //Depuración
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera worldCamera;
    //Score Cámara
    private OrthographicCamera fontCamera;
    private BitmapFont score;


    public GameScreen(MainGame mainGame) {
        super(mainGame);

        this.world = new World(new Vector2(0,-10), true);
        //Todo 5. Le pasamos al mundo el objeto que implemente la interfaz contactListener (en este caso será la propia instancia de GameScreen)
        this.world.setContactListener(this);

        FitViewport fitViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage = new Stage(fitViewport);


        this.arrayPipes = new Array();
        this.timeToCreatePipe = 0f;

        this.worldCamera = (OrthographicCamera) this.stage.getCamera();
        this.debugRenderer = new Box2DDebugRenderer();

        prepareGameSound();
        prepareScore();

    }


    @Override
    public void show() {
        addBackground();
        addFloor();
        addRoof();
        addBird();

        this.musicbg.setLooping(true);
        this.musicbg.play();

    }

    public void addBackground(){
        this.background = new Image(mainGame.assetManager.getBackground());
        this.background.setPosition(0,0);
        this.background.setSize(WORLD_WIDTH,WORLD_HEIGHT);
        this.stage.addActor(this.background);
    }

    public void addBird(){
        Animation<TextureRegion> birdSprite = mainGame.assetManager.getBirdAnimation();
        Sound sound = mainGame.assetManager.getJumpSound();
        this.bird = new Bird(this.world,birdSprite, sound, new Vector2(1f,4f));
        this.stage.addActor(this.bird);
    }

    private void addFloor() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(WORLD_WIDTH / 2f, 0.6f);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        this.bodyFloor = world.createBody(bodyDef);


        PolygonShape edge = new PolygonShape();
        edge.setAsBox(2.3f, 0.5f);
        this.fixtureFloor = this.bodyFloor.createFixture(edge, 3);
        this.fixtureFloor.setUserData(USER_FLOOR);
        edge.dispose();
    }

    public void addRoof(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        this.bodyRoof = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0,WORLD_HEIGHT,WORLD_WIDTH,WORLD_HEIGHT);
        this.fixtureRoof = this.bodyRoof.createFixture(edge, 1);
        this.fixtureRoof.setUserData(USER_ROOF);
        edge.dispose();
    }

    //Creamos un método para configurar tod o lo relacionado con el texto de la puntuación

    private void prepareScore(){
        //Todo 3.1 ...Y la inicializamos a 0
        this.scoreNumber = 0;
        this.score = this.mainGame.assetManager.getFont();
        this.score.getData().scale(1f);


        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH,SCREEN_HEIGTH);
        this.fontCamera.update();

    }

    private void prepareGameSound() {
        this.musicbg = this.mainGame.assetManager.getMusicBG();
        this.gameOverSound = this.mainGame.assetManager.getGameOverSound();
        this.hitSound = this.mainGame.assetManager.getHitSound();
    }


    public void addPipes(float delta){

        TextureRegion pipeDownTexture = mainGame.assetManager.getPipeDownTR();
        TextureRegion pipeTopTexture = mainGame.assetManager.getPipeTopTR();

        //Como ambas tuberías están en la misma clase solo debemos instanciar un objeto

        if(bird.getState() == Bird.STATE_NORMAL) {

            this.timeToCreatePipe+=delta;

            if(this.timeToCreatePipe >= TIME_TO_SPAWN_PIPES) {

                this.timeToCreatePipe-=TIME_TO_SPAWN_PIPES;
                float posRandomY = MathUtils.random(0f, 2f);
                //Cambiamos la coordenada x para que se cree fuera de la pantalla (5f)
                Pipes pipes = new Pipes(this.world, pipeDownTexture, pipeTopTexture, new Vector2(5f, posRandomY)); //Posición de la tubería inferior
                arrayPipes.add(pipes);
                this.stage.addActor(pipes);
            }
        }
    }


    public void removePipes(){
        for (Pipes pipe : this.arrayPipes) {

            if(!world.isLocked()) {
                if(pipe.isOutOfScreen()) {
                    pipe.detach();
                    pipe.remove();
                    arrayPipes.removeValue(pipe,false);
                }
            }
        }
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        addPipes(delta);


        this.stage.getBatch().setProjectionMatrix(worldCamera.combined);
        this.stage.act();
        this.world.step(delta,6,2);
        this.stage.draw();

        this.debugRenderer.render(this.world, this.worldCamera.combined);

        removePipes();

        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.score.draw(this.stage.getBatch(), ""+this.scoreNumber,SCREEN_WIDTH/2, 725);
        this.stage.getBatch().end();
    }



    @Override
    public void hide() {
        this.bird.detach();
        this.bird.remove();

        this.musicbg.stop();
    }

    @Override
    public void dispose() {

        this.stage.dispose();
        this.world.dispose();
    }



    /// ********************************************* ///
    /// *************** COLISIONES ****************** ///
    /// ********************************************* ///
    //Todo 6. Nos creamos un método auxiliar areColider, que nos ayude a determinar qué objetos han colisionado
    private boolean areColider(Contact contact, Object objA, Object objB) {

        return (contact.getFixtureA().getUserData().equals(objA) && contact.getFixtureB().getUserData().equals(objB)) ||
                (contact.getFixtureA().getUserData().equals(objB) && contact.getFixtureB().getUserData().equals(objA));
    }

    //Método que se llamará cada vez que se produzca cualquier contacto
    @Override
    public void beginContact(Contact contact) {

        //Todo 7. Si 'han colisionado' el pájaro con el contador sumamos 1 al contador...
        if (areColider(contact, USER_BIRD, USER_COUNTER)) {
            this.scoreNumber++;
        } else {
            //Todo 8 En cualquier otro caso significaría que el pájaro ha colisionado con algún otro elemento y se acaba la partida
            //Todo 8.1 Lanzamos el método hurt del pájaro para que se cambie el estado a DEAD
            bird.hurt();
            this.hitSound.play();
            //Todo 8.2 Paramos la música y lanzamos sonido de gameOver
            this.musicbg.stop();
            this.gameOverSound.play();
            //Todo 8.3 Recorremos el array de Pipes para parar los que se encuentren creados en este momento
            for (Pipes pipe : arrayPipes) {
                pipe.stopPipes();
            }

            //Todo 8.4 Se lanza la secuencia de acciones,cuya última será el pasar a la ventana de GameOverScreen
            this.stage.addAction(Actions.sequence(
                    Actions.delay(1.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            mainGame.setScreen(mainGame.gameOverScreen);
                        }
                    })

            ));

        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
