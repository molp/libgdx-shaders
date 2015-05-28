package de.qx.shaders.sample;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import de.qx.shaders.ShaderLoader;

/**
 * @author Michael "molp" Olp
 */
public class Sample extends Game {

    @Override
    public void create() {
        final LocalFileHandleResolver localFileHandleResolver = new LocalFileHandleResolver();
        final AssetManager assetManager = new AssetManager(localFileHandleResolver);

        assetManager.setLoader(ShaderProgram.class, new ShaderLoader(localFileHandleResolver));

        assetManager.load("basic.vert+basic.frag", ShaderProgram.class);

        setScreen(new SampleScreen());
    }

    @Override
    public void render() {
        super.render();
    }
}
