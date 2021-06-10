package fr.threedijnns.meshes.types;

import java.util.Arrays;

import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.algebra.Vectors;

public class Face {
	Vertex[]	verticies	= null;
	Vector3D	normal		= null;

	public Vector3D getNormal() {
		if(normal == null)
			return calcNormal();
		else
			return normal;
	}

	public Vector3D calcNormal() {
		Vector3D v1 = verticies[0].v;
		Vector3D v2 = verticies[1].v;
		Vector3D v3 = verticies[2].v;

		Vector3D a = v1.minus(v2);
		Vector3D b = v1.minus(v3);

		return (normal = Vectors.crossProduct(a, b).normalized());
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Face face = (Face) o;

		if(!Arrays.equals(verticies, face.verticies))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return verticies != null ? Arrays.hashCode(verticies) : 0;
	}
}
