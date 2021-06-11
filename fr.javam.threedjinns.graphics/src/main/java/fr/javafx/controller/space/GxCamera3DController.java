package fr.javafx.controller.space;

import java.util.Optional;

import fr.java.math.geometry.plane.Point2D;
import fr.java.maths.geometry.types.Points;
import fr.javafx.controller.GxCameraController;
import fr.threedijnns.api.interfaces.nodes.space.GxCamera3D;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * 
 * @author sanke
 *
 * @see fr.javafx.controller.space.behaviors.FreeFlightController
 * @see fr.javafx.controller.space.behaviors.ObjectCenterController
 */
public abstract class GxCamera3DController extends GxCameraController<GxCamera3D> {

	protected GxCamera3DController(Node _viewport, GxCamera3D _camera) {
		super(_viewport, _camera);
		if(_viewport != null)
			enable();
	}

	protected Optional<EventHandler<KeyEvent>> keyEventHandler() {
		return Optional.empty();
	}
	protected Optional<EventHandler<MouseEvent>> mouseEventHandler() {
		return Optional.empty();
	}
	protected Optional<EventHandler<ScrollEvent>> scrollEventHandler() {
		return Optional.empty();
	}

	private double previousX, previousY;
	protected void resetMouse(MouseEvent event) {
		previousX = event.getSceneX();
		previousY = event.getSceneY();
	}
	protected Point2D getMouseDelta(MouseEvent event) {
		Point2D res = Points.of(event.getSceneX() - previousX, event.getSceneY() - previousY);
		previousX = event.getSceneX();
		previousY = event.getSceneY();
		return res;
	}

}
