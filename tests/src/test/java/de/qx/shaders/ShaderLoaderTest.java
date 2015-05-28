package de.qx.shaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.libgdxtesting.GdxTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Michael "molp" Olp
 */
@RunWith(GdxTestRunner.class)
public class ShaderLoaderTest {

    @Test
    public void testShaderLoading() {
        final LocalFileHandleResolver localFileHandleResolver = new LocalFileHandleResolver();
        final AssetManager assetManager = new AssetManager(localFileHandleResolver);

        assetManager.setLoader(ShaderProgram.class, new ShaderLoader(localFileHandleResolver));

        assetManager.load("default.vert+default.frag", ShaderProgram.class);
        assetManager.finishLoading();

        final ShaderProgram program = assetManager.get("default.vert+default.frag", ShaderProgram.class);
        Assert.assertNotNull(program);

        assetManager.unload("default.vert+default.frag");
    }
}
