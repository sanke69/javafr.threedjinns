package fr.threedijnns.objects.space.wrapper;

import fr.java.math.geometry.space.Frame3D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.geometry.Space;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public class GxFrame3D extends GxAbstractWrapper3D<Frame3D> {

	boolean drawX,      		drawXneg,   		drawY,      		drawYneg,   		drawZ,      		drawZneg;
	double  XposLength, 		XnegLength, 		YposLength, 		YnegLength, 		ZposLength, 		ZnegLength;
	double  XposWidth = 2.0, 	XnegWidth = 1.0,  	YposWidth = 2.0,  	YnegWidth = 1.0,  	ZposWidth = 2.0,  	ZnegWidth = 1.0;

	boolean drawXY;
	double  XminXY, XmaxXY, YminXY, YmaxXY;
	double  majorTicksXY = 1.0, minorTicksXY = 0.5, thicknessMajorTicksXY = 1.0, thicknessMinorTicksXY = 0.5;

	boolean drawXZ;
	double  XminXZ, XmaxXZ, ZminXZ, ZmaxXZ;
	double  majorTicksXZ = 1.0, minorTicksXZ = 0.5, thicknessMajorTicksXZ = 1.0, thicknessMinorTicksXZ = 0.5;

	boolean drawYZ;
	double  YminYZ, YmaxYZ, ZminYZ, ZmaxYZ;
	double  majorTicksYZ = 1.0, minorTicksYZ = 0.5, thicknessMajorTicksYZ = 1.0, thicknessMinorTicksYZ = 0.5;

	public GxFrame3D() {
		super(Space.newFrame());
	}
	public GxFrame3D(Frame3D _locate) {
		super(_locate);
		drawX = drawXneg = drawY = drawYneg = drawZ = drawZneg = true;
		XposLength = YposLength = ZposLength = 1.0;
		XnegLength = YnegLength = ZnegLength = 1.0;
	}
	public GxFrame3D(Frame3D _locate, double _axis_length) {
		super(_locate);
		drawX = drawXneg = drawY = drawYneg = drawZ = drawZneg = true;
		XposLength = YposLength = ZposLength = _axis_length;
		XnegLength = YnegLength = ZnegLength = _axis_length;
	}
	public GxFrame3D(Frame3D _locate, double _axis_length, boolean _with_grid) {
		super(_locate);
		drawX = drawXneg = drawY = drawYneg = drawZ = drawZneg = true;
		XposLength = YposLength = ZposLength = _axis_length;
		XnegLength = YnegLength = ZnegLength = _axis_length;

		drawXY = _with_grid;
		XminXY = -10; XmaxXY = 10; YminXY = -10; YmaxXY = 10;
	}

	public GxFrame3D setAxisLength(double _length) {
		drawX      = drawY      = drawZ      = true;
		drawXneg   = drawYneg   = drawZneg   = false;
		XposLength = YposLength = ZposLength = _length;
		return this;
	}
	public GxFrame3D setAxisLength(double _posLength, double _negLength) {
		drawX      = drawY      = drawZ      = true;
		drawXneg   = drawYneg   = drawZneg   = true;
		XposLength = YposLength = ZposLength = _posLength;
		XnegLength = YnegLength = ZnegLength = _negLength;
		return this;
	}
	public GxFrame3D setAxisLength(double _posX, double _negX, double _posY, double _negY, double _posZ, double _negZ) {
		XposLength = _posX; YposLength = _posY; ZposLength = _posZ;
		XnegLength = _negX; YnegLength = _negY; ZnegLength = _negZ;
		return this;
	}
	
	public GxFrame3D drawAxis(boolean _draw) {
		drawX = drawY = drawZ = _draw;
		return this;
	}
	public GxFrame3D drawAxisNeg(boolean _drawNeg) {
		drawXneg = drawYneg = drawZneg = _drawNeg;
		return this;
	}
	public GxFrame3D drawAxis(boolean _drawX, boolean _drawY, boolean _drawZ) {
		drawX = _drawX; drawY = _drawY; drawZ = _drawZ;
		return this;
	}
	public GxFrame3D drawAxisNeg(boolean _drawXneg, boolean _drawYneg, boolean _drawZneg) {
		drawXneg = _drawXneg; drawYneg = _drawYneg; drawZneg = _drawZneg;
		return this;
	}



	@Override
	public void renderModel() {
		final Point3D  O = model().getOrigin();
		final Vector3D I = model().getXAxis();
		final Vector3D J = model().getYAxis();
		final Vector3D K = model().getZAxis();

		if(drawXY)
			draw_grid(O, I, J, XminXY, XmaxXY, YminXY, YmaxXY, majorTicksXY, minorTicksXY, thicknessMajorTicksXY, thicknessMinorTicksXY, 0xFFFFFFFF);

		if(drawX)
			draw_axis(O, I, XposLength, drawXneg ? XnegLength : -1, XposWidth, XnegWidth, 0xFF0000FF, 0xFFFFFFFF);
		if(drawY)
			draw_axis(O, J, YposLength, drawYneg ? YnegLength : -1, YposWidth, YnegWidth, 0x00FF00FF, 0xFFFFFFFF);
		if(drawZ)
			draw_axis(O, K, ZposLength, drawZneg ? ZnegLength : -1, ZposWidth, ZnegWidth, 0x0000FFFF, 0xFFFFFFFF);

		gx.setLineStipple	(3, (short) 0xFFFF);
		gx.setLineWidth		(1.0f);
		gx.setColor			(0xFFFFFFFF);
	}
	
	private void draw_axis(Point3D _o, Vector3D _dir, double _posLength, double _negLenth, double _posWidth, double _negWidth, int _posColor, int _negColor) {
		if(_negLenth > 0) {
			gx.setColor			(0xFEFEFEFE);
			gx.setLineWidth		((float) _negWidth);
			gx.setLineStipple	(3, (short) 0x0C0F);
			gx.line(_o, _o.minus(_dir.normalized().times(_negLenth)));
		}

		gx.setColor		(_posColor);
		gx.setLineWidth	((float) _posWidth);
		gx.setLineStipple	(3, (short) 0xFFFF);

		gx.line(_o, _o.plus(_dir.normalized().times(_posLength)));

		gx.setLineStipple	(3, (short) 0xFFFF);
		gx.setLineWidth		(1.0f);
		gx.setColor			(0xFFFFFFFF);
	}
	private void draw_grid(Point3D _o, Vector3D _i, Vector3D _j, double _XminXY, double _XmaxXY, double _YminXY, double _YmaxXY, double _majorTicksXY, double _minorTicksXY, double _thicknessMajorTicksXY, double _thicknessMinorTicksXY, int _color) {
		gx.setColor			(_color);
		gx.setLineWidth		((float) _majorTicksXY);
		gx.setLineStipple	(3, (short) 0x0C0F);

		gx.begin(PrimitiveType.LineList);
		for(double x = _XminXY; x <= _XmaxXY; x += _majorTicksXY) {
			gx.vertex(x, _YminXY, 0.0);
			gx.vertex(x, _YmaxXY, 0.0);
		}
		for(double y = _YminXY; y <= _YmaxXY; y += _majorTicksXY) {
			gx.vertex(_XminXY, y, 0.0);
			gx.vertex(_XmaxXY, y, 0.0);
		}
		gx.end();

		gx.setLineStipple	(3, (short) 0xFFFF);
		gx.setLineWidth		(1.0f);
		gx.setColor			(0xFFFFFFFF);
	}

}
