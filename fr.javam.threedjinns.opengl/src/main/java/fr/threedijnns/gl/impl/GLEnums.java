package fr.threedijnns.gl.impl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import fr.java.lang.enums.PixelFormat;
import fr.threedijnns.api.lang.enums.BlendChannel;
import fr.threedijnns.api.lang.enums.BlendEquation;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.DrawBuffer;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.FrameType;
import fr.threedijnns.api.lang.enums.LightParameter;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.enums.LogicFunction;
import fr.threedijnns.api.lang.enums.LogicOperator;
import fr.threedijnns.api.lang.enums.MaterialParameter;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.api.lang.enums.RenderMode;
import fr.threedijnns.api.lang.enums.RenderParameter;
import fr.threedijnns.api.lang.enums.StencilOperator;
import fr.threedijnns.api.lang.enums.TextureArg;
import fr.threedijnns.api.lang.enums.TextureOp;

public class GLEnums {

	public static int get(BlendChannel _blendChannel) {
		switch(_blendChannel) {
	    case AlphaSource		: 	return GL.GL_SRC_ALPHA;
	    case BLEND_SRCALPHASAT	:	return GL.GL_SRC_ALPHA_SATURATE;
	    case BLEND_INVSRCALPHA	:	return GL.GL_ONE_MINUS_SRC_ALPHA;
	    case BLEND_DESTALPHA	:	return GL.GL_DST_ALPHA;
	    case BLEND_INVDESTALPHA	:	return GL.GL_ONE_MINUS_DST_ALPHA;
	    case BLEND_CSTALPHA		:	return GL.GL_SRC_COLOR;
	    case BLEND_INVCSTALPHA	:	return GL.GL_ONE_MINUS_SRC_COLOR;
	    case BLEND_SRCCOLOR		:	return GL.GL_SRC_ALPHA;
	    case BLEND_INVSRCCOLOR	:	return GL.GL_ONE_MINUS_SRC_COLOR;
	    case BLEND_DESTCOLOR	:	return GL.GL_DST_COLOR;
	    case BLEND_INVDESTCOLOR	:	return GL.GL_ONE_MINUS_SRC_COLOR;
	    case BLEND_CSTCOLOR		:	return GL2.GL_CONSTANT_COLOR;
	    case BLEND_INVCSTCOLOR	:	return GL2.GL_ONE_MINUS_CONSTANT_COLOR;
	    case One			:	return GL.GL_ONE;
	    case Zero			:	return GL.GL_ZERO;
		default:					throw new IllegalArgumentException("Unknown BlendChannel: " + _blendChannel);		
		}
	}

	public static int get(BlendEquation _blendEquation) {
		switch(_blendEquation) {
		case BLEND_ADD				: 	return GL.GL_FUNC_ADD;
	    case BLEND_SUBTRACT			: 	return GL.GL_FUNC_SUBTRACT;
	    case BLEND_REVERSE_SUBTRACT	: 	return GL.GL_FUNC_REVERSE_SUBTRACT;
	    case BLEND_MIN				: 	return GL2.GL_MIN;
	    case BLEND_MAX				: 	return GL2.GL_MAX;
		default:					throw new IllegalArgumentException("Unknown BlendEquation: " + _blendEquation);		
		}
	}

	public static int get(BufferFlag _bufferFlag) {
		switch(_bufferFlag) {
		case DynamicDraw:	return GL2.GL_STATIC_DRAW;
		case StaticDraw:	return GL2.GL_DYNAMIC_DRAW;
		default:			throw new IllegalArgumentException("Unknown BufferFlag: " + _bufferFlag);		
		}
	}

	public static int get(BufferNature _bufferNature) {
		switch(_bufferNature) {
		case VertexBuffer:		
		case NormalBuffer:		
		case MapBuffer:			return GL2.GL_ARRAY_BUFFER;
		case IndexBuffer:		return GL2.GL_ELEMENT_ARRAY_BUFFER;

		case RenderBuffer:		return GL2.GL_RENDERBUFFER;
		case FrameBuffer:		return GL2.GL_DRAW_FRAMEBUFFER;
		case PixelBufferPack:	return GL2.GL_PIXEL_PACK_BUFFER;
		case PixelBufferUnpack:	return GL2.GL_PIXEL_UNPACK_BUFFER;

		case CustomBuffer:		return -1;

		default:				throw new IllegalArgumentException("Unknown BufferNature: " + _bufferNature);		
		}
	}
	public static int get(BufferNature _bufferNature, boolean _write) {
		switch(_bufferNature) {
		case VertexBuffer:		
		case NormalBuffer:		
		case MapBuffer:			return GL2.GL_ARRAY_BUFFER;
		case IndexBuffer:		return GL2.GL_ELEMENT_ARRAY_BUFFER;

		case RenderBuffer:		return GL2.GL_RENDERBUFFER;
		case FrameBuffer:		return _write ? GL2.GL_DRAW_FRAMEBUFFER : GL2.GL_READ_FRAMEBUFFER;
		case PixelBufferPack:	return _write ? GL2.GL_COPY_WRITE_BUFFER : GL2.GL_PIXEL_PACK_BUFFER;
		case PixelBufferUnpack:	return GL2.GL_PIXEL_UNPACK_BUFFER;

		case CustomBuffer:		return -1;

		default:				throw new IllegalArgumentException("Unknown BufferNature: " + _bufferNature);		
		}
	}

	public static int get(DrawBuffer _drawBuffer) {
		switch(_drawBuffer) {
		case None			: 	return GL.GL_NONE;
	    case Front			: 	return GL.GL_FRONT;
	    case Back			: 	return GL.GL_BACK;
	    case FrontAndBack	: 	return GL.GL_FRONT_AND_BACK;
	    case Left			: 	return GL2.GL_LEFT;
	    case Right			: 	return GL2.GL_RIGHT;
	    case FrontLeft		: 	return GL2.GL_FRONT_LEFT;
	    case FrontRight		: 	return GL2.GL_FRONT_RIGHT;
	    case BackLeft		: 	return GL2.GL_BACK_LEFT;
	    case BackRight		: 	return GL2.GL_BACK_RIGHT;
		default:				throw new IllegalArgumentException("Unknown DrawBuffer: " + _drawBuffer);		
		}
	}

	public static int get(ElementType _elementType) {
		switch(_elementType) {
		case Float1:	return GL.GL_FLOAT;
		case Float2:	return GL.GL_FLOAT;
		case Float3:	return GL.GL_FLOAT;
		case Float4:	return GL.GL_FLOAT;
		case Double1:	return GL2.GL_DOUBLE;
		case Double2:	return GL2.GL_DOUBLE;
		case Double3:	return GL2.GL_DOUBLE;
		case Double4:	return GL2.GL_DOUBLE;
		case Color:	return GL.GL_UNSIGNED_BYTE;
		default:				throw new IllegalArgumentException("Unknown ElementType: " + _elementType);		
		}
	}
	public static int getElementTypeSize(ElementType _elementType) {
		switch(_elementType) {
		case Float1:
		case Float2:
		case Float3:
		case Float4:	return 4;
		case Double1:	
		case Double2:
		case Double3:
		case Double4:	return 8;
		case Color:	return 4;
		default:				throw new IllegalArgumentException("Unknown ElementType: " + _elementType);		
		}
	}
	public static int getElementTypeCount(ElementType _elementType) {
		switch(_elementType) {
		case Float1:
		case Double1:	return 1;
		case Float2:	
		case Double2:	return 2;
		case Float3:	
		case Double3:	return 3;
		case Float4:	
		case Double4:	return 4;
		case Color:	return 1;
		default:				throw new IllegalArgumentException("Unknown ElementType: " + _elementType);		
		}
	}

	public static int get(EngineOption _option) {
		switch(_option) {
	    case AlphaTest						:	return GL2.GL_ALPHA_TEST;
	    case GX_AUTO_NORMAL						:	return GL2.GL_AUTO_NORMAL;
	    case GX_BLEND							:	return GL2.GL_BLEND;
	    case GX_COLOR_LOGIC_OP					:	return GL2.GL_COLOR_LOGIC_OP;
	    case ColorMaterial					:	return GL2.GL_COLOR_MATERIAL;
	    case GX_COLOR_SUM						:	return GL2.GL_COLOR_SUM;
	    case GX_COLOR_TABLE						:	return GL2.GL_COLOR_TABLE;
	    case GX_CONVOLUTION_1D					:	return GL2.GL_CONVOLUTION_1D;
	    case GX_CONVOLUTION_2D					:	return GL2.GL_CONVOLUTION_2D;
	    case GX_CULL_FACE						:	return GL2.GL_CULL_FACE;
	    case DepthTest						:	return GL2.GL_DEPTH_TEST;
	    case GX_DITHER							:	return GL2.GL_DITHER;
	    case GX_FOG								:	return GL2.GL_FOG;
	    case GX_HISTOGRAM						:	return GL2.GL_HISTOGRAM;
	    case GX_INDEX_LOGIC_OP					:	return GL2.GL_INDEX_LOGIC_OP;
	    case GX_LIGHT0							:	return GL2.GL_LIGHT0;
	    case GX_LIGHT1							:	return GL2.GL_LIGHT1;
	    case GX_LIGHT2							:	return GL2.GL_LIGHT2;
	    case GX_LIGHT3							:	return GL2.GL_LIGHT3;
	    case GX_LIGHT4							:	return GL2.GL_LIGHT4;
	    case GX_LIGHT5							:	return GL2.GL_LIGHT5;
	    case GX_LIGHT6							:	return GL2.GL_LIGHT6;
	    case GX_LIGHT7							:	return GL2.GL_LIGHT7;
//  	case GX_LIGHT8							:	return GL2.GL_LIGHT8;
//  	case GX_LIGHT9							:	return GL2.GL_LIGHT9;
	    case GX_LIGHTING						:	return GL2.GL_LIGHTING;
	    case GX_LINE_SMOOTH						:	return GL2.GL_LINE_SMOOTH;
	    case GX_LINE_STIPPLE					:	return GL2.GL_LINE_STIPPLE;
	    case GX_MAP1_COLOR_4					:	return GL2.GL_MAP1_COLOR_4;
	    case GX_MAP1_INDEX						:	return GL2.GL_MAP1_INDEX;
	    case GX_MAP1_NORMAL						:	return GL2.GL_MAP1_NORMAL;
	    case GX_MAP1_TEXTURE_COORD_1			:	return GL2.GL_MAP1_TEXTURE_COORD_1;
	    case GX_MAP1_TEXTURE_COORD_2			:	return GL2.GL_MAP1_TEXTURE_COORD_2;
	    case GX_MAP1_TEXTURE_COORD_3			:	return GL2.GL_MAP1_TEXTURE_COORD_3;
	    case GX_MAP1_TEXTURE_COORD_4			:	return GL2.GL_MAP1_TEXTURE_COORD_4;
	    case GX_MAP1_VERTEX_3					:	return GL2.GL_MAP1_VERTEX_3;
	    case GX_MAP1_VERTEX_4					:	return GL2.GL_MAP1_VERTEX_4;
	    case GX_MAP2_COLOR_4					:	return GL2.GL_MAP2_COLOR_4;
	    case GX_MAP2_INDEX						:	return GL2.GL_MAP2_INDEX;
	    case GX_MAP2_NORMAL						:	return GL2.GL_MAP2_NORMAL;
	    case GX_MAP2_TEXTURE_COORD_1			:	return GL2.GL_MAP2_TEXTURE_COORD_1;
	    case GX_MAP2_TEXTURE_COORD_2			:	return GL2.GL_MAP2_TEXTURE_COORD_2;
	    case GX_MAP2_TEXTURE_COORD_3			:	return GL2.GL_MAP2_TEXTURE_COORD_3;
	    case GX_MAP2_TEXTURE_COORD_4			:	return GL2.GL_MAP2_TEXTURE_COORD_4;
	    case GX_MAP2_VERTEX_3					:	return GL2.GL_MAP2_VERTEX_3;
	    case GX_MAP2_VERTEX_4					:	return GL2.GL_MAP2_VERTEX_4;
	    case GX_MINMAX							:	return GL2.GL_MINMAX;
	    case GX_MULTISAMPLE						:	return GL2.GL_MULTISAMPLE;
	    case GX_NORMALIZE						:	return GL2.GL_NORMALIZE;
	    case GX_POINT_SMOOTH					:	return GL2.GL_POINT_SMOOTH;
	    case GX_POINT_SPRITE					:	return GL2.GL_POINT_SPRITE;
	    case GX_POLYGON_OFFSET_FILL				:	return GL2.GL_POLYGON_OFFSET_FILL;
	    case GX_POLYGON_OFFSET_LINE				:	return GL2.GL_POLYGON_OFFSET_LINE;
	    case GX_POLYGON_OFFSET_POINT			:	return GL2.GL_POLYGON_OFFSET_POINT;
	    case GX_POLYGON_SMOOTH					:	return GL2.GL_POLYGON_SMOOTH;
	    case GX_POLYGON_STIPPLE					:	return GL2.GL_POLYGON_STIPPLE;
	    case GX_POST_COLOR_MATRIX_COLOR_TABLE	:	return GL2.GL_POST_COLOR_MATRIX_COLOR_TABLE;
	    case GX_POST_CONVOLUTION_COLOR_TABLE	:	return GL2.GL_POST_CONVOLUTION_COLOR_TABLE;
	    case GX_RESCALE_NORMAL					:	return GL2.GL_RESCALE_NORMAL;
	    case GX_SAMPLE_ALPHA_TO_COVERAGE		:	return GL2.GL_SAMPLE_ALPHA_TO_COVERAGE;
	    case GX_SAMPLE_ALPHA_TO_ONE				:	return GL2.GL_SAMPLE_ALPHA_TO_ONE;
	    case GX_SAMPLE_COVERAGE					:	return GL2.GL_SAMPLE_COVERAGE;
	    case GX_SEPARABLE_2D					:	return GL2.GL_SEPARABLE_2D;
	    case GX_SCISSOR_TEST					:	return GL2.GL_SCISSOR_TEST;
	    case GX_STENCIL_TEST					:	return GL2.GL_STENCIL_TEST;
	    case GX_TEXTURE_1D						:	return GL2.GL_TEXTURE_1D;
	    case GX_TEXTURE_2D						:	return GL2.GL_TEXTURE_2D;
	    case GX_TEXTURE_3D						:	return GL2.GL_TEXTURE_3D;
	    case GX_TEXTURE_CUBE_MAP				:	return GL2.GL_TEXTURE_CUBE_MAP;
	    case GX_TEXTURE_GEN_Q					:	return GL2.GL_TEXTURE_GEN_Q;
	    case GX_TEXTURE_GEN_R					:	return GL2.GL_TEXTURE_GEN_R;
	    case GX_TEXTURE_GEN_S					:	return GL2.GL_TEXTURE_GEN_S;
	    case GX_TEXTURE_GEN_T					:	return GL2.GL_TEXTURE_GEN_T;
	    case GX_VERTEX_PROGRAM_POINT_SIZE		:	return GL2.GL_VERTEX_PROGRAM_POINT_SIZE;
	    case GX_VERTEX_PROGRAM_TWO_SIDE			:	return GL2.GL_VERTEX_PROGRAM_TWO_SIDE;
		default:			throw new IllegalArgumentException("Unknown EngineOption: " + _option);		
		}
	}

	public static int get(FaceType _faceType) {
		switch(_faceType) {
		case Front:			return GL.GL_FRONT_AND_BACK;
		case Back:			return GL.GL_BACK;
		case FrontAndBack:	return GL.GL_FRONT_AND_BACK;
		default:			throw new IllegalArgumentException("Unknown FaceType: " + _faceType);		
		}
	}

	public static int get(FrameType _frameType) {
		switch(_frameType) {
	    case NoId:				return 0;
	    case TEX_CUBEMAP_XN:	return GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
	    case TEX_CUBEMAP_XP:	return GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
	    case TEX_CUBEMAP_YN:	return GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
	    case TEX_CUBEMAP_YP:	return GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
	    case TEX_CUBEMAP_ZN:	return GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
	    case TEX_CUBEMAP_ZP:	return GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
		default:				throw new IllegalArgumentException("Unknown FrameType: " + _frameType);		
		}
	}

	public static int get(LockFlag _lock) {
		switch(_lock) {
		case ReadOnly:		return GL2.GL_READ_ONLY;
		case WriteOnly:		return GL2.GL_WRITE_ONLY;
		case ReadWrite:		return GL2.GL_READ_WRITE;
		default:			throw new IllegalArgumentException("Unknown LockFlag: " + _lock);		
		}
	}

	public static int get(LogicFunction _logicFunction) {
		switch(_logicFunction) {
	    case GX_NEVER			:		return GL.GL_NEVER;
	    case GX_LESS_THAN		:		return GL.GL_LESS;
	    case GX_LESS_OR_EQUAL	:		return GL.GL_LEQUAL;
	    case GX_GREATER_THAN	:		return GL.GL_GREATER;
	    case GX_GREAT_OR_EQUAL	:		return GL.GL_GEQUAL;
	    case GX_EQUAL			:		return GL.GL_EQUAL;
	    case GX_NOT_EQUAL		:		return GL.GL_NOTEQUAL;
	    case GX_ALWAYS			:		return GL.GL_ALWAYS;
		default					:		throw new IllegalArgumentException("Unknown LogicFunction: " + _logicFunction);		
		}
	}
	public static int get(LogicOperator _logicOperator) {
		switch(_logicOperator) {
	    case LOGIC_CLEAR			:		return GL.GL_CLEAR;
	    case LOGIC_SET				:		return GL.GL_SET;
	    case LOGIC_COPY				:		return GL.GL_COPY;
	    case LOGIC_COPY_INVERTED	:		return GL.GL_COPY_INVERTED;
	    case LOGIC_NOOP				:		return GL.GL_NOOP;
	    case LOGIC_INVERT			:		return GL.GL_INVERT;
	    case LOGIC_AND				:		return GL.GL_AND;
	    case LOGIC_NAND				:		return GL.GL_NAND;
	    case LOGIC_OR				:		return GL.GL_OR;
	    case LOGIC_NOR				:		return GL.GL_NOR;
	    case LOGIC_XOR				:		return GL.GL_XOR;
	    case LOGIC_EQUIV			:		return GL.GL_EQUIV;
	    case LOGIC_AND_REVERSE		:		return GL.GL_AND_REVERSE;
	    case LOGIC_AND_INVERTED		:		return GL.GL_AND_INVERTED;
	    case LOGIC_OR_REVERSE		:		return GL.GL_OR_REVERSE;
	    case LOGIC_OR_INVERTED		:		return GL.GL_OR_INVERTED;
		default						:		throw new IllegalArgumentException("Unknown LogicOperator: " + _logicOperator);		
		}
	}

	public static int get(LightParameter _lightParameter) {
		switch(_lightParameter) {
	    case LGT_POSITION				:		return GL2.GL_POSITION;
	    case LGT_AMBIENT				:		return GL2.GL_AMBIENT;
	    case LGT_DIFFUSE				:		return GL2.GL_DIFFUSE;
	    case LGT_SPECULAR				:		return GL2.GL_SPECULAR;
	    case LGT_SPOT_CUTOFF			:		return GL2.GL_SPOT_CUTOFF;
	    case LGT_SPOT_DIRECTION			:		return GL2.GL_SPOT_DIRECTION;
	    case LGT_SPOT_EXPONENT			:		return GL2.GL_SPOT_EXPONENT;
	    case LGT_CONSTANT_ATTENUATION	:		return GL2.GL_CONSTANT_ATTENUATION;
	    case LGT_LINEAR_ATTENUATION		:		return GL2.GL_LINEAR_ATTENUATION;
	    case LGT_QUADRATIC_ATTENUATION	:		return GL2.GL_QUADRATIC_ATTENUATION;
		default:			throw new IllegalArgumentException("Unknown LightParameter: " + _lightParameter);		
		}
	}

	public static int get(MaterialParameter _materialParameter) {
		switch(_materialParameter) {
	    case AmbientColor				:		return GL2.GL_AMBIENT;
	    case DiffuseColor				:		return GL2.GL_DIFFUSE;
	    case SpecularColor				:		return GL2.GL_SPECULAR;
	    case Shininess				:		return GL2.GL_SHININESS;
	    case MAT_EMISSION				:		return GL2.GL_EMISSION;
	    case MAT_AMBIENT_AND_DIFFUSE	:		return GL2.GL_AMBIENT_AND_DIFFUSE;
	    case MAT_COLOR_INDEXES			:		return GL2.GL_COLOR_INDEXES;
		default							:		throw new IllegalArgumentException("Unknown MaterialParameter: " + _materialParameter);		
		}
	}

	 
	public static int get(MatrixType _matrixType) {
		switch(_matrixType) {
	 	case MAT_MODELVIEW		:		return GL2.GL_MODELVIEW;
	    case MAT_PROJECTION		:		return GL2.GL_PROJECTION;
	    case MAT_TEXTURE_0		:		return GL.GL_TEXTURE;
	    case MAT_TEXTURE_1		:		return GL.GL_TEXTURE;
	    case MAT_TEXTURE_2		:		return GL.GL_TEXTURE;
	    case MAT_TEXTURE_3  	:		return GL.GL_TEXTURE;
		default					:		throw new IllegalArgumentException("Unknown MatrixType: " + _matrixType);		
		}
	}

	public static int getPixelFormat(PixelFormat _pixelFormat) {
		switch(_pixelFormat) {
		case PXF_L8			:	return GL.GL_LUMINANCE;
		case PXF_AL8		:	return GL.GL_LUMINANCE_ALPHA;
		case PXF_RGB8		:	return GL.GL_RGB;
		case PXF_BGR8		:	return GL.GL_BGR;

		case PXF_A1R5G5B5	:	return GL.GL_RGBA;
		case PXF_A4R4G4B4	:	return GL.GL_RGBA;
		case PXF_YUV8		:	return GL.GL_BGR;
		case PXF_ARGB8		:	return GL.GL_RGBA;
		case PXF_RGBA8		:	return GL.GL_RGBA;
		case PXF_ABGR8		:	return GL.GL_RGBA;
		case PXF_BGRA8		:	return GL.GL_RGBA;
		case PXF_DXTC1		:	return GL.GL_RGBA;
		case PXF_DXTC3		:	return GL.GL_RGBA;
		case PXF_DXTC5		:	return GL.GL_RGBA;
		case PXF_UNKNOWN	:	throw new IllegalArgumentException("Unknown PixelFormat: " + _pixelFormat);
		default				:	throw new IllegalArgumentException("Unknown PixelFormat: " + _pixelFormat);
		}
	}
	public static int getInternalFormat(PixelFormat _pixelFormat) {
		switch(_pixelFormat) {
		case PXF_L8			:	return GL.GL_LUMINANCE8;
		case PXF_AL8		:	return GL.GL_LUMINANCE8_ALPHA8;
		case PXF_A1R5G5B5	:	return GL.GL_RGB5_A1;
		case PXF_A4R4G4B4	:	return GL.GL_RGBA4;
		case PXF_YUV8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_RGB8		:	return GL.GL_RGB;
		case PXF_BGR8		:	return GL.GL_BGR;
		case PXF_ARGB8		:	return GL.GL_BGRA;
		case PXF_ABGR8		:	return GL.GL_RGBA;
		case PXF_RGBA8		:	return GL.GL_RGBA;
		case PXF_BGRA8		:	return GL.GL_RGBA;
		case PXF_D24S8		:	return GL.GL_DEPTH24_STENCIL8;
		case PXF_DXTC1		:	return GL.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
		case PXF_DXTC3		:	return GL.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
		case PXF_DXTC5		:	return GL.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
		case PXF_UNKNOWN	:	throw new IllegalArgumentException("Unknown PixelFormat: " + _pixelFormat);
		default				:	throw new IllegalArgumentException("Unknown PixelFormat: " + _pixelFormat);
		}
	}
	public static int getPixelType(PixelFormat _pixelFormat) {
		switch(_pixelFormat) {
		case PXF_L8			:	return GL.GL_UNSIGNED_BYTE;
		case PXF_AL8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_RGB8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_BGR8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_A1R5G5B5	:	return GL.GL_UNSIGNED_SHORT_5_5_5_1;
		case PXF_A4R4G4B4	:	return GL.GL_UNSIGNED_SHORT_4_4_4_4;
		case PXF_YUV8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_ARGB8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_RGBA8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_BGRA8		:	return GL.GL_UNSIGNED_BYTE;
		case PXF_DXTC1		:	return 0;
		case PXF_DXTC3		:	return 0;
		case PXF_DXTC5		:	return 0;
		case PXF_UNKNOWN	:	throw new IllegalArgumentException("Unknown PixelFormat: " + _pixelFormat);
		default				:	throw new IllegalArgumentException("Unknown PixelFormat: " + _pixelFormat);
		}
	}
	@Deprecated
    public static int getBytesPerPixel(PixelFormat _pixelFormat) {
        switch(_pixelFormat) {
            case PXF_L8       : return 1;
            case PXF_AL8      : return 2;
            case PXF_RGB8     : return 3;
            case PXF_BGR8     : return 3;

            case PXF_A1R5G5B5 : return 2;
            case PXF_A4R4G4B4 : return 2;
            case PXF_ARGB8 : return 4;
            case PXF_DXTC1    : return 1;
            case PXF_DXTC3    : return 2;
            case PXF_DXTC5    : return 2;
            case PXF_YUV8   : return 3;
            default           : return 0;
        }
    }
    @Deprecated
	public static int getComponentCount(PixelFormat _pixelFormat) {
		switch(_pixelFormat) {
		case PXF_L8			:	return 1;
		case PXF_AL8		:
		case PXF_A1R5G5B5	:
		case PXF_A4R4G4B4	:	return 2;
		case PXF_YUV8		:
		case PXF_BGR8		:
		case PXF_RGB8		:	return 3;
		case PXF_ARGB8	:   return 4;
		case PXF_DXTC1		:
		case PXF_DXTC3		:
		case PXF_DXTC5		:
		case PXF_UNKNOWN	:	return -1;
		default				:	throw new IllegalArgumentException("Unknown MatrixType: " + _pixelFormat);
		}
	}
	
	
	public static int get(PolygonMode _polygonMode) {
		switch(_polygonMode) {
		case OnlyVertex:	return GL2.GL_POINT;
		case OnlySkeleton:	return GL2.GL_LINE;
		case Realistic:		return GL2.GL_FILL;
		default:			throw new IllegalArgumentException("Unknown PolygonMode: " + _polygonMode);		
		}
	}

	public static int get(PrimitiveType _primitiveType) {
		switch(_primitiveType) {
		case PointList:		return GL.GL_POINTS;
		case LineList:		return GL.GL_LINES;
		case LineLoop:		return GL.GL_LINE_LOOP;
		case LineStrip:		return GL.GL_LINE_STRIP;
		case TriangleList:	return GL.GL_TRIANGLES;
		case TriangleStrip:	return GL.GL_TRIANGLE_STRIP;
		case TriangleFan:	return GL.GL_TRIANGLE_FAN;
		case QuadList:		return GL2.GL_QUADS;
		case QuadStrip:		return GL2.GL_QUAD_STRIP;
		case Polygon:		return GL2.GL_POLYGON;
		default:			throw new IllegalArgumentException("Unknown PrimitiveType: " + _primitiveType);		
		}
	}
   
	public static int get(RenderMode _renderMode) {
		switch(_renderMode) {
	    case CLEAR	:	return GL.GL_ZERO;
		case RENDER	:	return GL2.GL_RENDER;
	    case SELECT	:	return GL2.GL_SELECT;
		default		:	throw new IllegalArgumentException("Unknown RenderMode: " + _renderMode);		
		}
	}

	public static int get(RenderParameter _renderParam) {
		switch(_renderParam) {
		case RENDER_ZWRITE			:	return 0;
	    case RENDER_ALPHABLEND		:	return GL.GL_BLEND;
	    case RENDER_SMOTH_POINT		:	return GL2.GL_POINT_SMOOTH;
	    case RENDER_SPRITE_POINT	:	return GL2.GL_POINT_SPRITE;
		default						:	throw new IllegalArgumentException("Unknown RenderParameter: " + _renderParam);		
		}
	}

	public static int get(StencilOperator _stencilOp) {
		switch(_stencilOp) {
	    case STENCIL_KEEP			:	return GL.GL_KEEP;
	    case STENCIL_ZERO			:	return GL.GL_ZERO;
	    case STENCIL_REPLACE		:	return GL.GL_REPLACE;
	    case STENCIL_INCR			:	return GL.GL_INCR;
	    case STENCIL_INCR_WRAP		:	return GL.GL_INCR_WRAP;
	    case STENCIL_DECR			:	return GL.GL_DECR;
	    case STENCIL_DECR_WRAP		:	return GL.GL_DECR_WRAP;
	    case STENCIL_INVERT			:	return GL.GL_INVERT;
		default						:	throw new IllegalArgumentException("Unknown StencilOperator: " + _stencilOp);		
		}
	}

	public static int get(TextureArg _textureArg) {
		switch(_textureArg) {
	    case TXA_TEXTURE	:	return GL.GL_TEXTURE;
	    case TXA_DIFFUSE	:	return GL2.GL_PRIMARY_COLOR;
	    case TXA_PREVIOUS	:	return GL2.GL_PREVIOUS;
	    case TXA_CONSTANT 	:	return GL2.GL_CONSTANT;
		default				:	throw new IllegalArgumentException("Unknown TextureArg: " + _textureArg);		
		}
	}

	public static int get(TextureOp _textureOp) {
		switch(_textureOp) {
	    case TXO_COLOR_FIRSTARG		:	return GL.GL_REPLACE;
	    case TXO_COLOR_ADD			:	return GL2.GL_ADD;
	    case TXO_COLOR_MODULATE		:	return GL2.GL_MODULATE;
	    case TXO_ALPHA_FIRSTARG		:	return GL.GL_REPLACE;
	    case TXO_ALPHA_ADD			:	return GL2.GL_ADD;
	    case TXO_ALPHA_MODULATE		:	return GL2.GL_MODULATE;
		default						:	throw new IllegalArgumentException("Unknown StencilOperator: " + _textureOp);		
		}
	}

}
