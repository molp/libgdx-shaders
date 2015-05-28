package de.qx.shaders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by michi on 27.05.2015.
 */
public class ShaderManager {

    private final AssetManager assetManager;

    private final ArrayMap<String, ShaderProgram> shaders;
    private final ArrayMap<String, String> shaderPaths;
    private ShaderProgram currentShader;

    public ShaderManager(AssetManager assetManager) {
        this.assetManager = assetManager;

        final LocalFileHandleResolver localFileHandleResolver = new LocalFileHandleResolver();
        assetManager.setLoader(ShaderProgram.class, new ShaderLoader(localFileHandleResolver));

        shaders = new ArrayMap<>();
        shaderPaths = new ArrayMap<>();
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

        currentShader = program;
        currentShader.begin();
    }

    public void end() {
        currentShader.end();
        currentShader = null;
    }

    public ShaderProgram getCurrentShader() {
        return currentShader;
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
    }

    public void setUniformMatrix(String uniformName, Matrix4 matrix) {
        if (currentShader == null) {
            new GdxRuntimeException("Please call begin() before setting uniforms");
        }
        currentShader.setUniformMatrix(uniformName, matrix);
    }

    public void setUniformf(String uniformName, float value) {
        if (currentShader == null) {
            new GdxRuntimeException("Please call begin() before setting uniforms");
        }
        currentShader.setUniformf(uniformName, value);
    }
}
