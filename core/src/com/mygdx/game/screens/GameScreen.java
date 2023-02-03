package com.mygdx.game.screens;

import static com.mygdx.game.extra.Utils.SCREEN_HEIGTH;
import static com.mygdx.game.extra.Utils.SCREEN_WIDTH;
import static com.mygdx.game.extra.Utils.USER_FLOOR;
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
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.MainGame;
import com.mygdx.game.actors.Bird;
import com.mygdx.game.actors.Pipes;

public class GameScreen  extends BaseScreen{


    private  static final float TIME_TO_SPAWN_PIPES = 1.5f;
    private float timeToCreatePipe;
    private Stage stage;
    private Bird bird;

    private Image background;

    private World world;


    private Array<Pipes> arrayPipes;

    private Music musicbg;

    //Depuración
    private Box2DDebugRenderer debugRenderer;

    //Todo 0. Cambiamos el nombre de ortCamera a worldCamera para diferenciarla con la cámara de la puntuación
    private OrthographicCamera worldCamera;

    //Todo 1: Para añadir un texto con la puntuación es necesario una cámara extra,
    // ya que las fuentes son uno de los pocos elementos que no se pueden añadir en función
    // de las medidas del mundo, sino que se hará en función de las medidas de la pantalla.
    // Para ello necesitaremos otra cámara que proyectarán simultaneamente, una el mundo del juego
    // y otra solo la fuente con la puntuación. Así como crearnos un Bitmap font para manejar el texto
    private OrthographicCamera fontCamera;
    private BitmapFont score;


    public GameScreen(MainGame mainGame) {
        super(mainGame);

        this.world = new World(new Vector2(0,-10), true);
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage = new Stage(fitViewport);


        this.arrayPipes = new Array();
        this.timeToCreatePipe = 0f;

        this.musicbg = this.mainGame.assetManager.getMusicBG();
        this.worldCamera = (OrthographicCamera) this.stage.getCamera();
        this.debugRenderer = new Box2DDebugRenderer();

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
        Body body = world.createBody(bodyDef);
        body.setUserData(USER_FLOOR);

        PolygonShape edge = new PolygonShape();
        edge.setAsBox(2.3f, 0.5f);
        body.createFixture(edge, 3);
        edge.dispose();
    }

    public void addRoof(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        EdgeShape edge = new EdgeShape();
        edge.set(0,WORLD_HEIGHT,WORLD_WIDTH,WORLD_HEIGHT);
        body.createFixture(edge, 1);
        edge.dispose();
    }

    //Creamos un método para configurar tod o lo relacionado con el texto de la puntuación
    //Todo 4.1 Nos acordamos de llamar a dicho método en el constructor
    private void prepareScore(){
        //Todo 3. Cargamos la fuente y configuramos la escala (vamos probando el tamaño
        this.score = this.mainGame.assetManager.getFont();
        this.score.getData().scale(1f);

        //Todo 4. Creamos la cámara, y se le da el tamaño de la PANTALLA (EN PIXELES) y luego se actualiza
        this.fontCamera = new OrthographicCamera();
        this.fontCamera.setToOrtho(false, SCREEN_WIDTH,SCREEN_HEIGTH);
        this.fontCamera.update();

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

        //Todo 5.1 Justo antes de dibujar el mundo, le volvemos a pasar al batch, los datos de
        // la cámara del mundo, para que vuelva a representar todo en función del tamaño de este
        this.stage.getBatch().setProjectionMatrix(worldCamera.combined);
        this.stage.act();
        this.world.step(delta,6,2);
        this.stage.draw();

        this.debugRenderer.render(this.world, this.worldCamera.combined);

        removePipes();

        //Todo 5.Cargamos la matriz de proyección con los datos de la cámara de la fuente,
        // para que proyecte el texto con las dimensiones en píxeles
        this.stage.getBatch().setProjectionMatrix(this.fontCamera.combined);
        this.stage.getBatch().begin();
        this.score.draw(this.stage.getBatch(), ""+arrayPipes.size,SCREEN_WIDTH/2, 725);
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
}
