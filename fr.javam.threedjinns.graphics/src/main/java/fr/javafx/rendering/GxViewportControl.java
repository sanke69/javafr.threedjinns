package fr.javafx.rendering;

import fr.javafx.rendering.skin.RenderableControlSkin;
import fr.javafx.scene.control.AbstractFxControlWithFPS;
import fr.javafx.scene.image.DirectWritableImage;
import fr.threedijnns.gx;
import fr.threedijnns.api.graphics.GxViewport;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.engine.renderers.GxRenderer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

public class GxViewportControl extends AbstractFxControlWithFPS implements GxViewport {
	StreamHandler2D							  	streamHandler;
	SimpleObjectProperty<DirectWritableImage> 	buffer;
	IFrameStream 								stream;

	GxRenderer 	 								renderer;

	public GxViewportControl(int _bufferWidth, int _bufferHeight) {
		this(null, _bufferWidth, _bufferHeight);
	}
	public GxViewportControl(GxRenderer _renderer, int _bufferWidth, int _bufferHeight) {
		super(60);
		renderer = _renderer;
		buffer   = new SimpleObjectProperty<DirectWritableImage>(new DirectWritableImage(_bufferWidth, _bufferHeight));

		gx.runLater(() -> stream = gx.registerViewport(this));
	}

	@Override
	protected Skin<GxViewportControl> 	createDefaultSkin() {
		return new RenderableControlSkin(this);
	}

	public void 						refresh() {
		
	}
	
	@Override
	public GxRenderer 					getRenderer() {
		return renderer;
	}

	@Override
	public IFrameStream 				getRenderStream() {
		return stream;
	}

	@Override
	public StreamHandler2D 				getRenderingStream() {
		if(streamHandler == null)
			streamHandler = new DoubleBufferFrameHandler(buffer);
		return streamHandler;
	}

	public ReadOnlyObjectProperty<DirectWritableImage> bufferImageProperty() {
		return buffer;
	}

}
