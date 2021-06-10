package fr.javafx.controller;

import java.util.Optional;

import fr.java.math.geometry.plane.Point2D;
import fr.java.maths.Points;
import fr.threedijnns.api.interfaces.nodes.GxCamera;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public abstract class GxCameraController<C extends GxCamera<?,?>> {
	protected final C 		camera;
	protected final Node 	viewport;

	protected GxCameraController(Node _viewport, C _camera) {
		super();
		camera   = _camera;
		viewport = _viewport;
		if(_viewport != null)
			enable();
	}

	public final C getCamera() {
		return camera; 
	}

	protected final void enable() {
		if(keyEventHandler().isPresent())
			viewport.addEventHandler(KeyEvent.ANY, keyEventHandler().get());
		if(mouseEventHandler().isPresent())
			viewport.addEventHandler(MouseEvent.ANY, mouseEventHandler().get());
		if(scrollEventHandler().isPresent())
			viewport.addEventHandler(ScrollEvent.ANY, scrollEventHandler().get());
	}
	protected final void disable() {
		if(keyEventHandler().isPresent())
			viewport.removeEventHandler(KeyEvent.ANY, keyEventHandler().get());
		if(mouseEventHandler().isPresent())
			viewport.removeEventHandler(MouseEvent.ANY, mouseEventHandler().get());
		if(scrollEventHandler().isPresent())
			viewport.removeEventHandler(ScrollEvent.ANY, scrollEventHandler().get());
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
