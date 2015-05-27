package de.qx.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

/**
 * {@link com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader} for {@link ShaderProgram} instances. The shader program data is loaded asynchronously. The shader
 * is then created on the rendering thread, synchronously.
 *
 * @author Szymon "Veldrin" Jabłoński
 */
public class ShaderLoader extends AsynchronousAssetLoader<ShaderProgram, ShaderParameter> {

    private String vertProgram;
    private String fragProgram;

    public ShaderLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, ShaderParameter parameter) {
        return null;
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, ShaderParameter parameter) {
        String vertexPath = fileName.substring(0, fileName.indexOf("+"));
        String fragmentPath = fileName.substring(fileName.indexOf("+") + 1, fileName.length());

        FileHandle vertexFile = Gdx.files.internal(vertexPath);
        FileHandle fragmentFile = Gdx.files.internal(fragmentPath);

        if (vertexFile.exists() && fragmentFile.exists()) {
            vertProgram = vertexFile.readString();
            fragProgram = fragmentFile.readString();
        }
    }

    @Override
    public ShaderProgram loadSync(AssetManager manager, String fileName, FileHandle file, ShaderParameter parameter) {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(vertProgram, fragProgram);
        return shader;
    }
}
