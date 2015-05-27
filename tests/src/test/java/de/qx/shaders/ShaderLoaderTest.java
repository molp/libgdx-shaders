package de.qx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.libgdxtesting.GdxTestRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael "molp" Olp
 */
@RunWith(GdxTestRunner.class)
public class ShaderLoaderTest {

    /*@BeforeClass
    public static void setup() {
        /*final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.renderInterval = 1f / 60f;
        new HeadlessApplication(new TestApplication(), config);*/
   /*     final LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.width = 100;
        configuration.height = 100;

        new LwjglApplication(new TestApplication(), configuration);
    }*/

    @Test
    public void testShaderLoading() {
        final LocalFileHandleResolver localFileHandleResolver = new LocalFileHandleResolver();
        final AssetManager assetManager = new AssetManager(localFileHandleResolver);

        assetManager.setLoader(ShaderProgram.class, new ShaderLoader(localFileHandleResolver));

        assetManager.load("basic.vert+basic.frag", ShaderProgram.class);
        assetManager.finishLoading();

        final ShaderProgram program = assetManager.get("basic.vert+basic.frag", ShaderProgram.class);
        Assert.assertNotNull(program);

        assetManager.unload("basic.vert+basic.frag");
    }
}
