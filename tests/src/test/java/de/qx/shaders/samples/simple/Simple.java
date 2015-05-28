package de.qx.shaders.samples.simple;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import de.qx.shaders.ShaderLoader;

/**
 * @author Michael "molp" Olp
 */
public class Simple extends Game {

    @Override
    public void create() {
        final LocalFileHandleResolver localFileHandleResolver = new LocalFileHandleResolver();
        final AssetManager assetManager = new AssetManager(localFileHandleResolver);
        setScreen(new SimpleScreen());
    }

    @Override
    public void render() {
        super.render();
    }
}
