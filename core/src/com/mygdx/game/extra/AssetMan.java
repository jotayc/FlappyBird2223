package com.mygdx.game.extra;

import static com.mygdx.game.extra.Utils.ATLAS_MAP;
import static com.mygdx.game.extra.Utils.BACKGROUND_IMAGE;
import static com.mygdx.game.extra.Utils.BIRD1;
import static com.mygdx.game.extra.Utils.BIRD2;
import static com.mygdx.game.extra.Utils.BIRD3;
import static com.mygdx.game.extra.Utils.FONT_FNT;
import static com.mygdx.game.extra.Utils.FONT_PNG;
import static com.mygdx.game.extra.Utils.GAMEOVER_SOUND;
import static com.mygdx.game.extra.Utils.HIT_SOUND;
import static com.mygdx.game.extra.Utils.MUSIC_BG;
import static com.mygdx.game.extra.Utils.PIPE_DOWN;
import static com.mygdx.game.extra.Utils.PIPE_UP;
import static com.mygdx.game.extra.Utils.SOUND_JUMP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetMan {

    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public AssetMan(){
        this.assetManager = new AssetManager();

        assetManager.load(ATLAS_MAP, TextureAtlas.class);

        assetManager.load(MUSIC_BG, Music.class);
        assetManager.load(SOUND_JUMP, Sound.class);
        assetManager.load(HIT_SOUND, Sound.class);
        assetManager.load(GAMEOVER_SOUND, Sound.class);
        assetManager.finishLoading();

        this.textureAtlas = assetManager.get(ATLAS_MAP);
    }
    //IMAGEN DE FONDO
    public TextureRegion getBackground(){
        return this.textureAtlas.findRegion(BACKGROUND_IMAGE);
    }

    //ANIMACIÓN PÁJARO
    public Animation<TextureRegion> getBirdAnimation(){
        return new Animation<TextureRegion>(0.33f,
                textureAtlas.findRegion(BIRD1),
                textureAtlas.findRegion(BIRD2),
                textureAtlas.findRegion(BIRD3));

    }

    //Textura de las tuberías
    public TextureRegion getPipeDownTR(){
        return this.textureAtlas.findRegion(PIPE_DOWN);
    }

    public TextureRegion getPipeTopTR(){
        return this.textureAtlas.findRegion(PIPE_UP);
    }

    public Sound getJumpSound(){
        return this.assetManager.get(SOUND_JUMP);
    }

    public Sound getHitSound(){
        return this.assetManager.get(HIT_SOUND);
    }
    public Sound getGameOverSound(){
        return this.assetManager.get(GAMEOVER_SOUND);
    }

    public Music getMusicBG(){
        return this.assetManager.get(MUSIC_BG);
    }


    //Crear en utils las variables para los identificadores de los archivos.
    public BitmapFont getFont(){
        return new BitmapFont(Gdx.files.internal(FONT_FNT),Gdx.files.internal(FONT_PNG), false);
    }


}














