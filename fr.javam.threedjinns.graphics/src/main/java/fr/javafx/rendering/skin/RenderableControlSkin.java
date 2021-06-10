package fr.javafx.rendering.skin;

import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.image.ImageView;

import fr.javafx.rendering.GxViewportControl;

public class RenderableControlSkin extends ImageView implements Skin<GxViewportControl> {
	private GxViewportControl control;

	public RenderableControlSkin(GxViewportControl _control) {
		super();
		control = _control;
		imageProperty().bind(control.bufferImageProperty());
		fitWidthProperty().bind(control.widthProperty());
		fitHeightProperty().bind(control.heightProperty());
	}

	@Override
	public GxViewportControl getSkinnable() {
		return control;
	}

	@Override
	public Node getNode() {
		return this;
	}

	@Override
	public void dispose() {
		imageProperty().unbind();
	}

}
