package fr.threedijnns.objects.space.primitives;

import fr.java.math.geometry.space.Point3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.base.GxObject3DBase;

public class GxPoint3DArray extends GxObject3DBase {
	Point3D[] points;

	public GxPoint3DArray() {
		super();
		points = null;
	}
	public GxPoint3DArray(int _nbPoints) {
		super();
		points = new Point3D[_nbPoints];
	}

	public void update(Point3D[] _dotCloud) {System.out.println("UPDATING...................................................." + _dotCloud.length);
		if(points == null || points.length != _dotCloud.length);
			points = new Point3D[_dotCloud.length];

		for(int i = 0; i < _dotCloud.length; ++i)
			points[i] = _dotCloud[i];
	}
	public void update(Point3D[] _dotCloud, double _ratio) {System.out.println("UPDATING...................................................." + _dotCloud.length);
		// TODO:: REMOVE !!!
		if(points == null || points.length != _dotCloud.length);
			points = new Point3D[_dotCloud.length];

		for(int i = 0; i < _dotCloud.length; ++i)
			points[i] = _dotCloud[i].divides(_ratio);
	}

	public Point3D[] asArray() {
		return points;
	}
	
	@Override
	public void process() {
		;
	}

	@Override
	public void render() {
		if(points == null)
			return ;
		preRender();
		gx.begin(PrimitiveType.PointList);
		gx.setPointSize(12.5f);
		gx.setPointAttenuation(5, 10, 1, 1, 1);
		for(int i = 0; i < points.length; ++i)
			gx.vertex(points[i]);
		gx.end();
		
		postRender();
	}

}
