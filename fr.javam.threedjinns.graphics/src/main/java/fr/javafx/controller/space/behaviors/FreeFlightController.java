/** ************************************************************************ **\
 * Copyright (c) 2007-?XYZ Steve PECHBERTI                                    *
 *                                                                            *
 * @author <a href='mailto:steve.pechberti@gmail.com'> Steve PECHBERTI </a>   *
 *                                                                            *
 * @section license License                                                   *
 *    [EN] This program is free software:                                     *
 *         you can redistribute it and/or modify it under the terms of        * 
 *         the GNU General Public License as published by                     *
 *         the Free Software Foundation, either version 3 of the License, or  *
 *         (at your option) any later version.                                *
 *         You should have received a copy of the GNU General Public License  *
 *         along with this program. If not, see                               *
 *            <http://www.gnu.org/licenses/gpl.html>                          *
 *    [FR] Ce programme est un logiciel libre ; vous pouvez le redistribuer   * 
 *         ou le modifier suivant les termes de la GNU General Public License *
 *         telle que publiée par la Free Software Foundation ;                *
 *         soit la version 3 de la licence, soit (à votre gré) toute version  *
 *         ultérieure.                                                        *
 *         Vous devez avoir reçu une copie de la GNU General Public License   *
 *         en même temps que ce programme ; si ce n'est pas le cas, consultez *
 *            <http://www.gnu.org/licenses/gpl.html>                          *
 *                                                                            *
 * @section disclaimer Disclaimer                                             *
 *    [EN] This program is distributed in the hope that it will be useful,    *
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.               *
 *    [FR] Ce programme est distribué dans l'espoir qu'il sera utile,         *
 *         mais SANS AUCUNE GARANTIE, sans même la garantie implicite de      *
 *         VALEUR MARCHANDE ou FONCTIONNALITE POUR UN BUT PARTICULIER.        *
 *                                                                            *
\** ************************************************************************ **/
package fr.javafx.controller.space.behaviors;

import java.util.Optional;

import fr.java.math.geometry.plane.Point2D;
import fr.java.maths.Points;
import fr.java.maths.algebra.Vectors;
import fr.javafx.controller.space.GxCamera3DController;
import fr.threedijnns.api.interfaces.nodes.space.GxCamera3D;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class FreeFlightController extends GxCamera3DController {

	private double speed = 1.0;

	public FreeFlightController(Node _viewport, GxCamera3D _camera) {
		super(_viewport, _camera);
	}

	protected double getSpeedModifier(KeyEvent event) {
		return event.isShiftDown() ? 100.0 : event.isControlDown() ? 0.1 : 1.0;
	}

	protected Optional<EventHandler<KeyEvent>>   keyEventHandler() {
		return Optional.of((evt) -> {
			if(camera == null)
				return ;

			int mode = evt.getEventType() == KeyEvent.KEY_PRESSED ? 1 : (evt.getEventType() == KeyEvent.KEY_RELEASED) ? 0 : -1;
			if(mode == -1)
				return ;

			float step = (float) getSpeedModifier(evt);
			switch(evt.getCode()) {
			case Z: 		this.camera.move(step);
							break;
			case S: 		this.camera.move(- step);
							break;
			case Q: 		this.camera.strafe(- step);
							break;
			case D: 		this.camera.strafe(step);
							break;
			case R: 		this.camera.raise(step);
							break;
			case F: 		this.camera.raise(- step);
							break;
			case A: 		this.camera.rolled(- step);
							break;
			case E: 		this.camera.rolled(step);
							break;
			case W: 		this.camera.yawed(- step);
							break;
			case C: 		this.camera.yawed(step);
							break;
			case T: 		this.camera.pitched(step);
							break;
			case G: 		this.camera.pitched(- step);
							break;
			case X: 		this.camera.lookAt(Points.of(0, 0, 0), Vectors.of(0, 0, 1));
							break;
			case SPACE: 	this.camera.move(100.0f * step);
							break;
			default : 		break;
			}
		});
	}
	protected Optional<EventHandler<MouseEvent>> mouseEventHandler() {
		return Optional.of((MouseEvent t) -> {
			if(camera == null)
				return ;
	
			if(t.getEventType() == MouseEvent.MOUSE_PRESSED) {
//				System.out.println("Mouse pressed");
				switch(t.getButton()) {
				case PRIMARY : 		handleLeftMousePress(t); break;
				case MIDDLE : 		handleMiddleMousePress(t); break;
				case SECONDARY : 	handleRightMousePress(t); break;
				default : 			throw new AssertionError();
				}
				handleMousePress(t);
			} else if(t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				Point2D d = getMouseDelta(t);
	
				switch(t.getButton()) {
				case PRIMARY :		handleLeftMouseDrag(t, d, speed); break;
				case MIDDLE : 		handleMiddleMouseDrag(t, d, speed); break;
				case SECONDARY :	handleRightMouseDrag(t, d, speed); break;
				default :			throw new AssertionError();
				}
			} else if(t.getEventType() == MouseEvent.MOUSE_MOVED) {
				handleMouseMoved(t, getMouseDelta(t), speed);
			} else if(t.getEventType() == MouseEvent.MOUSE_ENTERED) {
				if(viewport != null)
					viewport.requestFocus();
//				System.out.println("Mouse entering");;
			} else if(t.getEventType() == MouseEvent.MOUSE_CLICKED) {
				switch(t.getButton()) {
				case PRIMARY :  	handleLeftMouseReleased(t); break;
				case MIDDLE :		handleMiddleMouseReleased(t); break;
				case SECONDARY :	handleRightMouseReleased(t); break;
				default: 			throw new AssertionError();
				}
			}
	
//			camera.lookAt(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f);
		});
	}

	// Mouse Controller
	// ================
	private void handleMousePress(MouseEvent event) {
		resetMouse(event);
		event.consume();
	}

	private void handleLeftMousePress(MouseEvent e) {
//		System.out.println("Primary Button Pressed!");
	}
	private void handleLeftMouseReleased(MouseEvent t) {
//		System.out.println("Primary Button Clicked!");
	}
	private void handleLeftMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
		if(camera == null)
			return ;

		this.camera.yawed((float) - dragDelta.getX());
		this.camera.pitched((float) - dragDelta.getY());
	}

	private void handleMiddleMousePress(MouseEvent e) {
//		System.out.println("Middle Button Pressed!");
	}
	private void handleMiddleMouseReleased(MouseEvent t) {
//		System.out.println("Middle Button Clicked!");
	}
	private void handleMiddleMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
		// do nothing for now
	}

	private void handleRightMousePress(MouseEvent e) {
//		System.out.println("Secondary Button Pressed!");
	}
	private void handleRightMouseReleased(MouseEvent t) {
//		System.out.println("Secondary Button Clicked!");
	}
	private void handleRightMouseDrag(MouseEvent event, Point2D dragDelta, double modifier) {
		// do nothing for now
	}
	
	private void handleMouseMoved(MouseEvent event, Point2D moveDelta, double speed) {
		// do nothing for now
	}

}
