package fr.threedijnns.objects.space.shapes.camera;

import java.awt.image.BufferedImage;

import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.Points;
import fr.java.maths.geometry.space.camera.models.PinholeCamera3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.attributes.Texture2D;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.lang.buffer.texture.streamers.BufferedImageStreamer;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.objects.base.GxObject3DBase;

public class GxPinholeCameraView extends GxObject3DBase implements GxRenderable {

	protected Point3D[]   vertex;
	protected Point2D[]   text;

	protected PinholeCamera3D 	camera;
	protected BufferedImage		videoFrame;
	protected StreamHandler2D 	streamer;

	public GxPinholeCameraView(PinholeCamera3D _camera, final BufferedImage _videoFrame) {
		super();
		camera = _camera;
		Point3D[] screenBounds = camera.getImageBoundaries();

		vertex    = new Point3D[4];
		vertex[0] = screenBounds[0];
		vertex[1] = screenBounds[1];
		vertex[2] = screenBounds[2];
		vertex[3] = screenBounds[3];

		text      = new Point2D[4];
		text[0]   = Points.of(0.0f, 0.0f);
		text[1]   = Points.of(1.0f, 0.0f);
		text[2]   = Points.of(1.0f, 1.0f);
		text[3]   = Points.of(0.0f, 1.0f);

		if(_videoFrame != null) {
			enableTexture(true);
			new BufferedImageStreamer((Texture2D) getTexture(), _videoFrame).start();
		} else {
			setColor(0x00FF00FF);
		}
	}


	@Override
	public void process() {
		Point3D[] screenBounds = camera.getImageBoundaries();
//		Point3D[] screenBounds = camera.getCmosBoundaries();
		vertex[0] = screenBounds[0];
		vertex[1] = screenBounds[1];
		vertex[2] = screenBounds[2];
		vertex[3] = screenBounds[3];

double ONE = 1;
		text[0]   = Points.of(0.0f, 0.0f);
		text[1]   = Points.of(0.0f, ONE);
		text[2]   = Points.of(ONE, ONE);
		text[3]   = Points.of(ONE, 0.0f);		// TODO:: Still an issue....
	}

	@Override
	public void render() {
		gx.disable(EngineOption.GX_LIGHTING);

		gx.pushMatrix(MatrixType.MAT_MODELVIEW);
		gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, frame.getModelMatrix());

		if(isTextureEnabled() && hasTexture()) {
			getTexture().enable(0);

			gx.begin(PrimitiveType.QuadList);
				gx.vertex		(vertex[0]);
				gx.texCoords	(text[0]);

				gx.vertex		(vertex[1]);
				gx.texCoords	(text[1]);

				gx.vertex		(vertex[2]);
				gx.texCoords	(text[2]);

				gx.vertex		(vertex[3]);
				gx.texCoords	(text[3]);
			gx.end();

			getTexture().disable(0);
		} else {
			gx.begin(PrimitiveType.QuadList);
			gx.vertex		(vertex[0]);
			gx.vertex		(vertex[1]);
			gx.vertex		(vertex[2]);
			gx.vertex		(vertex[3]);
			gx.end();
		}

		gx.popMatrix(MatrixType.MAT_MODELVIEW);

		gx.enable(EngineOption.GX_LIGHTING);
	}

}
