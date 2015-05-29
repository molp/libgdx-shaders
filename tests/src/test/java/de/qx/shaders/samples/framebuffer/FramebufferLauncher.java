package de.qx.shaders.samples.framebuffer;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * @author Michael "molp" Olp
 */
public class FramebufferLauncher {
    public static final void main(String[] args) {
        final LwjglApplicationConfiguration configuration = new LwjglApplicationConfiguration();
        configuration.width = 500;
        configuration.height = 400;

        new LwjglApplication(new Game() {
            @Override
            public void create() {
                setScreen(new FrameBufferScreen());
            }
        }, configuration);
    }
}
