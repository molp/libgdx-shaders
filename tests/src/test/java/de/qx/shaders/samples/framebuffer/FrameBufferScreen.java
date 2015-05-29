package de.qx.shaders.samples.framebuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import de.qx.shaders.ShaderManager;

/**
 * @author Michael "molp" Olp
 */
public class FrameBufferScreen implements Screen {
    private AssetManager assetManager;
    private boolean assetsLoaded;

    private final SpriteBatch batch;
    private ShaderManager shaderManager;

    private TextureRegion region;
    private Mesh rectangle;

    public FrameBufferScreen() {
        assetManager = new AssetManager();
        assetManager.load("img/lena.png", Texture.class);

        shaderManager = new ShaderManager(assetManager);
        shaderManager.loadShader("black_and_white", "default.vert", "blackwhite.frag");
        shaderManager.loadShader("default", "default.vert", "default.frag");

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!assetsLoaded) {
            if (assetManager.update()) {
                assetsLoaded = true;

                build();
            }
        } else {
            // render the image into the framebuffer using the black and white shader
            shaderManager.beginFrameBuffer("frameBuffer01");
            {
                shaderManager.begin("black_and_white");
                shaderManager.setUniformf("u_blackWhite", 0.75f);
                shaderManager.setUniformMatrix("u_worldView", batch.getProjectionMatrix());

                batch.setShader(shaderManager.getCurrentShader());
                batch.begin();
                batch.draw(region, 0f, 0f);
                batch.end();

                shaderManager.end();
            }
            shaderManager.endFrameBuffer();

            // render the framebuffer onto a rectangle on screen
            shaderManager.begin("default");
            shaderManager.renderFrameBuffer("frameBuffer01", rectangle);
            shaderManager.end();
        }
    }

    private void build() {
        region = new TextureRegion(assetManager.<Texture>get("img/lena.png"));

        shaderManager.createFrameBuffer("frameBuffer01");

        final MeshBuilder meshBuilder = new MeshBuilder();
        meshBuilder.begin(VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates, GL20.GL_TRIANGLES);
        meshBuilder.rect(
                0f, 200f, 0f,
                200f, 200f, 0f,
                200f, 0f, 0f,
                0f, 0f, 0f,
                0f, 0f, 0f);
        rectangle = meshBuilder.end();
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
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shaderManager.dispose();

        assetManager.unload("img/lena.png");
        assetManager.dispose();

        rectangle.dispose();
    }
}
