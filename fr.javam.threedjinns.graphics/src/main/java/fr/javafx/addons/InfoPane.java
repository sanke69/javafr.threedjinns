package fr.javafx.addons;

import java.text.DecimalFormat;

import fr.java.maths.algebra.vectors.DoubleVector3D;
import fr.threedijnns.api.interfaces.nodes.space.GxCamera3D;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class InfoPane extends AnchorPane {

	public enum Mode {
		Control, Hud;
	}
	private final Label	CameraInfo;
	private final Label	CameraCoordinate;
	private final Label	CursorInfo;
	private final Label	CursorCoordinate;
	
	private final GxCamera3D camera;

	public InfoPane(GxCamera3D _cam) {
		super();
		camera = _cam;

		setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;");
		setMaxWidth(320);
		setMaxHeight(240);

		CameraInfo = new Label("Camera: ");
		CameraCoordinate = new Label("P U B R\n(???, ???)\n(???, ???)\n(???, ???)\n");
		CursorInfo = new Label("Mouse: ");
		CursorCoordinate = new Label("(???, ???)");

		CameraInfo.setStyle("-fx-text-fill: goldenrod; -fx-font: italic 12 \"serif\"; -fx-padding: 0 0 0 0; -fx-text-alignment: center");
		CameraCoordinate.setStyle("-fx-text-fill: goldenrod; -fx-font: italic 12 \"serif\"; -fx-padding: 0 0 0 0; -fx-text-alignment: center");
		CursorInfo.setStyle("-fx-text-fill: goldenrod; -fx-font: italic 12 \"serif\"; -fx-padding: 0 0 0 0; -fx-text-alignment: center");
		CursorCoordinate.setStyle("-fx-text-fill: goldenrod; -fx-font: italic 12 \"serif\"; -fx-padding: 0 0 0 0; -fx-text-alignment: center");

		setMaxWidth(320);
		maxHeightProperty().bind(CameraCoordinate.prefHeightProperty().add(CursorCoordinate.prefHeightProperty()));

		VBox vInfos = new VBox();
		vInfos.setSpacing(10);
		vInfos.setPadding(new Insets(0, 20, 10, 20));
		vInfos.getChildren().addAll(CursorInfo, CursorCoordinate, CameraInfo, CameraCoordinate);
		getChildren().addAll(vInfos);

		configure();

		CameraCoordinate.setOnMouseMoved(this::handleMouseMovement);
/*
		_cam.positionProperty().addListener(this::handleCameraMovement);
		_cam.upAxisProperty().addListener(this::handleCameraMovement);
		_cam.backAxisProperty().addListener(this::handleCameraMovement);
		_cam.rightAxisProperty().addListener(this::handleCameraMovement);
*/
	}

	protected void configure() {
	}

	public void handleMouseMovement(MouseEvent _mouse) {
		DecimalFormat fmt = new DecimalFormat("0000.##");

		CursorCoordinate.setText("(" + fmt.format(_mouse.getX()) + ", " + fmt.format(_mouse.getY()) + ")");
	}

	public void handleCameraMovement(ObservableValue<? extends DoubleVector3D> _obs, DoubleVector3D _old, DoubleVector3D _new) {
		DecimalFormat fmt = new DecimalFormat("######.###");
		StringBuilder sb  = new StringBuilder();
/*
		Vector3d p     = camera.positionProperty().get();
		Vector3d up    = camera.upAxisProperty().get();
		Vector3d back  = camera.backAxisProperty().get();
		Vector3d right = camera.rightAxisProperty().get();

		sb.append(Strings.padRight("P", 10) 
				+ "\t" + Strings.padRight("R", 10) 
				+ "\t" + Strings.padRight("B", 10) 
				+ "\t" + Strings.padRight("U", 10) + "\n");
		
		sb.append(Strings.padRight(fmt.format(p.getX()), 10) 
				+ "\t" + Strings.padRight(fmt.format(right.getX()), 10) 
				+ "\t" + Strings.padRight(fmt.format(back.getX()), 10) 
				+ "\t" + Strings.padRight(fmt.format(up.getX()), 10) + "\n");

		sb.append(Strings.padRight(fmt.format(p.getY()), 10) 
				+ "\t" + Strings.padRight(fmt.format(right.getY()), 10) 
				+ "\t" + Strings.padRight(fmt.format(back.getY()), 10) 
				+ "\t" + Strings.padRight(fmt.format(up.getY()), 10) + "\n");

		sb.append(Strings.padRight(fmt.format(p.getZ()), 10) 
				+ "\t" + Strings.padRight(fmt.format(right.getZ()), 10) 
				+ "\t" + Strings.padRight(fmt.format(back.getZ()), 10) 
				+ "\t" + Strings.padRight(fmt.format(up.getZ()), 10) + "\n");
*/
		CameraCoordinate.setText(sb.toString());
	}

}
