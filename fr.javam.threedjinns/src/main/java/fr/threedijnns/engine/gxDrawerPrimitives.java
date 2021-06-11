package fr.threedijnns.engine;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public interface gxDrawerPrimitives {

	public void begin			(PrimitiveType _primitive);

		public void vertex			(Point2D _pt);
		public void vertex			(Point3D _pt);

		public void vertex			(float _x, float _y);
		public void vertex			(float _x, float _y, float _z);
		public void vertex			(float _x, float _y, float _z, float _w);
		public void vertex			(FloatBuffer _fb);

		public void vertex			(double _x, double _y);
		public void vertex			(double _x, double _y, double _z);
		public void vertex			(double _x, double _y, double _z, double _w);
		public void vertex			(DoubleBuffer _db);
		

		public void normal			(Vector3D _pt);

		public void normal			(float _x, float _y, float _z);
		public void normal			(FloatBuffer _fb);

		public void normal			(double _x, double _y, double _z);
		public void normal			(DoubleBuffer _db);


		public void texCoords		(Point2D _pt);
		public void texCoords		(Point3D _pt);


		public void texCoords		(float _x, float _y);
		public void texCoords		(double _x, double _y);
		public void texCoords		(FloatBuffer _fb);
		
		public void texCoords		(float _x, float _y, float _z);
		public void texCoords		(double _x, double _y, double _z);
		public void texCoords		(DoubleBuffer _db);

	public void end				();

	public void point			(Point3D _pt);
	public void points			(Point3D... _pts);

	public void line			(Point3D _beg, Point3D _end);
	public void curve			(Point3D... _pts);

	public void triangle		(Point3D _a, Point3D _b, Point3D _c);
	public void triangleList	(Point3D[]... _pts);
	public void triangleStrip	(Point3D... _pts);
	public void triangleFan		(Point3D... _pts);

	public void quad			(Point3D _a, Point3D _b, Point3D _c, Point3D _d);
	public void quadList		(Point3D[]... _pts);

	public void polygon			(Point3D... _pts);

}
