package de.qx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.Stack;

/**
 * Created by michi on 27.05.2015.
 */
public class ShaderManager {

    private final AssetManager assetManager;

    private final ArrayMap<String, ShaderProgram> shaders;
    private final ArrayMap<String, String> shaderPaths;
    private ShaderProgram currentShader;

    private final ArrayMap<String, FrameBuffer> frameBuffers;
    private final Stack<FrameBuffer> activeFrameBuffers;

    private final Camera screenCamera;
    private final Mesh screenMesh;
    private int currentTextureId;

    public ShaderManager(AssetManager assetManager) {
        this.assetManager = assetManager;

        final LocalFileHandleResolver localFileHandleResolver = new LocalFileHandleResolver();
        assetManager.setLoader(ShaderProgram.class, new ShaderLoader(localFileHandleResolver));

        shaders = new ArrayMap<>();
        shaderPaths = new ArrayMap<>();

        frameBuffers = new ArrayMap<>();
        activeFrameBuffers = new Stack<>();

        screenCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        screenMesh = new Mesh(true, 4, 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

        Vector3 vec0 = new Vector3(0, 0, 0);
        screenCamera.unproject(vec0);
        Vector3 vec1 = new Vector3(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0);
        screenCamera.unproject(vec1);
        screenMesh.setVertices(new float[]{
                vec0.x, vec0.y, 0, 1, 1, 1, 1, 0, 1,
                vec1.x, vec0.y, 0, 1, 1, 1, 1, 1, 1,
                vec1.x, vec1.y, 0, 1, 1, 1, 1, 1, 0,
                vec0.x, vec1.y, 0, 1, 1, 1, 1, 0, 0});
        screenMesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
        screenCamera.translate(0f, -1f, 0f);
        screenCamera.update();
    }

    public void loadShader(String shaderName, String vertexShader, String fragmentShader) {
        final String shaderPath = vertexShader + "+" + fragmentShader;
        shaderPaths.put(shaderName, shaderPath);
        assetManager.load(shaderPath, ShaderProgram.class);
    }

    public void begin(String shaderName) {
        // check if we have a shader that has not been end()ed
        if (currentShader != null) {
            throw new IllegalArgumentException("Before calling begin() for a new shader please call end() for the current one!");
        }
        // check if we have a program for that name
        ShaderProgram program = shaders.get(shaderName);

        if (program == null) {
            // check if we have loaded that shader program
            // if not this line will through a runtime exception
            program = assetManager.get(shaderPaths.get(shaderName), ShaderProgram.class);
            shaders.put(shaderName, program);
        }

        currentTextureId = 0;

        currentShader = program;
        currentShader.begin();
    }

    public void end() {
        currentShader.end();
        currentShader = null;
    }

    public void createFrameBuffer(String frameBufferName) {
        createFrameBuffer(frameBufferName, Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void createFrameBuffer(String frameBufferName, Pixmap.Format format, int width, int height) {
        if (frameBuffers.containsKey(frameBufferName)) {
            throw new IllegalArgumentException("A framebuffer with the name '" + frameBufferName + "' already exists");
        }
        FrameBuffer frameBuffer = new FrameBuffer(format, width, height, true);
        frameBuffers.put(frameBufferName, frameBuffer);
    }

    public void beginFrameBuffer(String frameBufferName) {
        final FrameBuffer frameBuffer = frameBuffers.get(frameBufferName);
        frameBuffer.begin();
        activeFrameBuffers.push(frameBuffer);

        Gdx.graphics.getGL20().glViewport(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
        Gdx.graphics.getGL20().glClearColor(1f, 1f, 1f, 0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.graphics.getGL20().glEnable(GL20.GL_TEXTURE_2D);
        Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
        Gdx.graphics.getGL20().glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void endFrameBuffer() {
        if (activeFrameBuffers.empty()) {
            throw new GdxRuntimeException("There is no active frame buffer that can be ended");
        }
        final FrameBuffer frameBuffer = activeFrameBuffers.pop();
        frameBuffer.end();
    }

    /**
     * Renders the given FrameBuffer to the screen using the current ShaderProgram
     *
     * @param frameBufferName name of the FrameBuffer to render
     */
    public void renderFrameBuffer(String frameBufferName) {
        renderFrameBuffer(frameBufferName, screenMesh);
    }

    /**
     * Renders the given FrameBuffer onto a Mesh using the current ShaderProgram
     *
     * @param frameBufferName name of the FrameBuffer to render
     * @param target          Mesh to render onto
     */
    public void renderFrameBuffer(String frameBufferName, Mesh target) {
        if (currentShader == null) {
            throw new GdxRuntimeException("Rendering the frame buffers needs an active shader");
        }
        FrameBuffer frameBuffer = frameBuffers.get(frameBufferName);
        if (frameBuffer == null) {
            throw new GdxRuntimeException("A framebuffer with the name '" + frameBufferName + "' could not be found");
        }
        frameBuffer.getColorBufferTexture().bind(0);
        currentShader.setUniformMatrix("u_worldView", screenCamera.combined);
        currentShader.setUniformi("u_texture", 0);
        target.render(currentShader, GL20.GL_TRIANGLES);
    }

    public FrameBuffer getFrameBuffer(String frameBufferName) {
        return frameBuffers.get(frameBufferName);
    }

    public ShaderProgram getCurrentShader() {
        return currentShader;
    }

    public void setUniformMatrix(String uniformName, Matrix4 matrix) {
        if (currentShader == null) {
            throw new GdxRuntimeException("Please call begin() before setting uniforms");
        }
        currentShader.setUniformMatrix(uniformName, matrix);
    }

    public void setUniformf(String uniformName, float value) {
        if (currentShader == null) {
            throw new GdxRuntimeException("Please call begin() before setting uniforms");
        }
        currentShader.setUniformf(uniformName, value);
    }

    public void setUniform2fv(String uniformName, float[] values) {
        if (currentShader == null) {
            throw new GdxRuntimeException("Please call begin() before setting uniforms");
        }
        currentShader.setUniform2fv(uniformName, values, 0, 2);
    }

    public void setUniformTexture(String uniformName, Texture texture) {
        if (currentShader == null) {
            throw new GdxRuntimeException("Please call begin() before setting uniforms");
        }

        int textureId = ++currentTextureId;
        texture.bind(textureId);
        currentShader.setUniformi(uniformName, textureId);
    }

    public void dispose() {
        for (String path : shaderPaths.values()) {
            assetManager.unload(path);
        }
        shaderPaths.clear();

        for (ShaderProgram program : shaders.values()) {
            program.dispose();
        }
        shaders.clear();

        screenMesh.dispose();
    }

}
