package fr.threedijnns.api.lang.stream.engine;

import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public interface IFrameStream {

    StreamHandler2D getHandler();

    void 			bind();
    void 			swapBuffers();
    void 			destroy();

}
