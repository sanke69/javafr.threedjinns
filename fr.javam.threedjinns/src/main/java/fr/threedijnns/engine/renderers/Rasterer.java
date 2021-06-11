package fr.threedijnns.engine.renderers;

import fr.java.math.geometry.space.Point3D;
import fr.java.maths.geometry.space.camera.Projections3D;
import fr.java.maths.geometry.types.Points;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.GxScene;
import fr.threedijnns.api.lang.enums.BlendChannel;
import fr.threedijnns.api.lang.enums.DrawBuffer;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.objects.plane.Scene2D;
import fr.threedijnns.objects.space.Scene3D;

public /*abstract*/ class Rasterer implements GxRenderer {

	Scene2D scene2D;
	GxScene scene3D;

	public Rasterer() {
		super();
	}

	@Override
	public void setScene2D(Scene2D _scene) {
		scene2D = _scene;
	}
	@Override
	public void setScene3D(Scene3D _scene) {
		scene3D = _scene;
	}

	@Override
	public void process() {
		if(scene2D != null)
			scene2D.process();
		if(scene3D != null)
			scene3D.process();
	}

	@Override
	public void render() {
		gx.setDrawBuffer(DrawBuffer.Back);

		if(scene3D == null || scene3D.getCamera() == null) {
			if(scene2D == null) {
				gx.clearColor((float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random());
				gx.clearAllBuffers();

				gx.loadMatrix(MatrixType.MAT_PROJECTION, Projections3D.ortho(0, 1, 0, 1, -1, 1).asUniformMatrix());

				gx.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random());
				gx.polygon(new Point3D[] { Points.of(0.25f, 0.25f, 0.0f), Points.of(0.25f, 0.75f, 0.0f), Points.of(0.75f, 0.75f, 0.0f), Points.of(0.75f, 0.25f, 0.0f), });
			} else {
				gx.clearAllBuffers();
				scene2D.render();
			}

		} else {
			// CONFIGURATION DES OPTIONS DE RENDU OPENGL
			gx.clearColor(0x020902FF);
			gx.clearAllBuffers();

			gx.enable(EngineOption.DepthTest);
			gx.setDepthMask(true);

			gx.enable(EngineOption.AlphaTest);
			gx.setBlendTest(BlendChannel.AlphaSource, BlendChannel.One);

			gx.enable(EngineOption.ColorMaterial);

			scene3D.render();

			// Rendu scene 2D
			gx.disable(EngineOption.DepthTest);
			gx.disable(EngineOption.AlphaTest);
			gx.disable(EngineOption.ColorMaterial);

			if(scene2D != null)
				scene2D.render();
		}
		
		// FIN DU RENDU: ECRITURE DANS LE TAMPON
		gx.swapBuffers();
	}

	
}
