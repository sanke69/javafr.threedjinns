package fr.threedijnns.objects.space.wrapper;

import fr.java.lang.exceptions.NotYetImplementedException;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.Angles;
import fr.java.maths.algebra.Vectors;
import fr.java.maths.geometry.space.camera.projections.Frustum3D;
import fr.java.maths.geometry.space.camera.projections.Ortho3D;
import fr.java.maths.geometry.space.camera.projections.Perspective3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.space.GxCamera3D;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.PolygonMode;

public class GxCamera3DShape extends GxAbstractWrapper3D<GxCamera3D> {

	public GxCamera3DShape(GxCamera3D _camera) {
		super(_camera);
	}

	@Override
	public void renderModel() {
		if(model().getProjection() instanceof Perspective3D)
			renderPerspective((Perspective3D) model().getProjection());

		else if(model().getProjection() instanceof Frustum3D)
			renderFrustum((Frustum3D) model().getProjection());

		else if(model().getProjection() instanceof Ortho3D)
			throw new NotYetImplementedException();

		else 
			throw new NotYetImplementedException();
	}

	private void renderPerspective(Perspective3D P) {
		double fovy = Angles.Degree2Radian(P.getFovY());
		double near = P.getNear();
		double far  = P.getFar();
		double Hn   = near * Math.tan(fovy / 2.0);
		double Wn   = P.getAspect() * Hn;
		double Hf   = far * Math.tan(fovy / 2.0);
		double Wf   = P.getAspect() * Hf;
			
		Point3D  O = getFrame().getOrigin();
		Vector3D I = getFrame().getXAxis();
		Vector3D J = getFrame().getYAxis();
		Vector3D K = getFrame().getZAxis();

		Point3D  Cnear = O.minus(K.times(near));
		Point3D  Cfar  = O.minus(K.times(far));
		
		Point3D  TLnear = Cnear . minus(I.times(Wn)) . plus (J.times(Hn));
		Point3D  TRnear = Cnear . plus (I.times(Wn)) . plus (J.times(Hn));
		Point3D  BLnear = Cnear . minus(I.times(Wn)) . minus(J.times(Hn));
		Point3D  BRnear = Cnear . plus (I.times(Wn)) . minus(J.times(Hn));

		Point3D  TLfar  = Cfar  . minus(I.times(Wf)) . plus (J.times(Hf));
		Point3D  TRfar  = Cfar  . plus (I.times(Wf)) . plus (J.times(Hf));
		Point3D  BLfar  = Cfar  . minus(I.times(Wf)) . minus(J.times(Hf));
		Point3D  BRfar  = Cfar  . plus (I.times(Wf)) . minus(J.times(Hf));

		gx.disable(EngineOption.GX_LIGHTING);
		gx.setPolygonMode(PolygonMode.OnlySkeleton, FaceType.FrontAndBack);

		gx.setPointSize(5.0f);
		gx.point(O);
		gx.point(Cnear);
		gx.point(Cfar);

		gx.setPointSize(1.0f);

		// Rendu de l'orientation
		gx.setColor(1.0f, 1.0f, 1.0f, 0.2f);
		// X AXIS							==> ROUGE
		gx.setColor(0.9f, 0.2f, 0.1f);
		gx.line(O, O.plus(I));
		// Y AXIS							==> VERT
		gx.setColor(0.1f, 0.9f, 0.2f);
		gx.line(O, O.plus(J));
		// Z AXIS							==> BLEU
		gx.setColor(0.2f, 0.1f, 0.9f);
		gx.line(O, O.plus(K));

		// Rendu du frustum
		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.quad(TLnear, TRnear, BRnear, BLnear);
		gx.quad(TLfar,  TRfar,  BRfar,  BLfar);
		gx.quad(TLnear, TLfar,  BLfar,  BLnear);
		gx.quad(TLnear, TLfar,  TRfar,  TRnear);
		gx.quad(TRnear, TRfar,  BRfar,  BRnear);
		gx.quad(BLnear, BLfar,  BRfar,  BRnear);
		
		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);
		gx.enable(EngineOption.GX_LIGHTING);
	}

	private void renderFrustum(Frustum3D P) {
		Point3D  O = getFrame().getOrigin();
		Vector3D I = getFrame().getXAxis();
		Vector3D J = getFrame().getYAxis();
		Vector3D K = getFrame().getZAxis();

		Point3D  Cnear = O.minus(K.times(P.getNear()));
		Point3D  Cfar  = O.minus(K.times(P.getFar()));

		double thalesMin   = Cnear.minus(O).norm();
		double thalesMax   = Cfar.minus(O).norm();
		double thalesRatio = thalesMax / thalesMin;

		Point3D  TLnear = Cnear . plus(I.times(P.getLeft()))  . plus(J.times(P.getTop()));
		Point3D  TRnear = Cnear . plus(I.times(P.getRight())) . plus(J.times(P.getTop()));
		Point3D  BLnear = Cnear . plus(I.times(P.getLeft()))  . plus(J.times(P.getBottom()));
		Point3D  BRnear = Cnear . plus(I.times(P.getRight())) . plus(J.times(P.getBottom()));

		Point3D  TLfar  = O     . plus(Vectors.delta(TLnear, O).times(thalesRatio));
		Point3D  TRfar  = O     . plus(Vectors.delta(TRnear, O).times(thalesRatio));
		Point3D  BLfar  = O     . plus(Vectors.delta(BLnear, O).times(thalesRatio));
		Point3D  BRfar  = O     . plus(Vectors.delta(BRnear, O).times(thalesRatio));

		gx.disable(EngineOption.GX_LIGHTING);
		gx.setPolygonMode(PolygonMode.OnlySkeleton, FaceType.FrontAndBack);

		gx.setPointSize(5.0f);
		gx.point(O);
		gx.point(Cnear);
		gx.point(Cfar);

		gx.setPointSize(1.0f);

		// Rendu de l'orientation
		gx.setColor(1.0f, 1.0f, 1.0f, 0.2f);
		// X AXIS							==> ROUGE
		gx.setColor(0.9f, 0.2f, 0.1f);
		gx.line(O, O.plus(I));
		// Y AXIS							==> VERT
		gx.setColor(0.1f, 0.9f, 0.2f);
		gx.line(O, O.plus(J));
		// Z AXIS							==> BLEU
		gx.setColor(0.2f, 0.1f, 0.9f);
		gx.line(O, O.plus(K));

		// Rendu du frustum
		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.quad(TLnear, TRnear, BRnear, BLnear);
		gx.quad(TLfar,  TRfar,  BRfar,  BLfar);
		gx.quad(TLnear, TLfar,  BLfar,  BLnear);
		gx.quad(TLnear, TLfar,  TRfar,  TRnear);
		gx.quad(TRnear, TRfar,  BRfar,  BRnear);
		gx.quad(BLnear, BLfar,  BRfar,  BRnear);
		
		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);
		gx.enable(EngineOption.GX_LIGHTING);
	}

}
