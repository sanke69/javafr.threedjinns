package fr.threedijnns.objects.space.wrapper;

import fr.java.math.geometry.space.Frame3D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.geometry.space.camera.models.PinholeCamera3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.PolygonMode;

public class GxPinholeCamera3D extends GxAbstractWrapper3D<PinholeCamera3D> {

	public GxPinholeCamera3D(PinholeCamera3D _camera) {
		super(_camera);
	}

	public Frame3D  getFrame() {
		return model().getFrame();
	}

	public void process() {}

	boolean renderPyramid = true;
	boolean renderFrustum = true;
	boolean renderImage   = true;
	boolean renderCmos    = true;

	@Override
	public void renderModel() {
		gx.disable(EngineOption.GX_LIGHTING);
		gx.setPolygonMode(PolygonMode.OnlySkeleton, FaceType.FrontAndBack);

		render_frame();
		
		if(renderImage)
			render_image		(model().getImageOrigin(),     model().getImageBoundaries(),  model().getImageU(), model().getImageV());
		if(renderCmos)
			render_cmos		 	(model().getCmosOrigin(),      model().getCmosBoundaries(),   model().getCmosU(),  model().getCmosV());

		if(renderPyramid)
			render_near_pyramid	(model().getOrigin(),          model().getImageBoundaries());
		if(renderFrustum)
			render_frustum		(model().getImageBoundaries(), model().getFarBoundaries());

		gx.setColor(1f, 1f, 1f, 1f);
		gx.setLineWidth(1.0f);

		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);
		gx.enable(EngineOption.GX_LIGHTING);
	}

	private void render_frame() {
		Point3D  O = model().getFrame().getOrigin();
		Vector3D I = model().getFrame().getXAxis();
		Vector3D J = model().getFrame().getYAxis();
		Vector3D K = model().getFrame().getZAxis();

		gx.setLineWidth(3.0f);

		// Rendu de l'orientation
		gx.setColor(1.0f, 1.0f, 1.0f, 0.2f);
		// X AXIS: RIGHT					==> ROUGE
		gx.setColor(0.9f, 0.2f, 0.1f);
		gx.line(O, O.plus(I));
		// Y AXIS: UP						==> VERT
		gx.setColor(0.1f, 0.9f, 0.2f);
		gx.line(O, O.plus(J));
		// Z AXIS: BACK						==> BLEU
		gx.setColor(0.2f, 0.1f, 0.9f);
		gx.line(O, O.plus(K));
	}

	private void render_image(Point3D _imgC, Point3D[] _imgBBox, Vector3D _u, Vector3D _v) {
		gx.setLineWidth(1.0f);
		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.quad(_imgBBox[0], _imgBBox[1], _imgBBox[2], _imgBBox[3]);

		// U AXIS: RIGHT					==> ROUGE
		gx.setColor(0.9f, 0.2f, 0.1f);
		gx.line(_imgBBox[0], _imgBBox[0].plus(_u.times(10)));
		// V AXIS: UP						==> VERT
		gx.setColor(0.1f, 0.9f, 0.2f);
		gx.line(_imgBBox[0], _imgBBox[0].plus(_v.times(10)));
	}
	private void render_cmos(Point3D _cmosC, Point3D[] _cmosBBox, Vector3D _u, Vector3D _v) {
		gx.setLineWidth(1.0f);
		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.quad(_cmosBBox[0], _cmosBBox[1], _cmosBBox[2], _cmosBBox[3]);

		gx.setLineWidth(0.5f);
		// U AXIS: RIGHT					==> ROUGE
		gx.setColor(0.9f, 0.2f, 0.1f);
		gx.line(_cmosC, _cmosC.plus(_u.times(50)));
		// V AXIS: UP						==> VERT
		gx.setColor(0.1f, 0.9f, 0.2f);
		gx.line(_cmosC, _cmosC.plus(_v.times(50)));
	}


	private void render_near_pyramid(Point3D _O, Point3D[] _imgBBox) {
		// Rendu CentreOptique -> Image
		gx.setLineWidth(1.0f);
		gx.setColor(0.0f, 1.0f, 0.0f);
		gx.line(_O, _imgBBox[0]);
		gx.line(_O, _imgBBox[1]);
		gx.line(_O, _imgBBox[2]);
		gx.line(_O, _imgBBox[3]);
	}
	private void render_frustum(Point3D[] imgBBox, Point3D[] farBBox) {
		// Rendu du frustum
		gx.setLineWidth(1.0f);
		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.quad(imgBBox[0], imgBBox[1], imgBBox[2], imgBBox[3]);
		gx.quad(farBBox[0],  farBBox[1],  farBBox[2],  farBBox[3]);
		gx.quad(imgBBox[0], farBBox[0],  farBBox[3],  imgBBox[3]);
		gx.quad(imgBBox[0], farBBox[0],  farBBox[1],  imgBBox[1]);
		gx.quad(imgBBox[1], farBBox[1],  farBBox[2],  imgBBox[2]);
		gx.quad(imgBBox[3], farBBox[3],  farBBox[2],  imgBBox[2]);
	}

}
