package fr.threedijnns.engine;

import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.algebra.vectors.DoubleVector4D;
import fr.threedijnns.api.lang.enums.BlendChannel;
import fr.threedijnns.api.lang.enums.BlendEquation;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.LightParameter;
import fr.threedijnns.api.lang.enums.LogicFunction;
import fr.threedijnns.api.lang.enums.LogicOperator;
import fr.threedijnns.api.lang.enums.MaterialParameter;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.api.lang.enums.RenderParameter;
import fr.threedijnns.api.lang.enums.StencilOperator;
import fr.threedijnns.api.lang.enums.THintMode;
import fr.threedijnns.api.lang.enums.THintTarget;
import fr.threedijnns.api.lang.enums.TLightID;
import fr.threedijnns.api.lang.enums.TextureArg;
import fr.threedijnns.api.lang.enums.TextureOp;

public interface gxDrawerOption {

	void 			activate(RenderParameter _param, boolean _value);

	public void 	alphaTest				(LogicFunction _func, float _ref);
	@Deprecated // use blendTest instead
	public void 	alphaBlending			(BlendChannel _src, BlendChannel _dest);

	public void 	blendTest				(BlendChannel _src, BlendChannel _dst);
	public void 	blendTestSeparately		(BlendChannel _srcColor, BlendChannel _dstColor, BlendChannel _srcAlpha, BlendChannel _dstAlpha);
	public void 	blendEquation			(BlendEquation _eq);
	public void 	blendColor				(byte _red, byte _green, byte _blue, byte _alpha);

	public void 	depthTest				(LogicFunction _func);
	public void 	depthRange				(float _near, float _far);
	public void 	depthMask				(boolean _flag);
	public void 	depthClear				(float _f);

	public void 	stencilTest				(LogicFunction _func, int _ref, int _mask);
	public void 	stencilOp				(StencilOperator _sfail, StencilOperator _dpfail, StencilOperator _dppass);
	public void 	stencilMask				(int _mask);
	public void 	stencilTestSeparately	(FaceType _face, LogicFunction _func, int _ref, int _mask);
	public void 	stencilOpSeparately		(FaceType _face, StencilOperator _sfail, StencilOperator _dpfail, StencilOperator _dppass);
	public void 	stencilMaskSeparately	(FaceType _face, int _mask);

	public void 	pointSize				(float _size);
	public void 	pointAttenuation		(float _min, float _max, float _a/* = 1.0f*/, float _b/*.0f*/, float _c/*.0001f*/);

	public void 	lineWidth				(float _width);
	public void 	lineStipple				(int _factor, short _pattern);

	void            polygonMode(PolygonMode _pm, FaceType _ft);
	void 			polygonOffset(float _factor, float _units);

	public void 	lightGlobalAmbient(Integer _ambient, boolean _enable);

	public void 	light(TLightID _id, boolean _value);
	public void 	light(TLightID _id, LightParameter _option, int _integer);
	public void 	light(TLightID _id, LightParameter _option, float _real);
	public void 	light(TLightID _id, LightParameter _option, Integer _color);
	public void 	light(TLightID _id, LightParameter _option, Point3D _position);
	public void 	light(TLightID _id, LightParameter _option, Vector3D _direction);
	public void 	light(TLightID _id, LightParameter _option, DoubleVector4D _value);

	public void 	color(int _color);
	public void 	color(Integer _color);
	public void 	color(byte _red, byte _g, byte _b);
	public void 	color(byte _red, byte _g, byte _b, byte _a);
	public void 	color(float _red, float _g, float _b);
	public void 	color(float _red, float _g, float _b, float _a);
	public void 	color(double _red, double _g, double _b);
	public void 	color(double _red, double _g, double _b, double _a);
	
	public void 	colorMask(boolean _red, boolean _green, boolean _blue, boolean _alpha);
	
	public void 	material(boolean _value);
	public void 	material(FaceType _ft, MaterialParameter _mv, int _integer);
	public void 	material(FaceType _ft, MaterialParameter _mv, Integer _color);
	public void 	material(FaceType _ft, MaterialParameter _mv, float _real);
	public void 	material(FaceType _ft, MaterialParameter _mv, DoubleVector4D _vector);

	void 			setupTextureUnit(int _unit, TextureOp _op, TextureArg _arg1, TextureArg _arg2 /*= TXA_DIFFUSE*/, Integer _constantx00);

	void 			indexMask(int _mask);
	void 			logicOp(LogicOperator _op);
	void 			hint(THintTarget _target, THintMode _mode);

}
