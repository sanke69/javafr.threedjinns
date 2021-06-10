package fr.threedijnns.objects.space.wrapper;

import fr.java.math.geometry.space.Point3D;
import fr.threedijnns.gx;

public class GxPoint3D extends GxAbstractWrapper3D<Point3D.Editable> {
	double width;

	public GxPoint3D(Point3D _pt) {
		super(_pt.cloneEditable());

		width = 1.0;
	}
	public GxPoint3D(Point3D _pt, double _size) {
		super(_pt.cloneEditable());

		width = _size;
	}
	public GxPoint3D(Point3D _pt, double _size, int _color) {
		super(_pt.cloneEditable());

		isColorEnabled = true;
		color = _color;
		width = _size;
	}

	@Override
	public void renderModel() {
		gx.setPointSize((float) width);
//		gx.setPointAttenuation(5, 10, 1, 1, 1);
		gx.point(model());
//		gx.setPointAttenuation(5, 10, 1, 1, 1);
		gx.setPointSize(1.0f);
	}

}
