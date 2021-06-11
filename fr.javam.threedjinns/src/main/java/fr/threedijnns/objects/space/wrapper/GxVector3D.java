package fr.threedijnns.objects.space.wrapper;

import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.space.Point3D;
import fr.threedijnns.gx;

public class GxVector3D extends GxAbstractWrapper3D<Vector3D> {
	Point3D  position;
	double   width;

	public GxVector3D(Vector3D _vector) {
		super(_vector);

		position = null;
		width    = 1.0;
	}
	public GxVector3D(Vector3D _vector, Point3D _position) {
		super(_vector);

		position = _position;
		width    = 1.0;
	}
	public GxVector3D(Vector3D _vector, Point3D _position, double _size, int _color) {
		super(_vector);

		isColorEnabled = true;
		color    = _color;
		width    = _size;
	}

	public void 	setPosition(Point3D _position) {
		position = _position;
	}
	public Point3D 	getPosition() {
		return position;
	}

	@Override
	public void 	renderModel() {
		if(position == null)
			return ;

		final Point3D  O = position;
		final Vector3D d = model();

		gx.setLineWidth((float) width);
		gx.setLineStipple(3, (short) 0xFFFF);
		gx.line(O, O.plus(d));
		gx.setLineStipple(3, (short) 0xFFFF);
		gx.setLineWidth(1.0f);
	}

}
