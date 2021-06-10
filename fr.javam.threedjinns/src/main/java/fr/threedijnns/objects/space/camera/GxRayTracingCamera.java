package fr.threedijnns.objects.space.camera;

import fr.java.maths.geometry.space.camera.Projections3D;

public class GxRayTracingCamera extends GxDefaultCamera3D {

	public GxRayTracingCamera(Projections3D _projection) {
		super(_projection);
	}
/*
	public Vector3D tlscrPos() {
		return position() . minus( back().times(scrDist())) . minus(right().times(scrWidth() / 2.0f)) . plus(up().times(scrHeight() / 2.0f));
	}

	Vector3D uVector(int _hRes) {
		return right().times(scrWidth()  / _hRes);
	}
	Vector3D vVector(int _vRes) {
		return up().times(scrHeight() / _vRes);
	}

	public void process() {
		;
	}
	public void render() {
		Vector3D TL = position().minus(back().times(scrDist())) . minus(right().times(scrWidth() / 2.0f)) . plus(up().times(scrHeight() / 2.0f));
		Vector3D TR = position().minus(back().times(scrDist())) . plus(right().times(scrWidth() / 2.0f))  . plus(up().times(scrHeight() / 2.0f));
		Vector3D BL = position().minus(back().times(scrDist())) . minus(right().times(scrWidth() / 2.0f)) . minus(up().times(scrHeight() / 2.0f));
		Vector3D BR = position().minus(back().times(scrDist())) . plus(right().times(scrWidth() / 2.0f))  . minus(up().times(scrHeight() / 2.0f));

		Vector3D TLB = position().plusEquals( (TL.minusEquals(position()).timesEquals(m_Back)) );
		Vector3D TRB = position().plusEquals( (TR.minusEquals(position()).timesEquals(m_Back)) );
		Vector3D BLB = position().plusEquals( (BL.minusEquals(position()).timesEquals(m_Back)) );
		Vector3D BRB = position().plusEquals( (BR.minusEquals(position()).timesEquals(m_Back)) );

		gx.disable(EngineOption.GX_LIGHTING);
		gx.setPolygonMode(PolygonMode.OnlySkeleton, FaceType.FrontAndBack);

		// Rendu de l'orientation
		gx.setColor(1.0f, 1.0f, 1.0f, 0.2f);

		gx.setColor(0xFF0000FF);
		gx.line(position(), position().minus(back()));
		gx.setColor(0x00FF00FF);
		gx.line(position(), position().plus(right()));
		gx.setColor(0x0000FFFF);
		gx.line(position(), position().plus(up()));

		// Rendu de l'écran
		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.quad(TL, TR, BR, BL);
		/*
		gl.glBegin(gl.GL_LINES);
			// X AXIS							==> VERT
		gl.glColor3d(    0.1f,     0.9f,     0.2f);
		gl.glVertex3d(position().x, position().y, position().z);
		gl.glVertex3d(position().x-back().x,  position().y-back().y,  position().z-back().z);
			// Y AXIS							==> BLEU
		gl.glColor3d(    0.2f,     0.1f,     0.9f);
		gl.glVertex3d(position().x, position().y, position().z);
		gl.glVertex3d(position().x+right().x,  position().y+right().y,  position().z+right().z);
			// Z AXIS							==> ROUGE
		gl.glColor3d(    0.9f,     0.2f,     0.1f);
		gl.glVertex3d(position().x, position().y, position().z);
		gl.glVertex3d(position().x+up().x,  position().y+up().y,  position().z+up().z);
		gl.glEnd();
*/
		// Rendu de l'�cran
		/*
		gl.glColor3d(1.0f, 1.0f, 1.0f);
		gl.glBegin(gl.GL_QUADS);
 			gl.glVertex3d(TL.x, TL.y, TL.z);
			gl.glVertex3d(TR.x, TR.y, TR.z);
			gl.glVertex3d(BR.x, BR.y, BR.z);
			gl.glVertex3d(BL.x, BL.y, BL.z);
		gl.glEnd();
		*/

		// Rendu des rayons
		/*
		int texw = 100, texh = 100;
		for(int j = 0; j < texh; ++j)
			for(int i = 0; i < texw; ++i) {
				float t = 100.0f;
				ray    ray = new ray();

				ray.v = tlscrPos().plus( uVector(texw).times((i+0.5f))).minus(vVector(texh).times((j+0.5f)));
				Vector3f n = ray.v.minus(position());
				ray.n = n.normalized();

				gl.glBegin(gl.GL_LINES);
				gl.glColor3d(i * 1.0f/texw, j * 1.0f/texw, (i*j) * 1.0f/(texw*texh));
				gl.glVertex3d(ray.v.x, ray.v.y, ray.v.z);
				gl.glVertex3d(ray.v.x + t * ray.n.x, ray.v.y + t * ray.n.y, ray.v.z + t * ray.n.z);
				gl.glEnd();
			}
			* /

		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);
		gx.enable(EngineOption.GX_LIGHTING);

	}
*/
}
