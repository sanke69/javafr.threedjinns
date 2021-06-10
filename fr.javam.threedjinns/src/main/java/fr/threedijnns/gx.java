package fr.threedijnns;

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.enums.PixelFormat;
import fr.java.math.algebra.matrix.specials.Matrix44D;
import fr.java.math.geometry.plane.Dimension2D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.algebra.matrices.Matrix44d;
import fr.java.maths.algebra.vectors.DoubleVector4D;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.java.sdk.log.LogInstance;
import fr.threedijnns.api.graphics.GxViewport;
import fr.threedijnns.api.lang.buffer.gxBuffer;
import fr.threedijnns.api.lang.buffer.texture.TextureBase;
import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.BlendChannel;
import fr.threedijnns.api.lang.enums.BlendEquation;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.Capability;
import fr.threedijnns.api.lang.enums.DrawBuffer;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.LightParameter;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.enums.LogicFunction;
import fr.threedijnns.api.lang.enums.LogicOperator;
import fr.threedijnns.api.lang.enums.MaterialParameter;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.api.lang.enums.RenderParameter;
import fr.threedijnns.api.lang.enums.StencilOperator;
import fr.threedijnns.api.lang.enums.THintMode;
import fr.threedijnns.api.lang.enums.THintTarget;
import fr.threedijnns.api.lang.enums.TLightID;
import fr.threedijnns.api.lang.enums.TextureArg;
import fr.threedijnns.api.lang.enums.TextureOp;
import fr.threedijnns.api.lang.stream.engine.IFrameStream;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.engine.gxBufferManager;
import fr.threedijnns.engine.gxDrawer;
import fr.threedijnns.engine.gxDrawerObjects;
import fr.threedijnns.engine.gxDrawerOption;
import fr.threedijnns.engine.gxDrawerPrimitives;
import fr.threedijnns.engine.gxEngine;
import fr.threedijnns.engine.gxInitializer;
import fr.threedijnns.engine.gxThread;

// cf. http://jogamp.org/jogl/www/
public abstract class gx {
	public static final LogInstance log = LogInstance.getLogger();

	private static gxInitializer		m_Initializer;

	private static gxEngine    			m_Renderer;
	private static gxDrawer			 	m_Drawer;
	private static gxDrawerOption	 	m_DrawerOption;
	private static gxDrawerPrimitives 	m_DrawerPrimitives;
	private static gxDrawerObjects 		m_DrawerObjects;
	private static gxThread			 	m_Thread;

	private static gxBufferManager		m_BufferManager;
	
	/**
	 *     IIIII  N   N  IIIII  TTTTT  IIIII   AAA   L      IIIII  ZZZZZ  EEEEE
	 *       I    NN  N    I      T      I    A   A  L        I       Z   E    
	 *       I    N N N    I      T      I    AAAAA  L        I      Z    EEE  
	 *       I    N  NN    I      T      I    A   A  L        I     Z     E    
	 *     IIIII  N   N  IIIII    T    IIIII  A   A  LLLLL  IIIII  ZZZZZ  EEEEE
	 */
	public static void 			initialize(gxInitializer _initializer) {
		m_Initializer = _initializer;
		_initializer.startEngine();
		
		m_Thread = _initializer.getThread();

		if(! m_Thread.isRunning() ) {
			System.err.println("Not on GL Thread, and no other GX implementation exist, so WRONG INIT !!!");
			System.exit(1);
		}

		m_Renderer 			= _initializer.getEngineInstance();
		m_Drawer 			= _initializer.getDrawerInstance();
		m_DrawerOption 		= _initializer.getDrawerOptionInstance();
		m_DrawerPrimitives 	= _initializer.getDrawerPrimitiveInstance();
		m_DrawerObjects 	= _initializer.getDrawerObjectInstance();
		
		m_BufferManager		= _initializer.getBufferManagerInstance();
		
		m_Thread.runLater(() -> {
			StringBuilder sb = new StringBuilder();

			sb.append("[3D-Ngine] started with JOGL-OpenGL\n");
			sb.append("===================================\n");

			sb.append("Render         : " + getRendererDesc() + "\n");
			sb.append("Hardware       : " + getHardwareDesc() + "\n");
			sb.append("Vendor         : " + getVendorDesc() + "\n");

			sb.append("Supported Functionnalties :" + "\n");
			sb.append("---------------------------" + "\n");
			m_Renderer.checkCapabilities();
			for (Capability cap : Capability.values())
				sb.append(cap.name() + (hasCapability(cap) ? " OK" : " KO") + "\n");

			System.out.println(sb.toString());
		});

	}

	public final static IFrameStream registerViewport(GxViewport _viewport) {
		return m_Initializer.registerViewport(_viewport);
	}

	
	/**
	 *     EEEEE  N   N   GGG   IIIII  N   N  EEEEE
	 *     E      NN  N  G        I    NN  N  E
	 *     EEE    N N N  G  GG    I    N N N  EEE
	 *     E      N  NN  G   G    I    N  NN  E
	 *     EEEEE  N   N   GGG   IIIII  N   N  EEEEE
	 */
	public final static String 		getName() {
		return m_Renderer.getName();
	}

	public final static String 		getVersion() {
		return m_Renderer.getVersion();
	}

	public final static String 		getRendererDesc() {
		return m_Renderer.getRendererDesc();
	}
	public final static String getHardwareDesc() {
		return m_Renderer.getHardwareDesc();
	}
	public final static String getVendorDesc() {
		return m_Renderer.getVendorDesc();
	}

	public final static boolean 	isAMD() {
		return m_Renderer.isAMD();
	}
	public final static boolean 	isNVIDIA() {
		return m_Renderer.isNVIDIA();
	}
	
	public final static boolean 	hasCapability(Capability _capability) {
		return m_Renderer.hasCapability(_capability);
	}
	public final static String  capToString(Capability _capability) {
		switch (_capability) {
		case CAP_HW_MIPMAPPING:
			return "Hardware generation of MipMapping level";
		case CAP_DXT_COMPRESSION:
			return "Compression of DXT texture";
		case CAP_TEX_NON_POWER_2:
			return "Non-power of 2 texture dimension";
		case CAP_TEX_CUBE_MAP:
			return "Gestion des texture CubeMap";
		case CAP_PBO:
			return "Gestion des Pixel Buffer";
		default:
			return "Unknown Functionnalty";
		}
	}

	public final static boolean 	isAvailable(EngineOption _option) {
		return m_Renderer.isAvailable(_option);
	}
	public final static boolean isEnable(EngineOption _option) {
		return m_Renderer.isEnable(_option);
	}
	public final static void enable(EngineOption _option) {
		m_Renderer.enable(_option);
	}
	public final static void disable(EngineOption _option) {
		m_Renderer.disable(_option);
	}

	public final static void 		makeCurrent() {
		
	}
	public static void attach() {
		m_Renderer.attach();
	}
	public static void detach() {
		m_Renderer.detach();
	}
	

	/**
	 *  TTTTT   RRRR     EEEEE    AAA   DDDD
     *    T     R   R    E       A   A  D   D
	 *    T     RRRR     EEE     AAAAA  D   D
	 *    T     R   R    E       A   A  D   D
	 *    T     R    R   EEEEE   A   A  DDDD
	**/
	public final static boolean 	isRunning() {
		return m_Thread != null && m_Thread.isRunning();
	}
	public final static boolean 	isGxThread() {
		return m_Thread.isRunning();
	}

	public final static void 		runLater(Runnable _runnable) {
		m_Thread.runLater(_runnable);
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR
	 *     D   D  R   R  A   A  W   W  E      R   R
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR 
	 *     D   D  R  R   A   A  W W W  E      R  R
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R
 	 */
	public final static void 		setDrawBuffer(DrawBuffer _drawbuffer) {
		m_Drawer.setDrawBuffer(_drawbuffer);
	}


	public final static void 		clearBuffer(DrawBuffer _drawbuffer) {
		m_Drawer.clearBuffer(_drawbuffer);
	}
	public final static void 		clearAllBuffers() {
		m_Drawer.clearAllBuffers();
	}


	public final static int  		clearColor() {
		return m_Drawer.clearColor();
	}
	public final static void 		clearColor		(int _color) {
		m_Drawer.clearColor(_color);
	}
	public final static void 		clearColor		(byte _red, byte _green, byte _blue, byte _alpha) {
		m_Drawer.clearColor(_red, _green, _blue, _alpha);
	}
	public final static void 		clearColor		(int _red, int _green, int _blue, int _alpha) {
		m_Drawer.clearColor(_red, _green, _blue, _alpha);
	}
	public final static void 		clearColor		(float _red, float _green, float _blue, float _alpha) {
		m_Drawer.clearColor(_red, _green, _blue, _alpha);
	}
	public final static void 		clearColor		(double _red, double _green, double _blue, double _alpha) {
		m_Drawer.clearColor(_red, _green, _blue, _alpha);
	}
	public final static void 		clearColor		(FloatBuffer _fb) {
		m_Drawer.clearColor(_fb);
	}
	public final static void 		clearColor		(DoubleBuffer _db) {
		m_Drawer.clearColor(_db);
	}

	public final static void 		beginRender() {
		m_Drawer.beginRender();
	}

	public final static void 		endRender() {
		m_Drawer.endRender();
	}

	
	public final static void 		swapBuffers() {
		m_Drawer.swapBuffer();
	}


	public final static void 		pushMatrix(MatrixType _type) {
		m_Drawer.pushMatrix(_type);
	}
	public final static void 		pushMatrix(MatrixType _type, int _texId) {
		m_Drawer.pushMatrix(_type);
	}
	public final static Matrix44d 	getMatrix(MatrixType _type) {
		return m_Drawer.getMatrix(_type);
	}
	public final static Matrix44d 	getMatrix(MatrixType _type, int _texId) {
		return m_Drawer.getMatrix(_type, _texId);
	}
	public final static void 		loadMatrix(MatrixType _type, Matrix44d _matrix) {
		m_Drawer.loadMatrix(_type, _matrix);
	}
	public final static void 		loadMatrix(MatrixType _type, Matrix44d _matrix, int _texId) {
		m_Drawer.loadMatrix(_type, _matrix, _texId);
	}
	public final static void 		loadMatrixMult(MatrixType _type, Matrix44D _matrix) {
		m_Drawer.loadMatrixMult(_type, _matrix);
	}
	public final static void 		loadMatrixMult(MatrixType _type, Matrix44d _matrix, int _texId) {
		m_Drawer.loadMatrixMult(_type, _matrix, _texId);
	}
	public final static void 		popMatrix(MatrixType _type) {
		m_Drawer.popMatrix(_type);
	}
	public final static void 		popMatrix(MatrixType _type, int _texId) {
		m_Drawer.popMatrix(_type, _texId);
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR       OOO   PPPP   TTTTT  IIIII   OOO   N   N
	 *     D   D  R   R  A   A  W   W  E      R   R     O   O  P   P    T      I    O   O  NN  N
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR      O   O  PPPP     T      I    O   O  N N N
	 *     D   D  R  R   A   A  W W W  E      R  R      O   O  P        T      I    O   O  N  NN
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R      OOO   P        T    IIIII   OOO   N   N
 	 */
	public final static void setAlphaTest(LogicFunction _func, float _ref) {
		m_DrawerOption.alphaTest(_func, _ref);
	}
	@Deprecated
	public final static void setupAlphaBlending(BlendChannel _src, BlendChannel _dest) {
		m_DrawerOption.alphaBlending(_src, _dest);
	}

	public final static void setBlendTest(BlendChannel _src, BlendChannel _dst) {
		m_DrawerOption.blendTest(_src, _dst);
	}
	public final static void setBlendTestSeparately(BlendChannel _srcColor, BlendChannel _dstColor, BlendChannel _srcAlpha, BlendChannel _dstAlpha) {
		m_DrawerOption.blendTestSeparately(_srcColor, _dstColor, _srcAlpha, _dstAlpha);
	}
	public final static void setBlendEquation(BlendEquation _eq) {
		m_DrawerOption.blendEquation(_eq);
	}
	public final static void setBlendColor(byte _red, byte _green, byte _blue, byte _alpha) {
		m_DrawerOption.blendColor(_red, _green, _blue, _alpha);
	}

	public final static void setDepthTest(LogicFunction _func) {
		m_DrawerOption.depthTest(_func);
	}
	public final static void setDepthRange(float _near, float _far) {
		m_DrawerOption.depthRange(_near, _far);
	}
	public final static void setDepthMask(boolean _flag) {
		m_DrawerOption.depthMask(_flag);
	}
	public final static void setDepthClear(float _f) {
		m_DrawerOption.depthClear(_f);
	}

	public final static void setStencilTest(LogicFunction _func, int _ref, int _mask) {
		m_DrawerOption.stencilTest(_func, _ref, _mask);
	}
	public final static void setStencilOp(StencilOperator _sfail, StencilOperator _dpfail, StencilOperator _dppass) {
		m_DrawerOption.stencilOp(_sfail, _dpfail, _dppass);
	}
	public final static void setStencilMask(int _mask) {
		m_DrawerOption.stencilMask(_mask);
	}
	public final static void setStencilTestSeparately(FaceType _face, LogicFunction _func, int _ref, int _mask) {
		m_DrawerOption.stencilTestSeparately(_face, _func, _ref, _mask);
	}
	public final static void setStencilOpSeparately(FaceType _face, StencilOperator _sfail, StencilOperator _dpfail, StencilOperator _dppass) {
		m_DrawerOption.stencilOpSeparately(_face, _sfail, _dpfail, _dppass);
	}
	public final static void setStencilMaskSeparately(FaceType _face, int _mask) {
		m_DrawerOption.stencilMaskSeparately(_face, _mask);
	}

	public final static void setPointSize(float _width) {
		m_DrawerOption.pointSize(_width);
	}
	public final static void setPointAttenuation(float _min, float _max, float _a, float _b, float _c) {
		m_DrawerOption.pointAttenuation(_min, _max, _a, _b, _c);
	}

	public final static void setLineWidth(float _width) {
		m_DrawerOption.lineWidth(_width);
	}
	public final static void setLineStipple(int _n, short _pattern) {
		m_DrawerOption.lineStipple(_n, _pattern);
	}

	public final static void setPolygonMode(PolygonMode _pm, FaceType _ft) {
		m_DrawerOption.polygonMode(_pm, _ft);
	}
	public final static void setPolygonOffset(float _factor, float _units) {
		m_DrawerOption.polygonOffset(_factor, _units);
	}

	public final static void setLightGlobalAmbient(Integer _color, boolean _enable) {
		m_DrawerOption.lightGlobalAmbient(_color, _enable);
	}

	public final static void setLight(TLightID _id, boolean _value) {
		m_DrawerOption.light(_id, _value);
	}
	public final static void setLight(TLightID _id, LightParameter _value, int _integer) {
		m_DrawerOption.light(_id, _value, _integer);
	}
	public final static void setLight(TLightID _id, LightParameter _value, float _real) {
		m_DrawerOption.light(_id, _value, _real);
	}
	public final static void setLight(TLightID _id, LightParameter _value, Integer _color) {
		m_DrawerOption.light(_id, _value, _color);
	}
	public final static void setLight(TLightID _id, LightParameter _value, Point3D _position) {
		m_DrawerOption.light(_id, _value, _position);
	}
	public final static void setLight(TLightID _id, LightParameter _value, Vector3D _direction) {
		m_DrawerOption.light(_id, _value, _direction);
	}
	public final static void setLight(TLightID _id, LightParameter _value, DoubleVector4D _color) {
		m_DrawerOption.light(_id, _value, _color);
	}

	public final static void setColor(int _c) {
		m_DrawerOption.color(_c);
	}
	public final static void setColor(byte _r, byte _g, byte _b) {
		m_DrawerOption.color(_r, _g, _b);
	}
	public final static void setColor(byte _r, byte _g, byte _b, byte _a) {
		m_DrawerOption.color(_r, _g, _b, _a);
	}
	public final static void setColor(float _r, float _g, float _b) {
		m_DrawerOption.color(_r, _g, _b);
	}
	public final static void setColor(float _r, float _g, float _b, float _a) {
		m_DrawerOption.color(_r, _g, _b, _a);
	}
	public final static void setColor(double _r, double _g, double _b) {
		m_DrawerOption.color(_r, _g, _b);
	}
	public final static void setColor(double _r, double _g, double _b, double _a) {
		m_DrawerOption.color(_r, _g, _b, _a);
	}

	public final static void setColorMask(boolean _red, boolean _green, boolean _blue, boolean _alpha) {
		m_DrawerOption.colorMask(_red, _green, _blue, _alpha);
	}

	public final static void setMaterial(boolean _enabled) {
		m_DrawerOption.material(_enabled);
	}
	public final static void setMaterial(FaceType _ft, MaterialParameter _mv, int _i) {
		m_DrawerOption.material(_ft, _mv, _i);
	}
	public final static void setMaterial(FaceType _ft, MaterialParameter _mv, float _r) {
		m_DrawerOption.material(_ft, _mv, _r);
	}
	public final static void setMaterial(FaceType _ft, MaterialParameter _mv, Integer _c) {
		m_DrawerOption.material(_ft, _mv, _c);
	}/*
	public final static void setMaterial(FaceType _ft, MaterialParameter _mv, Vector4f _v) {
		m_DrawerOption.material(_ft, _mv, _v);
	}*/

	public final static void setupTextureUnit(int _id, TextureOp _op, TextureArg _arg1, TextureArg _arg2, Integer _key) {
		//m_Renderer.setupTextureUnit(_id, _op, _arg1, _arg2, _key);
	}

	public final static void activate(RenderParameter _param, boolean _value) {
		m_DrawerOption.activate(_param, _value);
	}
	
	public final static void setIndexMask(int _mask) {
		m_DrawerOption.indexMask(_mask);
	}

	public final static void setLogicOp(LogicOperator _operator) {
		m_DrawerOption.logicOp(_operator);
	}

	public final static void setHint(THintTarget _target, THintMode _mode) {
		m_DrawerOption.hint(_target, _mode);
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR      PPPP   RRRR   IIIII  M   M  IIIII  TTTTT  IIIII  V   V  EEEEE
	 *     D   D  R   R  A   A  W   W  E      R   R     P   P  R   R    I    MM MM    I      T      I    V   V  E    
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR      PPPP   RRRR     I    M M M    I      T      I    V   V  EEE  
	 *     D   D  R  R   A   A  W W W  E      R  R      P      R  R     I    M   M    I      T      I     V V   E    
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R     P      R   R  IIIII  M   M  IIIII    T    IIIII    V    EEEEE
 	 */
	public final static void begin(PrimitiveType _primitive) {
		m_DrawerPrimitives.begin(_primitive);
	}


	public final static void vertex(Point2D _pt) {
		m_DrawerPrimitives.vertex(_pt);
	}
	public final static void vertex(Point3D _pt) {
		m_DrawerPrimitives.vertex(_pt);
	}

	public final static void vertex(float _x, float _y) {
		m_DrawerPrimitives.vertex(_x, _y);
	}
	public final static void vertex(float _x, float _y, float _z) {
		m_DrawerPrimitives.vertex(_x, _y, _z);
	}
	public final static void vertex(float _x, float _y, float _z, float _w) {
		m_DrawerPrimitives.vertex(_x, _y, _z, _w);
	}
	public final static void vertex(FloatBuffer _fb) {
		m_DrawerPrimitives.vertex(_fb);
	}

	public final static void vertex(double _x, double _y) {
		m_DrawerPrimitives.vertex(_x, _y);
	}
	public final static void vertex(double _x, double _y, double _z) {
		m_DrawerPrimitives.vertex(_x, _y, _z);
	}
	public final static void vertex(double _x, double _y, double _z, double _w) {
		m_DrawerPrimitives.vertex(_x, _y, _z, _w);
	}
	public final static void vertex(DoubleBuffer _db) {
		m_DrawerPrimitives.vertex(_db);
	}

	public final static void normal(Point3D _pt) {
		m_DrawerPrimitives.normal(_pt.getX(), _pt.getY(), _pt.getZ());
	}
	public final static void normal(Vector3D _pt) {
		m_DrawerPrimitives.normal(_pt);
	}
	
	public final static void normal(float _x, float _y, float _z) {
		m_DrawerPrimitives.normal(_x, _y, _z);
	}
	public final static void normal(FloatBuffer _fb) {
		m_DrawerPrimitives.normal(_fb);
	}

	public final static void normal(double _x, double _y, double _z) {
		m_DrawerPrimitives.normal(_x, _y, _z);
	}
	public final static void normal(DoubleBuffer _fb) {
		m_DrawerPrimitives.normal(_fb);
	}
	

	public final static void texCoords(Point2D _pt) {
		m_DrawerPrimitives.texCoords(_pt);
	}
	
	public final static void texCoords(float _x, float _y) {
		m_DrawerPrimitives.texCoords(_x, _y);
	}
	public final static void texCoords(float _x, float _y, float _z) {
		m_DrawerPrimitives.texCoords(_x, _y, _z);
	}
	public final static void texCoords(FloatBuffer _db) {
		m_DrawerPrimitives.texCoords(_db);
	}

	public final static void texCoords(double _x, double _y) {
		m_DrawerPrimitives.texCoords(_x, _y);
	}
	public final static void texCoords(double _x, double _y, double _z) {
		m_DrawerPrimitives.texCoords(_x, _y, _z);
	}
	public final static void texCoords(DoubleBuffer _db) {
		m_DrawerPrimitives.texCoords(_db);
	}


	public final static void end() {
		m_DrawerPrimitives.end();
	}



	public final static void point(Point3D _pt) {
		m_DrawerPrimitives.point(_pt);
	}
	public final static void points(Point3D... _pts) {
		m_DrawerPrimitives.points(_pts);
	}

	public final static void line(Point3D _a, Point3D _b) {
		m_DrawerPrimitives.line(_a, _b);
	}

	public final static void curve(Point3D... _pts) {
		m_DrawerPrimitives.curve(_pts);
	}


	public final static void triangle(Point3D _a, Point3D _b, Point3D _c) {
		m_DrawerPrimitives.triangle(_a, _b, _c);
	}

	public final static void triangleList(Point3D[]... _pts) {
		m_DrawerPrimitives.triangleList(_pts);
	}

	public final static void triangleStrip(Point3D... _pts) {
		m_DrawerPrimitives.triangleStrip(_pts);
	}

	public final static void triangleFan(Point3D... _pts) {
		m_DrawerPrimitives.triangleFan(_pts);
	}


	public final static void quad(Point3D _a, Point3D _b, Point3D _c, Point3D _d) {
		m_DrawerPrimitives.quad(_a, _b, _c, _d);
	}
	public final static void quadList(Point3D[]... _pts) {
		m_DrawerPrimitives.quadList(_pts);
	}

	public final static void polygon(Point3D... _pts) {
		m_DrawerPrimitives.polygon(_pts);
	}



	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR       OOO   BBB
	 *     D   D  R   R  A   A  W   W  E      R   R     O   O  B  B
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR      O   O  BBBB
	 *     D   D  R  R   A   A  W W W  E      R  R      O   O  B   B
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R      OOO   BBBB
 	 */
	public static void	enable(BufferNature _type) {
		m_DrawerObjects.enable(_type);
	}

	public final static void setDeclaration(IDeclaration _declaration) {
		m_DrawerObjects.setDeclaration(_declaration);
	}

	public final static void setVertexBuffer(int _stream, gxBuffer _buffer) {
		m_DrawerObjects.setVB(_stream, _buffer.getBuffer(), 0, 0, _buffer.getCount() - 1);
	}
	public final static void setVertexBuffer(int _stream, gxBuffer _buffer, long _min, long _max) {
		m_DrawerObjects.setVB(_stream, _buffer.getBuffer(), 0, _min, _max != 0 ? _max : _buffer.getCount() - 1);
	}
	
	public final static void setIndexBuffer(gxBuffer _buffer) {
		m_DrawerObjects.setIB(_buffer.getBuffer(), _buffer.getType().nbBytes());
	}
	
	public final static void drawPrimitives(PrimitiveType _type, long _offset, long _count) {
		m_DrawerObjects.drawPrimitives(_type, _offset, _count);
	}
	public final static void drawIndexedPrimitives(PrimitiveType _type, long _offset, long _count) {
		m_DrawerObjects.drawIndexedPrimitives(_type, _offset, _count);
	}



	/**
	 *  BBB
	 *  B  B
	 *  BBBB
	 *  B   B
	 *  BBBB
	 */
	public final static IDeclaration 	createDeclaration(DeclarationElement[] _elements) {
		return m_BufferManager.createDecl(_elements);
	}

	public final static gxBuffer 		createVertexBuffer(Buffer _data, Primitives _type, long _size, BufferFlag... _flags) {
		gxBuffer buffer = new gxBuffer(m_BufferManager.createVBO(_type, _size, _flags));
		if (_data != null)
			buffer.fill(_data, _size);

		return buffer;
	}
	public final static void 			deleteVertexBuffer(gxBuffer _buffer) {
		m_BufferManager.deleteVBO(_buffer.getBuffer());
	}

	public final static gxBuffer 		createIndexBuffer(Buffer _data, long _size, BufferFlag... _flags) {
		gxBuffer buffer = new gxBuffer(m_BufferManager.createIBO(Primitives.INTEGER, _size, _flags));
		if (_data != null)
			buffer.fill(_data, _size);

		return buffer;
	}
	public final static void 			deleteIndexBuffer(gxBuffer _buffer) {
		m_BufferManager.deleteIBO(_buffer.getBuffer());
	}

	public final static TextureBase 	createTexture2D(Dimension2D _size, PixelFormat _format, LockFlag _flags) {
		return m_BufferManager.createTexture2D(_size, _format, _flags);
	}
	public final static void 			deleteTexture2D(TextureBase _base) {
		m_BufferManager.deleteTexture2D(_base);
	}
	public final static TextureBase 	createTextureCubeMap(Dimension2D _size, PixelFormat _format,
			LockFlag _flags) {
		return m_BufferManager.createTextureCubeMap(_size, _format, _flags);
	}

	public final static TextureStream 	createTextureStream2D(TextureBase _textureBase, StreamHandler2D _streamHandler, int _nbSlots) {
		return m_BufferManager.createTextureStream2D(_textureBase, _streamHandler, _nbSlots);
	}
	
	
	
	

	// Matrix manipulation methods

	
	
	
	// static void fog(unsigned int LightId, boolean Value) const;
	// static void fog(unsigned int LightId, TLightValue Value, const VECT4&
	// Param) const;

	public final static void setPixelBuffer(int _stream, gxBuffer _buffer, SimpleRectangle2D _in, SimpleRectangle2D _out) {
		m_BufferManager.setPBO(_stream, _buffer.getBuffer(), 0/* sizeof(T) */, _in, _out);
	}
/*
	public final static void setTransparency(gxBuffer _buffer, Integer _color) {
		m_BufferManager.setTransparency4Tex(_buffer.getBuffer(), _color);
	}
*/


/*
	public final static gxBuffer createPixelBuffer0(int _w, int _h, ByteBuffer _data, BufferFlag... _flags) {
		gxBuffer buffer = new gxBuffer(m_BufferManager.createPB(BufferPrimitive.BYTE, _w * _h, _flags));
		if (_data != null)
			buffer.fill(_data, _w * _h);

		return buffer;
	}
*/
	public final static gxBuffer createPixelBuffer(Buffer _data, Primitives _type, long _size, BufferFlag... _flags) {
		gxBuffer buffer = new gxBuffer(m_BufferManager.createPBO(_type, _size, _flags));
		if (_data != null)
			buffer.fill(_data, _size);

		return buffer;
	}

	public final static void setPixelBuffer(int _stream, gxBuffer _buffer) {
		m_BufferManager.setPBO(_stream, _buffer.getBuffer(), 0/* sizeof(T) */);
	}

	public final static void deletePixelBuffer(gxBuffer _buffer) {
		m_BufferManager.deletePBO(_buffer.getBuffer());
	}

	public final static Buffer readPixelBuffer(int _x, int _y) {
		return m_BufferManager.readPBO(_x, _y);
	}



}
