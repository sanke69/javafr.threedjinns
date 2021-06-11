package fr.threedijnns.gl.impl;

import static com.jogamp.opengl.GL.GL_DRAW_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_MAX_SAMPLES;
import static com.jogamp.opengl.GL.GL_READ_FRAMEBUFFER;
import static com.jogamp.opengl.GL.GL_RENDERBUFFER;
import static fr.threedijnns.gl.JOGL.gl;

import java.nio.Buffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.enums.PixelFormat;
import fr.java.lang.exceptions.NotYetImplementedException;
import fr.java.math.algebra.matrix.generic.Matrix44D;
import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.plane.Dimension2D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.maths.algebra.matrices.DoubleMatrix44;
import fr.java.maths.algebra.vectors.DoubleVector4D;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.java.utils.Buffers3D;
import fr.java.utils.primitives.Buffers;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.BufferBase;
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
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;
import fr.threedijnns.engine.gxBufferManager;
import fr.threedijnns.engine.gxDrawer;
import fr.threedijnns.engine.gxDrawerObjects;
import fr.threedijnns.engine.gxDrawerOption;
import fr.threedijnns.engine.gxDrawerPrimitives;
import fr.threedijnns.engine.gxEngine;
import fr.threedijnns.gl.impl.textures.GLTexture2D;
import fr.threedijnns.gl.impl.textures.GLTextureCubeMap;
import fr.threedijnns.gl.impl.textures.streams.GLTextureStreamPBO;

public class GLRenderer implements 	gxEngine, 
									gxDrawer, 
									gxDrawerOption,
									gxDrawerPrimitives, 
									gxDrawerObjects,
									gxBufferManager {

	protected int geetMaxTextureSize() {

		IntBuffer maxSizeBuf = Buffers3D.allocateIntBuffer(1);
		gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, maxSizeBuf);
		
		return maxSizeBuf.get(0);
	}
	private final GLContextCapabilities		m_Extensions;
	private final Map<Capability, Boolean>	m_Capabilities;

	private static int 						s_LightCount = 0;

	private GLDeclaration					m_CurrentDeclaration;
	private long							m_CurrentIndexStride = 4;

	public GLRenderer() {
		super();
		m_Extensions   = GLContextCapabilities.get();
		m_Capabilities = new HashMap<Capability, Boolean>();
	}

	/** ENGINE
	 *     EEEEE  N   N   GGG   IIIII  N   N  EEEEE
	 *     E      NN  N  G        I    NN  N  E
	 *     EEE    N N N  G  GG    I    N N N  EEE
	 *     E      N  NN  G   G    I    N  NN  E
	 *     EEEEE  N   N   GGG   IIIII  N   N  EEEEE
	 */
	@Override
	public String 	getName() {
		return "3D-Ngine - JOGL Implementation";
	}
	@Override
	public String 	getVersion() {
		return "0.0.0.1a";
	}

	@Override
	public String 	getRendererDesc() {
		if(gl == null)
			return "No OpenGL Service available";

		String GLVersion = gl.glGetString(GL.GL_VERSION);
		if(GLVersion.length() == 0)
			return "No OpenGL Service available";
		return "OpenGL " + GLVersion;
	}
	@Override
	public String 	getHardwareDesc() {
		if(gl == null)
			return "No OpenGL Service available";

		String GLHardware = gl.glGetString(GL.GL_VERSION);
		if(GLHardware.length() == 0)
			return "Unable to get Hardware Description";
		return GLHardware;
	}
	@Override
	public String 	getVendorDesc() {
		if(gl == null)
			return "No OpenGL Service available";

		String GLVendor = gl.glGetString(GL.GL_VENDOR);
		if(GLVendor == null)
			return "Unable to get Hardware Vendor";
		return GLVendor;
	}

    @Deprecated
    public boolean 	isAMD() {
    	GLContextCapabilities caps = GLContextCapabilities.get();
        return caps.GL_ATI_fragment_shader || caps.GL_ATI_texture_compression_3dc || caps.GL_AMD_debug_output;
    }
    @Deprecated
    public boolean 	isNVIDIA() {
    	GLContextCapabilities caps = GLContextCapabilities.get();
        return caps.GL_NV_vertex_program || caps.GL_NV_register_combiners || caps.GL_NV_gpu_program4;
    }

	@Override
	public void 	checkCapabilities() {
		for(Capability cap : Capability.values()) {
			switch (cap) {
			case CAP_TEX_NON_POWER_2:
				m_Capabilities.put(cap, m_Extensions.GL_ARB_texture_non_power_of_two);
				break;
			case CAP_HW_MIPMAPPING:
				m_Capabilities.put(cap, m_Extensions.GL_SGIS_generate_mipmap);
				break;
			case CAP_TEX_CUBE_MAP:
				m_Capabilities.put(cap, m_Extensions.GL_ARB_texture_cube_map);
				break;
			case CAP_PBO:
				m_Capabilities.put(cap, m_Extensions.GL_ARB_pixel_buffer_object);
				break;
			case CAP_DXT_COMPRESSION:
				m_Capabilities.put(cap, m_Extensions.GL_ARB_texture_compression && m_Extensions.GL_EXT_texture_compression_s3tc);
				break;
			case CAP_TEX_EDGE_CLAMP:
				m_Capabilities.put(cap, m_Extensions.GL_EXT_texture_edge_clamp);
				break;
			case CAP_ENUM_LENGTH:
				System.err.println("Currently no test, always false!");
				m_Capabilities.put(cap, false);
				break;
			default:
				System.err.println("UNKNOWN CAPABILITY:: " + cap.name());
				break;
			}
		}

	}
    private static void checkCapabilities(final GLContextCapabilities caps) {
        if (!caps.OpenGL15)
            throw new UnsupportedOperationException("Support for OpenGL 1.5 or higher is required.");

        if (!(caps.OpenGL20 || caps.GL_ARB_texture_non_power_of_two))
            throw new UnsupportedOperationException("Support for npot textures is required.");

        if (!(caps.OpenGL30 || caps.GL_ARB_framebuffer_object || caps.GL_EXT_framebuffer_object))
            throw new UnsupportedOperationException("Framebuffer object support is required.");
    }
	@Override
	public boolean 	hasCapability(Capability _capability) {
		return m_Capabilities.get(_capability);
	}

	@Override
	public boolean 	isAvailable(EngineOption _option) {
		return gl.glIsEnabled(GLEnums.get(_option)) ? true : false;
	}
	@Override
	public boolean 	isEnable(EngineOption _option) {
		return gl.glIsEnabled(GLEnums.get(_option)) ? true : false;
	}
	@Override
	public void 	enable(EngineOption _option) {
		gl.glEnable(GLEnums.get(_option));
	}
	@Override
	public void 	disable(EngineOption _option) {
		gl.glDisable(GLEnums.get(_option));
	}

	/**
	 *  TTTTT   RRRR     EEEEE    AAA   DDDD
     *    T     R   R    E       A   A  D   D
	 *    T     RRRR     EEE     AAAAA  D   D
	 *    T     R   R    E       A   A  D   D
	 *    T     R    R   EEEEE   A   A  DDDD
	**/
	@Override
	public void 	makeCurrent() {
		
	}
	@Override
	public void 	attach() {
//		JOGL.getContext().makeCurrent();
	}
	@Override
	public void 	detach() {
//		gx.runLater(() -> JOGL.getContext().makeCurrent());
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR
	 *     D   D  R   R  A   A  W   W  E      R   R
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR 
	 *     D   D  R  R   A   A  W W W  E      R  R
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R
 	 */

	// Buffer selection methods
	@Override
	public void setDrawBuffer(DrawBuffer _drawbuffer) {
		gl.glDrawBuffer(GLEnums.get(_drawbuffer));
	}

	// Clear Option Parameters
	@Override
	public void clearBuffer(DrawBuffer _drawbuffer) {
		throw new NotYetImplementedException();
	}
	@Override
	public void clearAllBuffers() {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
	}

	public int  clearColor() {
		return -1;
	}
	public void clearColor(int _color) {
		float r = (float) (((_color >> 24) & 0xFF) / 255.0f);
		float g = (float) (((_color >> 16) & 0xFF) / 255.0f);
		float b = (float) (((_color >>  8) & 0xFF) / 255.0f);
		float a = 1.0f; //(float) (((_color >>  0) & 0xFF) / 255.0f);
		gl.glClearColor(r, g, b, a);
	}
	@Override
	public void clearColor(byte _red, byte _green, byte _blue, byte _alpha) {
		gl.glClearColor(_red / 255.0f, _green / 255.0f, _blue / 255.0f, _alpha / 255.0f);
	}
	@Override
	public void clearColor(int _red, int _green, int _blue, int _alpha) {
		gl.glClearColorIi(_red, _green, _blue, _alpha);
	}
	@Override
	public void clearColor(float _red, float _green, float _blue, float _alpha) {
		gl.glClearColor(_red, _green, _blue, _alpha);
	}
	public void clearColor(FloatBuffer _fb) {

	}
	public void clearColor(double _red, double _green, double _blue, double _alpha) {
		gl.glClearColor((float) _red * 255.0f, (float) _green * 255.0f, (float) _blue * 255.0f, (float) _alpha * 255.0f);
	}
	public void clearColor(DoubleBuffer _db) {
		
	}

	// Rendering methods
	@Override
	public void beginRender() {
		clearAllBuffers();
	}
	@Override
	public void endRender() {
		;
	}

	@Override
	public void swapBuffer() {
		gl.glFlush();
	}

	// Matrix methods
	@Override
	public void 		pushMatrix(MatrixType _type) {
		if(_type != MatrixType.MAT_MODELVIEW && _type != MatrixType.MAT_PROJECTION)
			gl.glActiveTexture(GL.GL_TEXTURE0);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glPushMatrix();
	}
	@Override
	public void 		pushMatrix(MatrixType _type, int _texId) {
		if(_type == MatrixType.MAT_MODELVIEW || _type == MatrixType.MAT_PROJECTION)
			throw new IllegalAccessError("Use only with texture matrix");

		gl.glActiveTexture(GL.GL_TEXTURE0 + _texId);
		gl.glMatrixMode(GLEnums.get(_type));
		gl.glPushMatrix();
	}
	@Override
	public DoubleMatrix44 	getMatrix(MatrixType _type) {
		if(_type != MatrixType.MAT_MODELVIEW && _type != MatrixType.MAT_PROJECTION)
			gl.glActiveTexture(GL.GL_TEXTURE0);

		DoubleBuffer m = DoubleBuffer.allocate(16);
		gl.glGetDoublev(GLEnums.get(_type), m);

		DoubleMatrix44 r = new DoubleMatrix44();
		r.m00 = m.get(0);
		r.m10 = m.get(1);
		r.m20 = m.get(2);
		r.m30 = m.get(3);
		r.m01 = m.get(4);
		r.m11 = m.get(5);
		r.m21 = m.get(6);
		r.m31 = m.get(7);
		r.m02 = m.get(8);
		r.m12 = m.get(9);
		r.m22 = m.get(10);
		r.m32 = m.get(11);
		r.m03 = m.get(12);
		r.m13 = m.get(13);
		r.m23 = m.get(14);
		r.m33 = m.get(15);
		return r;
	}
	@Override
	public DoubleMatrix44 	getMatrix(MatrixType _type, int _texId) {
		if(_type == MatrixType.MAT_MODELVIEW || _type == MatrixType.MAT_PROJECTION)
			throw new IllegalAccessError("Use only with texture matrix");

		gl.glActiveTexture(GL.GL_TEXTURE0 + _texId);
		
		DoubleBuffer m = DoubleBuffer.allocate(16);
		gl.glGetDoublev(GLEnums.get(_type), m);

		DoubleMatrix44 r = new DoubleMatrix44();
		r.m00 = m.get(0);
		r.m10 = m.get(1);
		r.m20 = m.get(2);
		r.m30 = m.get(3);
		r.m01 = m.get(4);
		r.m11 = m.get(5);
		r.m21 = m.get(6);
		r.m31 = m.get(7);
		r.m02 = m.get(8);
		r.m12 = m.get(9);
		r.m22 = m.get(10);
		r.m32 = m.get(11);
		r.m03 = m.get(12);
		r.m13 = m.get(13);
		r.m23 = m.get(14);
		r.m33 = m.get(15);
		return r;
	}
	@Override
	public void 		loadMatrix(MatrixType _type, DoubleMatrix44 _m) {
		if(_type != MatrixType.MAT_MODELVIEW && _type != MatrixType.MAT_PROJECTION)
			gl.glActiveTexture(GL.GL_TEXTURE0);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glLoadIdentity();
		gl.glLoadMatrixd(_m.getColumnBuffer());
	}
	@Override
	public void 		loadMatrix(MatrixType _type, DoubleMatrix44 _m, int _texId) {
		if(_type == MatrixType.MAT_MODELVIEW || _type == MatrixType.MAT_PROJECTION)
			throw new IllegalAccessError("Use only with texture matrix");

		gl.glActiveTexture(GL.GL_TEXTURE0 + _texId);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glLoadIdentity();
		gl.glLoadMatrixd(_m.getColumnBuffer());
	}
	@Override
	public void 		loadMatrixMult(MatrixType _type, Matrix44D _m) {
		if(_type != MatrixType.MAT_MODELVIEW && _type != MatrixType.MAT_PROJECTION)
			gl.glActiveTexture(GL.GL_TEXTURE0);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glMultMatrixd(_m.getColumnBuffer());
	}
	@Override
	public void 		loadMatrixMult(MatrixType _type, DoubleMatrix44 _m, int _texId) {
		if(_type == MatrixType.MAT_MODELVIEW || _type == MatrixType.MAT_PROJECTION)
			throw new IllegalAccessError("Use only with texture matrix");

		gl.glActiveTexture(GL.GL_TEXTURE0 + _texId);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glMultMatrixd(_m.getColumnBuffer());
	}
	@Override
	public void 		popMatrix(MatrixType _type) {
		if(_type != MatrixType.MAT_MODELVIEW && _type != MatrixType.MAT_PROJECTION)
			gl.glActiveTexture(GL.GL_TEXTURE0);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glPopMatrix();
	}
	@Override
	public void 		popMatrix(MatrixType _type, int _texId) {
		if(_type == MatrixType.MAT_MODELVIEW || _type == MatrixType.MAT_PROJECTION)
			throw new IllegalAccessError("Use only with texture matrix");

		gl.glActiveTexture(GL.GL_TEXTURE0 + _texId);

		gl.glMatrixMode(GLEnums.get(_type));
		gl.glPopMatrix();
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR       OOO   PPPP   TTTTT  IIIII   OOO   N   N
	 *     D   D  R   R  A   A  W   W  E      R   R     O   O  P   P    T      I    O   O  NN  N
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR      O   O  PPPP     T      I    O   O  N N N
	 *     D   D  R  R   A   A  W W W  E      R  R      O   O  P        T      I    O   O  N  NN
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R      OOO   P        T    IIIII   OOO   N   N
 	 */
	@Override
	public void activate(RenderParameter _param, boolean _value) {
		switch (_param) {
		case RENDER_ZWRITE:
			if(_value) {
				gl.glEnable(GL.GL_DEPTH_TEST);
				gl.glDepthMask(true);
				gl.glDepthFunc(GL.GL_LEQUAL);
				gl.glDepthRange(0.0f, 1.0f);
				gl.glClearDepth(1.0f);
			} else {
				gl.glDisable(GL.GL_DEPTH_TEST);
			}
			break;

		case RENDER_ALPHABLEND:
			if(_value) {
				gl.glEnable(GL2.GL_ALPHA_TEST);
				gl.glAlphaFunc(GL.GL_GREATER, 0.5f);
				gl.glEnable(GL.GL_BLEND);
				//gl.glBlendFunc(GL.GL_GREATER, 0.5f);
			} else {
				gl.glDisable(GL2.GL_ALPHA_TEST);
				gl.glDisable(GL.GL_BLEND);
			}
			break;

		case RENDER_SMOTH_POINT:
			if(_value) {
				gl.glEnable(GL2.GL_POINT_SMOOTH);
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			} else {
				gl.glDisable(GL2.GL_POINT_SMOOTH);
			}
			break;

		case RENDER_SPRITE_POINT:
			if(_value) {
				gl.glTexEnvf(GL2.GL_POINT_SPRITE, GL2.GL_COORD_REPLACE, GL.GL_TRUE);
				//gl.glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, GL_TRUE);
				//gl.glEnable (GL_POINT_SPRITE);

				//gl.glTexEnvf (GL_POINT_SPRITE_, GL_COORD_REPLACE_, GL_TRUE);
				//gl.glEnable (GL_POINT_SPRITE_);
				gl.glEnable(GLEnums.get(_param));
			} else {
				gl.glDisable(GLEnums.get(_param));
			}
			break;
		}
		;
	}

	@Override
	public void alphaTest(LogicFunction _func, float _ref) {
		gl.glAlphaFunc(GLEnums.get(_func), _ref);
	}
	@Override @Deprecated
	public void alphaBlending(BlendChannel _src, BlendChannel _dest) {
		gl.glBlendFunc(GLEnums.get(_src), GLEnums.get(_dest));
	}

	@Override
	public void blendTest(BlendChannel _src, BlendChannel _dst) {
		gl.glBlendFunc(GLEnums.get(_src), GLEnums.get(_dst));
	}
	@Override
	public void blendTestSeparately(BlendChannel _srcColor, BlendChannel _dstColor, BlendChannel _srcAlpha, BlendChannel _dstAlpha) {
		gl.glBlendFuncSeparate(GLEnums.get(_srcColor), GLEnums.get(_dstColor), GLEnums.get(_srcAlpha), GLEnums.get(_dstAlpha));
	}
	@Override
	public void blendEquation(BlendEquation _eq) {
		gl.glBlendEquation(GLEnums.get(_eq));
	}
	@Override
	public void blendColor(byte _red, byte _green, byte _blue, byte _alpha) {
		gl.glBlendColor(_red, _green, _blue, _alpha);
	}

	@Override
	public void depthTest(LogicFunction _func) {
		gl.glDepthFunc(GLEnums.get(_func));
	}
	@Override
	public void depthRange(float _near, float _far) {
		gl.glDepthRange(_near, _far);
	}
	@Override
	public void depthMask(boolean _flag) {
		gl.glDepthMask(_flag);
	}
	@Override
	public void depthClear(float _f) {
		gl.glClearDepth(_f);
	}
	
	@Override
	public void stencilTest(LogicFunction _func, int _ref, int _mask) {
		gl.glStencilFunc(GLEnums.get(_func), _ref, _mask);
	}
	@Override
	public void stencilOp(StencilOperator _sfail, StencilOperator _dpfail, StencilOperator _dppass) {
		gl.glStencilOp(GLEnums.get(_sfail), GLEnums.get(_dpfail), GLEnums.get(_dppass));
	}
	@Override
	public void stencilMask(int _mask) {
		gl.glStencilMask(_mask);
	}
	@Override
	public void stencilTestSeparately(FaceType _face, LogicFunction _func, int _ref, int _mask) {
		gl.glStencilFuncSeparate(GLEnums.get(_face), GLEnums.get(_func), _ref, _mask);
	}
	@Override
	public void stencilOpSeparately(FaceType _face, StencilOperator _sfail, StencilOperator _dpfail, StencilOperator _dppass) {
		gl.glStencilOpSeparate(GLEnums.get(_face), GLEnums.get(_sfail), GLEnums.get(_dpfail), GLEnums.get(_dppass));
	}
	@Override
	public void stencilMaskSeparately(FaceType _face, int _mask) {
		gl.glStencilMaskSeparate(GLEnums.get(_face), _mask);
	}

	@Override
	public void pointSize(float _d) {
		gl.glEnable(GL3.GL_PROGRAM_POINT_SIZE);
		gl.glPointSize(_d);
	}
	@Override
	public void pointAttenuation(float _min, float _max, float _a, float _b, float _c) {
		//Les coefficients d'atténuation à appliquer
		FloatBuffer coeffs = FloatBuffer.wrap(new float[] { _a, _b, _c });

		gl.glPointParameterfv(GL2.GL_POINT_DISTANCE_ATTENUATION, coeffs);

		//Taille du point
		gl.glPointParameterf(GL2.GL_POINT_SIZE_MAX, _max);
		gl.glPointParameterf(GL2.GL_POINT_SIZE_MIN, _min);
	}

	@Override
	public void lineWidth(float _width) {
		gl.glLineWidth(_width);
	}
	@Override
	public void lineStipple(int _factor, short _pattern) {
		gl.glLineStipple(_factor, _pattern);
	}

	@Override
	public void polygonMode(PolygonMode _polygonMode, FaceType _faceType) {
		int polygonMode = 0;
		int faceType    = 0;

		switch(_polygonMode) {
		case OnlyVertex:	polygonMode = GL2.GL_POINT; 			break;
		case OnlySkeleton:	polygonMode = GL2.GL_LINE;  			break;
		case Realistic:		polygonMode = GL2.GL_FILL;  			break;
		case OnlyBoundBox:	polygonMode = GL2.GL_LINE;  			break;
		case OnlyLand:		polygonMode = GL2.GL_LINE;  			break;
		default: 			polygonMode = GL2.GL_FILL;  			break;
		};
		switch(_faceType) {
		case Back:			faceType    = GL2.GL_FRONT; 			break;
		case Front:			faceType    = GL2.GL_BACK; 				break;
		case FrontAndBack:	faceType    = GL2.GL_FRONT_AND_BACK; 	break;
		default:			faceType    = GL2.GL_FRONT_AND_BACK; 	break;
		}

		gl.glPolygonMode(faceType, polygonMode);
	}
	@Override
	public void polygonOffset(float _factor, float _units) {
		gl.glPolygonOffset(_factor, _units);
	}

	@Override
	public void lightGlobalAmbient(Integer _color, boolean _enable) {
		if(_enable) {
			FloatBuffer global_ambient = FloatBuffer.allocate(4);
			//			global_ambient[0] = _color.argb32.r / 255.0f;
			//			global_ambient[1] = _color.argb32.g / 255.0f;
			//			global_ambient[2] = _color.argb32.b / 255.0f;
			//			global_ambient[3] = _color.argb32.a / 255.0f;

			gl.glEnable(GL2.GL_LIGHTING);
			gl.glShadeModel(GL2.GL_SMOOTH);
			gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, global_ambient);
		} else {
			FloatBuffer global_ambient = FloatBuffer.allocate(4);
			global_ambient.put(0.0f);
			global_ambient.put(0.0f);
			global_ambient.put(0.0f);
			global_ambient.put(0.0f);

			gl.glDisable(GL2.GL_LIGHTING);
			gl.glShadeModel(GL2.GL_SMOOTH);
			gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, global_ambient);
		}
	}

	@Override
	public void light(TLightID _id, boolean _value) {
		if(_value == true) {
			if(s_LightCount != 0)
				gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(_id.GL());
			s_LightCount++;
		} else {
			gl.glDisable(_id.GL());
			if(--s_LightCount == 0)
				gl.glDisable(GL2.GL_LIGHTING);
		}
	}
	@Override
	public void light(TLightID _id, LightParameter _value, int _integer) {
		gl.glLighti(_id.GL(), GLEnums.get(_value), _integer);
	}
	@Override
	public void light(TLightID _id, LightParameter _value, float _real) {
		gl.glLightf(_id.GL(), GLEnums.get(_value), _real);
	}
	@Override
	public void light(TLightID _id, LightParameter _value, Integer _color) {
		IntBuffer color = IntBuffer.allocate(4);
		color.put(0);
		color.put(1);
		color.put(2);
		color.put(3);
		gl.glLightiv(_id.GL(), GLEnums.get(_value), color);
	}
	@Override
	public void light(TLightID _id, LightParameter _value, Point3D _vector) {
		throw new NotYetImplementedException();
//		gl.glLightfv(_id.GL(), GLEnums.get(_value), _vector.asBuffer());
	}
	@Override
	public void light(TLightID _id, LightParameter _value, Vector3D _vector) {
		throw new NotYetImplementedException();
//		gl.glLightfv(_id.GL(), GLEnums.get(_value), _vector.asBuffer());
	}
	@Override
	public void light(TLightID _id, LightParameter _value, DoubleVector4D _vector) {
		throw new NotYetImplementedException();
//		gl.glLightfv(_id.GL(), GLEnums.get(_value), _vector.asBuffer());
	}

	@Override
	public void color(int _color) {
		byte red   = (byte) ((_color >> 24) & 0xFF); //_color.rgba32.r / 255.0f;
		byte green = (byte) ((_color >> 16) & 0xFF); //_color.rgba32.g / 255.0f;
		byte blue  = (byte) ((_color >>  8) & 0xFF); //_color.rgba32.b / 255.0f;
		byte alpha = (byte) ((_color      ) & 0xFF); //_color.rgba32.a / 255.0f;

//		gl.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
		gl.glColor4ub(red, green, blue, alpha);
//		gl.glColor4f(_red, _green, _blue, _alpha);
	}
	@Override
	public void color(Integer _color) {
		throw new NotYetImplementedException();
	}
	@Override
	public void color(byte _red, byte _green, byte _blue) {
		gl.glColor3ub(_red, _green, _blue);
	}
	@Override
	public void color(byte _red, byte _green, byte _blue, byte _alpha) {
		gl.glColor4ub(_red, _green, _blue, _alpha);
	}
	@Override
	public void color(float _red, float _green, float _blue) {
		gl.glColor3f(_red, _green, _blue);
	}
	@Override
	public void color(float _red, float _green, float _blue, float _alpha) {
		gl.glColor4f(_red, _green, _blue, _alpha);
	}
	@Override
	public void color(double _red, double _green, double _blue) {
		gl.glColor3d(_red, _green, _blue);
	}
	@Override
	public void color(double _red, double _green, double _blue, double _alpha) {
		gl.glColor4d(_red, _green, _blue, _alpha);
	}

	@Override
	public void colorMask(boolean _red, boolean _green, boolean _blue, boolean _alpha) {
		gl.glColorMask(_red, _green, _blue, _alpha);
	}
	
		@Override
	public void material(boolean _value) {
		if(_value == true) {
			gl.glPushAttrib(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
		} else {
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glPopAttrib();
		}
	}
	@Override
	public void material(FaceType _ft, MaterialParameter _value, int _integer) {
		gl.glMateriali(GLEnums.get(_ft), GLEnums.get(_value), _integer);
	}
	@Override
	public void material(FaceType _ft, MaterialParameter _value, Integer _color) {
		IntBuffer color = IntBuffer.allocate(4);
		//		color[0] = _color.argb32.r;
		//		color[1] = _color.argb32.g;
		//		color[2] = _color.argb32.b;
		//		color[3] = _color.argb32.a;
		gl.glMaterialiv(GLEnums.get(_ft), GLEnums.get(_value), color);
	}
	@Override
	public void material(FaceType _ft, MaterialParameter _value, float _real) {
		gl.glMaterialf(GLEnums.get(_ft), GLEnums.get(_value), _real);
	}
	@Override
	public void material(FaceType _ft, MaterialParameter _value, DoubleVector4D _vector) {
			FloatBuffer color = FloatBuffer.allocate(4);
			//		vector[0] = _vector.x;
			//		vector[1] = _vector.y;
			//		vector[2] = _vector.z;
			//		vector[3] = _vector.w;
			throw new NotYetImplementedException();
//			gl.glMaterialfv(GLEnums.get(_ft), GLEnums.get(_value), _vector.asBuffer());
		}

	@Override
	public void setupTextureUnit(int _unit, TextureOp _Op, TextureArg _arg1, TextureArg _arg2, Integer _constant) {
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glActiveTexture(GL2.GL_TEXTURE0 + _unit);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);

		switch (_Op) {
		case TXO_ALPHA_FIRSTARG:
		case TXO_ALPHA_ADD:
		case TXO_ALPHA_MODULATE:
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GLEnums.get(_Op));
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GLEnums.get(_arg1));
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GLEnums.get(_arg2));
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL.GL_SRC_COLOR);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL.GL_SRC_COLOR);
			break;
		case TXO_COLOR_FIRSTARG:
		case TXO_COLOR_ADD:
		case TXO_COLOR_MODULATE:
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_ALPHA, GLEnums.get(_Op));
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_ALPHA, GLEnums.get(_arg1));
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_ALPHA, GLEnums.get(_arg2));
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_ALPHA, GL.GL_SRC_ALPHA);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_ALPHA, GL.GL_SRC_ALPHA);
			break;
		default:
			break;

		}

		if((_arg1 == TextureArg.TXA_CONSTANT) || (_arg2 == TextureArg.TXA_CONSTANT)) {
			FloatBuffer color = FloatBuffer.allocate(4);
			color.put(0.f/*_constant*/);
			color.put(0.f/*_constant*/);
			color.put(0.f/*_constant*/);
			color.put(0.f/*_constant*/);
			gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_COLOR, color);
		}
	}

	@Override
	public void indexMask(int _mask) {
		gl.glIndexMask(_mask);
	}
	@Override
	public void logicOp(LogicOperator _operator) {
		gl.glLogicOp(GLEnums.get(_operator));
	}
	@Override
	public void hint(THintTarget _target, THintMode _mode) {
		gl.glHint(_target.GL(), _mode.GL());
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR      PPPP   RRRR   IIIII  M   M  IIIII  TTTTT  IIIII  V   V  EEEEE
	 *     D   D  R   R  A   A  W   W  E      R   R     P   P  R   R    I    MM MM    I      T      I    V   V  E    
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR      PPPP   RRRR     I    M M M    I      T      I    V   V  EEE  
	 *     D   D  R  R   A   A  W W W  E      R  R      P      R  R     I    M   M    I      T      I     V V   E    
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R     P      R   R  IIIII  M   M  IIIII    T    IIIII    V    EEEEE
 	 */
	@Override
	public void begin(PrimitiveType _primitive) {
		gl.glBegin(GLEnums.get(_primitive));
	}

	@Override
	public void vertex(Point2D _pt) {
		gl.glVertex2d(_pt.getX(),  _pt.getY());
	}
	@Override
	public void vertex(Point3D _pt) {
		if(_pt instanceof Point3D)
			gl.glVertex3d(_pt.getX(),  _pt.getY(),  _pt.getZ());
		else
			gl.glVertex3f((float) _pt.getX(), (float) _pt.getY(), (float) _pt.getZ());
	}

	@Override
	public void vertex(float _x, float _y) {
		gl.glVertex2f(_x,  _y);
	}
	@Override
	public void vertex(float _x, float _y, float _z) {
		gl.glVertex3f(_x,  _y,  _z);
	}
	@Override
	public void vertex(float _x, float _y, float _z, float _w) {
		gl.glVertex4f(_x,  _y,  _z, _w);
	}
	@Override
	public void vertex(FloatBuffer _fb) {
		if(_fb.capacity() < 2)
			throw new IllegalArgumentException();

		switch(_fb.capacity()) {
		case 2 : gl.glVertex2fv(_fb); return ;
		case 3 : gl.glVertex3fv(_fb); return ;
		case 4 : gl.glVertex4fv(_fb); return ;
		}
	}
	@Override
	public void vertex(double _x, double _y) {
		gl.glVertex2d(_x,  _y);
	}
	@Override
	public void vertex(double _x, double _y, double _z) {
		gl.glVertex3d(_x,  _y,  _z);
	}
	@Override
	public void vertex(double _x, double _y, double _z, double _w) {
		gl.glVertex4d(_x,  _y,  _z, _w);
	}
	@Override
	public void vertex(DoubleBuffer _db) {
		if(_db.capacity() < 2)
			throw new IllegalArgumentException();

		switch(_db.capacity()) {
		case 2 : gl.glVertex2dv(_db); return ;
		case 3 : gl.glVertex3dv(_db); return ;
		case 4 : gl.glVertex4dv(_db); return ;
		}
	}
	

	@Override
	public void normal(Vector3D _v) {
		gl.glNormal3d(_v.getX(),  _v.getY(),  _v.getZ());
	}
	
	public void normal(float _x, float _y, float _z) {
		gl.glNormal3f(_x,  _y,  _z);
	}
	public void normal(FloatBuffer _fb) {
		gl.glNormal3fv(_fb);
	}

	public void normal(double _x, double _y, double _z) {
		gl.glNormal3d(_x,  _y,  _z);
	}
	public void normal(DoubleBuffer _db) {
		gl.glNormal3dv(_db);
	}

	@Override
	public void texCoords(Point2D _pt) {
		gl.glTexCoord2d(_pt.getX(),  _pt.getY());
	}
//	public void texCoords(Point2D.Float _pt) {
//		gl.glTexCoord2f(_pt.getX(),  _pt.getY());
//	}

	@Override
	public void texCoords(Point3D _pt) {
		gl.glTexCoord3d(_pt.getX(),  _pt.getY(),  _pt.getZ());
	}
//	@Override
//	public void texCoords(Point3D.Float _pt) {
//		gl.glTexCoord3f(_pt.getX(),  _pt.getY(),  _pt.getZ());
//	}

	@Override
	public void texCoords(float _x, float _y) {
		gl.glTexCoord2f(_x, _y);
	}
	@Override
	public void texCoords(float _x, float _y, float _z) {
		gl.glTexCoord3f(_x, _y, _z);
	}
	@Override
	public void texCoords(FloatBuffer _fb) {
		if(_fb.capacity() < 1)
			throw new IllegalArgumentException();

		switch(_fb.capacity()) {
		case 1 : gl.glTexCoord1fv(_fb); return ;
		case 2 : gl.glTexCoord2fv(_fb); return ;
		case 3 : gl.glTexCoord3fv(_fb); return ;
		case 4 : gl.glTexCoord4fv(_fb); return ;
		}
	}
	@Override
	public void texCoords(double _x, double _y) {
		gl.glTexCoord2d(_x, _y);
	}
	@Override
	public void texCoords(double _x, double _y, double _z) {
		gl.glTexCoord3d(_x, _y, _z);
	}
	@Override
	public void texCoords(DoubleBuffer _db) {
		if(_db.capacity() < 1)
			throw new IllegalArgumentException();

		switch(_db.capacity()) {
		case 1 : gl.glTexCoord1dv(_db); return ;
		case 2 : gl.glTexCoord2dv(_db); return ;
		case 3 : gl.glTexCoord3dv(_db); return ;
		case 4 : gl.glTexCoord4dv(_db); return ;
		}
	}

	public void end() {
		gl.glEnd();
	}

	@Override
	public void point(Point3D _pt) {
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3d(_pt.getX(),  _pt.getY(),  _pt.getZ());
		gl.glEnd();
	}
	@Override
	public void points(Point3D... _pts) {
		gl.glBegin(GL.GL_POINTS);
		for(Point3D pt : _pts)
			gl.glVertex3d(pt.getX(),  pt.getY(),  pt.getZ());
		gl.glEnd();
	}

	@Override
	public void line(Point3D _a, Point3D _b) {
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(_a.getX(),  _a.getY(),  _a.getZ());
		gl.glVertex3d(_b.getX(),  _b.getY(),  _b.getZ());
		gl.glEnd();
	}
	@Override
	public void curve(Point3D... _pts) {
		gl.glBegin(GL.GL_LINE_STRIP);
		for(Point3D pt : _pts)
			gl.glVertex3d(pt.getX(),  pt.getY(),  pt.getZ());
		gl.glEnd();
	}

	@Override
	public void triangle(Point3D _a, Point3D _b, Point3D _c) {
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex3d(_a.getX(),  _a.getY(),  _a.getZ());
		gl.glVertex3d(_b.getX(),  _b.getY(),  _b.getZ());
		gl.glVertex3d(_c.getX(),  _c.getY(),  _c.getZ());
		gl.glEnd();
	}
	@Override
	public void triangleList(Point3D[]... _pts) {
		gl.glBegin(GL.GL_TRIANGLES);
		for(Point3D[] t : _pts) {
			gl.glVertex3d(t[0].getX(),  t[0].getY(),  t[0].getZ());
			gl.glVertex3d(t[1].getX(),  t[1].getY(),  t[1].getZ());
			gl.glVertex3d(t[2].getX(),  t[2].getY(),  t[2].getZ());
		}
		gl.glEnd();
	}
	@Override
	public void triangleStrip(Point3D... _pts) {
		
	}
	@Override
	public void triangleFan(Point3D... _pts) {
		
	}

	@Override
	public void quad(Point3D _a, Point3D _b, Point3D _c, Point3D _d) {
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3d(_a.getX(),  _a.getY(),  _a.getZ());
		gl.glVertex3d(_b.getX(),  _b.getY(),  _b.getZ());
		gl.glVertex3d(_c.getX(),  _c.getY(),  _c.getZ());
		gl.glVertex3d(_d.getX(),  _d.getY(),  _d.getZ());
		gl.glEnd();
	}
	@Override
	public void quadList(Point3D[]... _pts) {
		
	}

	@Override
	public void polygon(Point3D... _pts) {
		gl.glBegin(GL2.GL_POLYGON);
		for(Point3D pt : _pts)
			gl.glVertex3d(pt.getX(),  pt.getY(),  pt.getZ());
		gl.glEnd();
	}

	/**
	 *     DDDD   RRRR    AAA   W   W  EEEEE  RRRR       OOO   BBB    JJJJJ  EEEEE  TTTTT
	 *     D   D  R   R  A   A  W   W  E      R   R     O   O  B  B     J    E        T
	 *     D   D  RRRR   AAAAA  W   W  EEE    RRRR      O   O  BBBB     J    EEE      T
	 *     D   D  R  R   A   A  W W W  E      R  R      O   O  B   B  J J    E        T
	 *     DDDD   R   R  A   A   W W   EEEEE  R   R      OOO   BBBB    J     EEEEE    T
 	 */
	public void	enable(BufferNature _type) {
		switch(_type) {
		case VertexBuffer:			gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
									break;
		case NormalBuffer:			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
									break;
		case ColorBuffer:			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
									break;
		case IndexBuffer:			gl.glEnableClientState(GL2.GL_INDEX_ARRAY);
									break;
		case PixelBufferPack:		gl.glEnableClientState(GL2.GL_PIXEL_PACK_BUFFER);
									break;
		case PixelBufferUnpack:		gl.glEnableClientState(GL2.GL_PIXEL_UNPACK_BUFFER);
									break;
		default:
									break;
		}
	}

	@Override
	public void setDeclaration(IDeclaration _declaration) {
		/*
		//glColor4f(1, 1, 1, 1);
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		//glDisableClientState(GL_INDEX_ARRAY);
		for(int i = 0; i < 4; ++i) {
			gl.glActiveTexture(GL.GL_TEXTURE0 + i);
			gl.glDisable(GL2.GL_TEXTURE_2D);
		}
		*/

		m_CurrentDeclaration = (GLDeclaration) _declaration;
		/*
		if(m_CurrentDeclaration != null)
			for(GLDeclaration.TElement elt : m_CurrentDeclaration.getElements())
				switch(elt.Usage) {
				case ELT_USAGE_POSITION:	gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
											break;
				case ELT_USAGE_NORMAL:		gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
											break;
				case ELT_USAGE_DIFFUSE:		gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
											break;
				case ELT_USAGE_TEXCOORD0:
				case ELT_USAGE_TEXCOORD1:
				case ELT_USAGE_TEXCOORD2:
				case ELT_USAGE_TEXCOORD3:	gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
											break;
				default:
											break;
				}
		else {
			*/
		if(m_CurrentDeclaration != null) {
			gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
			//glDisableClientState(GL_INDEX_ARRAY);
			for(int i = 0; i < 4; ++i) {
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glDisable(GL2.GL_TEXTURE_2D);
			}
		}
		
	}

	@Override
	public void setVB(int _stream, BufferBase _buffer, long _stride, long _min, long _max) {
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, ((GLBuffer) _buffer).getId());

		GLDeclaration.TElementArray StreamDesc = m_CurrentDeclaration.getStreamElements(_stream);

		for(GLDeclaration.TElement i : StreamDesc) {
			switch (i.Usage) {
			case Position:
				gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
				gl.glVertexPointer(GLEnums.getElementTypeCount(i.Type), GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;

			case Normal:
				gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
				gl.glNormalPointer(GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;

			case DiffuseColor:
				gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
				gl.glColorPointer(GLEnums.getElementTypeCount(i.Type), GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;

			case TexCoord0:
				gl.glActiveTexture(GL.GL_TEXTURE0);
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glClientActiveTexture(GL.GL_TEXTURE0);

				gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(GLEnums.getElementTypeCount(i.Type), GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;

			case TexCoord1:
				gl.glActiveTexture(GL.GL_TEXTURE0);
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glClientActiveTexture(GL.GL_TEXTURE0);
				gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(GLEnums.getElementTypeCount(i.Type), GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;

			case TexCoord2:
				gl.glActiveTexture(GL.GL_TEXTURE0);
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glClientActiveTexture(GL.GL_TEXTURE0);
				gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(GLEnums.getElementTypeCount(i.Type), GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;

			case TexCoord3:
				gl.glActiveTexture(GL.GL_TEXTURE0);
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glClientActiveTexture(GL.GL_TEXTURE0);
				gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
				gl.glTexCoordPointer(GLEnums.getElementTypeCount(i.Type), GLEnums.get(i.Type), (int) _stride, i.Offset + _min * _stride);
				break;
			}
		}
	}
	@Override
	public void setIB(BufferBase _buffer, long _stride) {
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ((GLBuffer) _buffer).getId());
		m_CurrentIndexStride = _stride;
	}

	@Override
	public void drawPrimitives(PrimitiveType _type, long _offset, long _nbIndex) {
		switch (_type) {
		case TriangleList:
			gl.glDrawArrays(GL.GL_TRIANGLES, (int) _offset, (int) _nbIndex * 3);
			break;
		case TriangleStrip:
			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, (int) _offset, (int) _nbIndex + 2);
			break;
		case TriangleFan:
//			gl.glDrawArrays(GL.GL_TRIANGLE_FAN, (int) _offset, (int) _nbIndex + 1);
			gl.glDrawArrays(GL.GL_TRIANGLE_FAN, (int) _offset, (int) _nbIndex);
			break;
		case LineList:
			gl.glDrawArrays(GL.GL_LINES, (int) _offset, (int) _nbIndex * 2);
			break;
		case LineLoop:
			gl.glDrawArrays(GL.GL_LINE_LOOP, (int) _offset, (int) _nbIndex);
			break;
		case LineStrip:
			gl.glDrawArrays(GL.GL_LINE_STRIP, (int) _offset, (int) _nbIndex);
			break;
		case PointList:
			gl.glDrawArrays(GL.GL_POINTS, (int) _offset, (int) _nbIndex);
			break;
		case QuadList:
			gl.glDrawArrays(GL2.GL_QUADS, (int) _offset, (int) _nbIndex);
			break;
		case QuadStrip:
			gl.glDrawArrays(GL2.GL_QUAD_STRIP, (int) _offset, (int) _nbIndex + 2);
			break;
		case Polygon:
			gl.glDrawArrays(GL2.GL_POLYGON, (int) _offset, (int) _nbIndex);
			break;
		}
	}
	@Override
	public void drawIndexedPrimitives(PrimitiveType _type, long _offset, long _count) {
		int indicesType = (m_CurrentIndexStride == 2 ? GL.GL_UNSIGNED_SHORT : GL.GL_UNSIGNED_INT);
		int offset = (int) (_offset * m_CurrentIndexStride);

		/*
				if(isSelecting()) {
					glDisableClientState(GL_VERTEX_ARRAY);
					glDisableClientState(GL_COLOR_ARRAY);
					glDisableClientState(GL_NORMAL_ARRAY);
					glDisableClientState(GL_INDEX_ARRAY);
					for(int i = 0; i < 8; i++) {
						glClientActiveTexture(GL_TEXTURE0 + i);
						glDisableClientState(GL_TEXTURE_COORD_ARRAY);
					}
				}
		*/
		// Affichage des primitives
		switch (_type) {
		case TriangleList:
			gl.glDrawElements(GL.GL_TRIANGLES, (int) _count, indicesType, offset);
			break;
		case TriangleStrip:
			gl.glDrawElements(GL.GL_TRIANGLE_STRIP, (int) _count, indicesType, offset);
			break;
		case TriangleFan:
			gl.glDrawElements(GL.GL_TRIANGLE_FAN, (int) _count, indicesType, offset);
			break;
		case LineList:
			gl.glDrawElements(GL.GL_LINES, (int) _count, indicesType, offset);
			break;
		case LineLoop:
			gl.glDrawElements(GL.GL_LINE_LOOP, (int) _count, indicesType, offset);
			break;
		case LineStrip:
			gl.glDrawElements(GL.GL_LINE_STRIP, (int) _count, indicesType, offset);
			break;
		case PointList:
			gl.glDrawElements(GL.GL_POINTS, (int) _count, indicesType, offset);
			break;
		case QuadList:
			gl.glDrawElements(GL2.GL_QUADS, (int) _count, indicesType, offset);
			break;
		case QuadStrip:
			gl.glDrawElements(GL2.GL_QUAD_STRIP, (int) _count, indicesType, offset);
			break;
		case Polygon:
			gl.glDrawElements(GL2.GL_POLYGON, (int) _count, indicesType, offset);
			break;
		}
	}

    /*
    *   PPPP   IIIII  X   X  EEEEE  L         BBB    U   U  FFFFF  FFFFF  EEEEE  RRRR
    *   P   P    I     X X   E      L         B  B   U   U  F      F      E      R   R
    *   PPPP     I      X    EEE    L         BBBB   U   U  FFF    FFF    EEEE   RRRR
    *   P        I     X X   E      L         B   B  U   U  F      F      E      R  R
    *   P      IIIII  X   X  EEEEE  LLLLL     BBBB    UUU   F      F      EEEEE  R   R
    */
	@Override
	public IDeclaration createDecl(DeclarationElement[] _elements) {
		GLDeclaration Declaration = new GLDeclaration();

		// Parcours des éléments de la déclaration et conversion en élément OpenGL
		int Offset = 0;
		for(DeclarationElement Elt : _elements) {
			// Construction de l'élément OpenGL correspondant
			GLDeclaration.TElement CurrentElement = Declaration.new TElement();
			CurrentElement.Usage = Elt.Usage;
			CurrentElement.Type = Elt.DataType;

			// Incrémentation de l'offset
			CurrentElement.Offset = 0;//Offset;					// TODO:: If used with full streamed (x, y, z, nx, ny, nz, tu, tv)
			Offset += GLEnums.getElementTypeCount(Elt.DataType) * GLEnums.getElementTypeSize(Elt.DataType);

			// Ajout de l'élément courant
			Declaration.addElement(Elt.Stream, CurrentElement);
		}

		return Declaration;
	}
	
	@Override
	public BufferBase createVBO(Primitives _type, long _size, BufferFlag... _flags) {
		GLBuffer newBuffer = new GLBuffer(BufferNature.VertexBuffer, _type, _size);

		int DRAW_TYPE = GL2.GL_STATIC_DRAW; /*GL_DYNAMIC_DRAW*/

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, newBuffer.getId());
		//gl.glBufferData(GL_ARRAY_BUFFER_, _size * _stride, NULL, CEnum::BufferFlags(_flags));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _size * _type.nbBytes(), null, GL2.GL_STATIC_DRAW /*GL_STATIC_DRAW*/);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		return newBuffer;
	}
	@Override
	public void deleteVBO(BufferBase _buffer) {
		int bufID = ((GLBuffer) _buffer).getId();
		IntBuffer ID = IntBuffer.wrap(new int[] { bufID });
		gl.glDeleteBuffers(1, ID);
	}

	@Override
	public BufferBase createIBO(Primitives _type, long _size, BufferFlag... _flags) {
		GLBuffer newBuffer = new GLBuffer(BufferNature.IndexBuffer, _type, _size);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, newBuffer.getId());
		//gl.glBufferData(GL_ARRAY_BUFFER_, _size * _stride, NULL, CEnum::BufferFlags(_flags));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, _size * _type.nbBytes(), null, GL2.GL_STATIC_DRAW /*GL_STATIC_DRAW_*/);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);

		return newBuffer;
	}
	@Override
	public void deleteIBO(BufferBase _buffer) {
		int bufID = ((GLBuffer) _buffer).getId();
		IntBuffer ID = IntBuffer.wrap(new int[] { bufID });
		gl.glDeleteBuffers(1, ID);
	}

	@Override
	public TextureBase createTexture2D(Dimension2D _size, PixelFormat _format, LockFlag _flags) {
		return new GLTexture2D((int) _size.getWidth(), (int) _size.getHeight(), _format);
	}
	@Override
	public void deleteTexture2D(TextureBase _base) {
		glDeleteBuffers(BufferNature.Texture, ((GLTexture2D) _base).getId());
		_base = null;
	}
	@Override
	public TextureBase createTextureCubeMap(Dimension2D _size, PixelFormat _format, LockFlag _flags) {
		return new GLTextureCubeMap((int) _size.getWidth(), (int) _size.getHeight(), GLEnums.getComponentCount(_format), _format, null, null);
	}
	@Override
	public TextureStream createTextureStream2D(TextureBase _texture, StreamHandler2D handler, int nbSlots) {
		assert(_texture instanceof GLTexture2D);
//		return new GLTextureStream((GLTexture2D) _texture, handler, nbSlots);
		return new GLTextureStreamPBO((GLTexture2D) _texture, handler, nbSlots);
//		return new GLTextureStreamPBORange((GLTexture2D) _texture, handler, nbSlots);
	}

	@Override
	public BufferBase createPBO(Primitives _type, long _size, BufferFlag... _flags) {
		GLBuffer newBuffer = new GLBuffer(BufferNature.PixelBufferUnpack, _type, _size);

		gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, newBuffer.getId());
		gl.glBufferData(GL2.GL_PIXEL_UNPACK_BUFFER, _size * _type.nbBytes(), null, GLEnums.get(_flags[0]));
		gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);

		return newBuffer;
	}
	@Override
	public void setPBO(int _stream, BufferBase _buffer, long _stride) {
		setPBO(_stream, _buffer, _stride, null, null);
	}
	@Override
	public void setPBO(int _stream, BufferBase _buffer, long _stride, SimpleRectangle2D _in, SimpleRectangle2D _out) {
		//LOGGER::warning(1) << "NOT IMPLEMENTED YET !!!";
		gl.glEnableClientState(GL2.GL_PIXEL_PACK_BUFFER);
		gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, ((GLBuffer) _buffer).getId());
	}
	@Override
	public void deletePBO(BufferBase _buffer) {
		int bufID = ((GLBuffer) _buffer).getId();
		IntBuffer ID = IntBuffer.wrap(new int[] { bufID });
		gl.glDeleteBuffers(1, ID);
	}

	@Override
	public BufferBase createFBO(Primitives _type, long _size, BufferFlag... _flags) {
		throw new NotYetImplementedException();
	}
	@Override
	public void setFBO(int _stream, BufferBase _buffer, long _stride) {
		throw new NotYetImplementedException();
	}
	@Override
	public void setFBO(int _stream, BufferBase _buffer, long _stride, SimpleRectangle2D _in, SimpleRectangle2D _out) {
		throw new NotYetImplementedException();
	}
	@Override
	public void deleteFBO(BufferBase _buffer) {
		throw new NotYetImplementedException();
	}

	@Override
	public Buffer readPBO(int _w, int _h) {
		IntBuffer Viewport = IntBuffer.allocate(4);
		gl.glGetIntegerv(GL.GL_VIEWPORT, Viewport);
		_w = Viewport.get(2);
		_h = Viewport.get(3);

		IntBuffer readPixels = IntBuffer.allocate(_w * _h);

		gl.glReadPixels(0, 0, _w, _h, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, readPixels);

		return readPixels;
	}


	/**
	 * JOGL Specific...
	 */
	public static int 	glGetInteger(int pname) {
		IntBuffer intBuffer = Buffers.allocateIntBuffer(32);
		gl.glGetIntegerv(pname, intBuffer);
		return intBuffer.get(0);
	}

	public static int 	glGenBuffer(BufferNature _type) {
		IntBuffer intBuffer = Buffers.allocateIntBuffer(1);

		switch(_type) {
		case VertexBuffer		:
		case NormalBuffer		:
		case ColorBuffer		:
		case MapBuffer			:	
		case IndexBuffer		:
		case CustomBuffer		:
		case PixelBufferPack	:
		case PixelBufferUnpack	:	gl.glGenBuffers(1, intBuffer);
									break;
		case Texture			:	gl.glGenTextures(1, intBuffer);
									break;
		case RenderBuffer		:	gl.glGenRenderbuffers(1, intBuffer);
									break;
		case FrameBuffer		:	gl.glGenFramebuffers(1, intBuffer);
									break;
		default					:	throw new NotYetImplementedException();
		}

		return intBuffer.get(0);
	}
	public static int[] glGenBuffers(BufferNature _type, int _n) {
		IntBuffer intBuffer = Buffers.allocateIntBuffer(_n);

		switch(_type) {
		case VertexBuffer		:
		case NormalBuffer		:
		case ColorBuffer		:
		case MapBuffer			:	
		case IndexBuffer		:
		case PixelBufferPack	:
		case PixelBufferUnpack	:	gl.glGenBuffers(_n, intBuffer);
									break;
		case Texture			:	gl.glGenTextures(_n, intBuffer);
									break;
		case RenderBuffer		:	gl.glGenRenderbuffers(_n, intBuffer);
									break;
		case FrameBuffer		:	gl.glGenFramebuffers(_n, intBuffer);
									break;
		default					:	throw new NotYetImplementedException();
		}

		return intBuffer.array();
	}

	public static void glDeleteBuffer(BufferNature _type, int _id) {
		IntBuffer intBuffer = Buffers.allocateIntBuffer(1);
		intBuffer.put(0, _id);

		switch(_type) {
		case VertexBuffer		:
		case NormalBuffer		:
		case ColorBuffer		:
		case MapBuffer			:	
		case IndexBuffer		:

		case CustomBuffer		:
		case PixelBufferPack	:
		case PixelBufferUnpack	:	gl.glDeleteBuffers(1, intBuffer);
									break;
		case Texture			:	gl.glDeleteTextures(1, intBuffer);
									break;
		case RenderBuffer		:	gl.glDeleteRenderbuffers(1, intBuffer);
									break;
		case FrameBuffer		:	gl.glDeleteFramebuffers(1, intBuffer);
									break;
		default					:	throw new NotYetImplementedException();
		}

	}
	public static void glDeleteBuffers(BufferNature _type, int... _ids) {
		IntBuffer intBuffer = Buffers.allocateIntBuffer(_ids.length);
		intBuffer.put(_ids);
		intBuffer.rewind();

		switch(_type) {
		case VertexBuffer		:
		case NormalBuffer		:
		case ColorBuffer		:
		case MapBuffer			:	
		case IndexBuffer		:
		case PixelBufferPack	:
		case PixelBufferUnpack	:	gl.glDeleteBuffers(_ids.length, intBuffer);
									break;
		case Texture			:	gl.glDeleteTextures(_ids.length, intBuffer);
									break;
		case RenderBuffer		:	gl.glDeleteRenderbuffers(_ids.length, intBuffer);
									break;
		case FrameBuffer		:	gl.glDeleteFramebuffers(_ids.length, intBuffer);
									break;
		default					:	throw new NotYetImplementedException();
		}

	}

	public static void glBind(GLBuffer _buffer, boolean _writeAccess) {
		switch(_buffer.getNature()) {
		case VertexBuffer		:
		case NormalBuffer		:
		case ColorBuffer		:
		case MapBuffer			:	
		case IndexBuffer		:	throw new NotYetImplementedException();

		case PixelBufferPack	:	gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, _buffer.getId());
									break;
		case PixelBufferUnpack	:	gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, _buffer.getId());
									break;
		case Texture			:	gl.glBindTexture(GL.GL_TEXTURE_2D, _buffer.getId());
									break;
		case RenderBuffer		:	gl.glBindRenderbuffer(GL_RENDERBUFFER, _buffer.getId());
									break;
		case FrameBuffer		:	gl.glBindFramebuffer(_writeAccess ? GL_DRAW_FRAMEBUFFER : GL_READ_FRAMEBUFFER, _buffer.getId());
									break;

		case CustomBuffer		:	gl.glBindBuffer(_buffer.getCustomNature(), _buffer.getId());
									break;

		default					:	throw new NotYetImplementedException();
		}
	}
	public static void glUnbind(BufferNature _nature, boolean _writeAccess) {
		switch(_nature) {
		case VertexBuffer		:
		case NormalBuffer		:
		case ColorBuffer		:
		case MapBuffer			:	
		case IndexBuffer		:	throw new NotYetImplementedException();

		case PixelBufferPack	:	gl.glBindBuffer(GL2.GL_PIXEL_PACK_BUFFER, 0);
									break;
		case PixelBufferUnpack	:	gl.glBindBuffer(GL2.GL_PIXEL_UNPACK_BUFFER, 0);
									break;
		case Texture			:	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
									break;
		case RenderBuffer		:	gl.glBindRenderbuffer(GL_RENDERBUFFER, 0);
									break;
		case FrameBuffer		:	gl.glBindFramebuffer(_writeAccess ? GL_DRAW_FRAMEBUFFER : GL_READ_FRAMEBUFFER, 0);
									break;

		default					:	throw new NotYetImplementedException();
		}
	}
	public static void glUnbind(int _customNature) {
		gl.glBindBuffer(_customNature, 0);
	}

    public static int checkSampleCapability(final int samples, final GLContextCapabilities caps) {
        if (samples <= 1)
            return samples;

        if (!(caps.OpenGL30 || (caps.GL_EXT_framebuffer_multisample && caps.GL_EXT_framebuffer_blit)))
            throw new UnsupportedOperationException("Multisampled rendering on framebuffer objects is not supported.");

        return Math.min(samples, GLRenderer.glGetInteger(GL_MAX_SAMPLES));
    }

    private static final int TEX_ROW_ALIGNMENT = 16 * 4; // 16 pixels

    public static int getStride(final int width) {
        // Force a packed format on AMD. Their drivers show unstable
        // performance if we mess with (UN)PACK_ROW_LENGTH.
        return gx.isAMD() ?
                width * 4 :
                getStride(width, TEX_ROW_ALIGNMENT);
    }
    public static int getStride(final int width, final int aligment) {
        int stride = width * 4;

        if ((stride & (aligment - 1)) != 0)
            stride += aligment - (stride & (aligment - 1));

        return stride;
    }

}
