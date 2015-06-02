# libgdx-shaders
Tools to simplify shader and framebuffer usage with libGDX

libgdx-shaders is greatly inspired by an [existing project](https://bitbucket.org/Thotep/gdx-shaders/wiki/Home) and improves that project by adding gradle support and proper asset loading via the [AssetLoader](https://github.com/libgdx/libgdx/blob/955bbe62a099fcf41871fa1f504a3fa63e141fad/gdx/src/com/badlogic/gdx/assets/AssetManager.java).

A quick example:

    // loading shaders
    ShaderManager shaderManager = new ShaderManager(assetManager);
    shaderManager.loadShader("black_and_white", "default.vert", "blackwhite.frag");
    shaderManager.loadShader("default", "default.vert", "default.frag");
    
    // setup a framebuffer
    shaderManager.createFrameBuffer("frameBuffer01");
    
    // render the image into the framebuffer using the black and white shader
    shaderManager.beginFrameBuffer("frameBuffer01");
    {
        shaderManager.begin("black_and_white");
        shaderManager.setUniformf("u_blackWhite", 0.75f);
        shaderManager.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
    
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
