package fr.threedijnns.meshes.types;

import fr.java.math.algebra.vector.generic.Vector2D;
import fr.java.math.algebra.vector.generic.Vector3D;

public class Vertex {
	Vector3D v;
	Vector3D vn;
	Vector2D vt;

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Vertex vertex = (Vertex) o;

		if(v != null ? !v.equals(vertex.v) : vertex.v != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = v != null ? v.hashCode() : 0;
		result = 31 * result;
		return result;
	}

}
