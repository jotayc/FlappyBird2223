package com.mygdx.game.screens;

import static com.mygdx.game.extra.Utils.USER_FLOOR;
import static com.mygdx.game.extra.Utils.WORLD_HEIGHT;
import static com.mygdx.game.extra.Utils.WORLD_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
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

    //Todo 1* Tenemos que crear una constante para indicar cada cuanto tiempo queremos que se cree
    // una tubería
    private  static final float TIME_TO_SPAWN_PIPES = 1.5f;
    private float timeToCreatePipe;
    private Stage stage;
    private Bird bird;

    private Image background;

    private World world;

    //Todo 1.1* Borramos el atributo unico Pipes, y creamos un array de Pipes. ATENCIÓN SE USA LA CLASE 'Array' de la biblioteca de LIBGDX NO DE JAVA!!!!
    private Array<Pipes> arrayPipes;

    private Music musicbg;


    //Depuración
    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera ortCamera;

    public GameScreen(MainGame mainGame) {
        super(mainGame);

        this.world = new World(new Vector2(0,-10), true);
        FitViewport fitViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.stage = new Stage(fitViewport);

        //Todo 1.2. Inicializamos el array y la variable que almacenará el tiempo
        this.arrayPipes = new Array();
        this.timeToCreatePipe = 0f;

        this.musicbg = this.mainGame.assetManager.getMusicBG();
        this.ortCamera = (OrthographicCamera) this.stage.getCamera();
        this.debugRenderer = new Box2DDebugRenderer();
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

    //Todo 2.Creamos un método para crear Pipes
    public void addPipes(float delta){

        TextureRegion pipeDownTexture = mainGame.assetManager.getPipeDownTR();
        TextureRegion pipeTopTexture = mainGame.assetManager.getPipeTopTR();

        //Como ambas tuberías están en la misma clase solo debemos instanciar un objeto

        if(bird.getState() == Bird.STATE_NORMAL) {
            //Todo 3. Acumulamos delta hasta que llegue al tiempo que hemos establecido para que cree la siguiente tubería.
            this.timeToCreatePipe+=delta;
            //Todo 4. Si el tiempo acumulado es mayor que el tiempo que hemos establecido, se crea una tubería...
            if(this.timeToCreatePipe >= TIME_TO_SPAWN_PIPES) {
                //Todo 4.1 ... y le restamos el tiempo a la variable acumulada para que vuelva el contador a 0.
                this.timeToCreatePipe-=TIME_TO_SPAWN_PIPES;
                float posRandomY = MathUtils.random(0f, 2f);
                //Cambiamos la coordenada x para que se cree fuera de la pantalla (5f)
                Pipes pipes = new Pipes(this.world, pipeDownTexture, pipeTopTexture, new Vector2(5f, posRandomY)); //Posición de la tubería inferior
                arrayPipes.add(pipes);
                this.stage.addActor(pipes);
            }
        }
    }

    //Todo 6.Creamos un método para eliminar pipes
    public void removePipes(){
        for (Pipes pipe : this.arrayPipes) {
            //Todo 6.1 Si el mundo no está bloqueado, es decir, que no esté actualizando la física en ese preciso momento...
            if(!world.isLocked()) {
                //Todo 6.2...y la tubería en cuestión está fuera de la pantalla.
                if(pipe.isOutOfScreen()) {
                    //Todo 6.3 Eliminamos los recursos
                    pipe.detach();
                    //Todo 6.4 La eliminamos del escenario
                    pipe.remove();

                    //Todo 6.5 La eliminamos del array
                    arrayPipes.removeValue(pipe,false);
                }
            }
        }
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Todo 7.Añadimos las tuberías en función del tiempo (delta)
        addPipes(delta);

        this.stage.act();
        this.world.step(delta,6,2);
        this.stage.draw();

        this.debugRenderer.render(this.world, this.ortCamera.combined);

        //Todo 8 Final. Eliminamos las tuberías que vayan saliendose de la pantalla
        removePipes();
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
