package de.qx.shaders.samples.simple;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.qx.shaders.ShaderManager;

/**
 * @author Michael "molp" Olp
 */
public class SimpleScreen implements Screen {
    private AssetManager assetManager;
    private boolean assetsLoaded;

    private ShaderManager shaderManager;

    private final SpriteBatch batch;

    private TextureRegion region;

    public SimpleScreen() {
        assetManager = new AssetManager();

        assetManager.load("img/lena.png", Texture.class);

        shaderManager = new ShaderManager(assetManager);
        shaderManager.loadShader("black_and_white", "default.vert", "blackwhite.frag");

        batch = new SpriteBatch();
    }

    @Override
    public void render(float dt) {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!assetsLoaded) {
            if (assetManager.update()) {
                assetsLoaded = true;

                build();
            }
        } else {
            shaderManager.begin("black_and_white");
            batch.begin();
            batch.setShader(shaderManager.getCurrentShader());

            shaderManager.setUniformf("u_blackWhite", 0.75f);
            batch.draw(region, 0f, 0f);

            batch.end();
            shaderManager.end();
        }
    }

    private void build() {
        region = new TextureRegion(assetManager.<Texture>get("img/lena.png"));
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        shaderManager.dispose();

        assetManager.unload("img/lena.png");

        assetManager.dispose();
    }
}
