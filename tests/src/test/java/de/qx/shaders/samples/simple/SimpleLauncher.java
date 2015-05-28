package de.qx.shaders.samples.simple;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * @author Michael "molp" Olp
 */
public class SimpleLauncher {

    public static void main(String[] args) {
        final LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.width = 500;
        configuration.height = 400;

        new LwjglApplication(new Simple(), configuration);
    }
}
