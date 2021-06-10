package fr.threedijnns.objects.space.shapes.vbo;

import java.nio.DoubleBuffer;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.exceptions.NotYetImplementedException;
import fr.java.media.shape.DotCloud;
import fr.java.nio.buffer.Point3DBufferX;
import fr.java.sdk.nio.buffer.Point3DDoubleBufferX;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.buffer.texture.ITexture;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.api.lang.enums.RenderParameter;

public class GxPoints3D extends AbstractVBObject {

    protected float m_Size, m_MinSize, m_MaxSize;
    protected float a, b, c;

	public GxPoints3D() {
		super();
		m_Size    = 1.0f;
		m_MinSize = 1.0f;
		m_MaxSize = 1.0f;
	}
	public GxPoints3D(Point3DBufferX _dots) {
		super();
		m_Size    = 1.0f;
		m_MinSize = 1.0f;
		m_MaxSize = 1.0f;

		if(_dots instanceof Point3DDoubleBufferX)
			build((Point3DDoubleBufferX) _dots);
	}

	public void update(Point3DBufferX _dots) {
        if(m_VertexBuffer != null && m_VertexBuffer.getCount() != 3 * _dots.remaining()) {
        	gx.deleteVertexBuffer(m_VertexBuffer);
        	m_VertexBuffer = null;
        }

        if(m_VertexBuffer == null) {
			if(_dots instanceof Point3DDoubleBufferX)
				build((Point3DDoubleBufferX) _dots);
	
			else if(_dots instanceof DotCloud) {
				DotCloud dots = (DotCloud) _dots;
	
				if(dots.isWrapper()) {
					if(dots.getWrapped() instanceof Point3DDoubleBufferX)
						build((Point3DDoubleBufferX) dots.getWrapped());
				} else
					throw new NotYetImplementedException();
	
			} else {
	
				if(_dots instanceof DoubleBuffer)
					build((DoubleBuffer) _dots.Buffer());
	
			}
        } else {
			if(_dots instanceof Point3DDoubleBufferX)
				m_VertexBuffer.fill(((Point3DDoubleBufferX) _dots).Buffer(), 3 * _dots.remaining());
			
			else if(_dots instanceof DotCloud) {
				DotCloud dots = (DotCloud) _dots;
	
				if(dots.isWrapper()) {
					if(dots.getWrapped() instanceof Point3DDoubleBufferX) {
						Point3DDoubleBufferX buf3d = (Point3DDoubleBufferX) dots.getWrapped();
						m_VertexBuffer.fill(buf3d.Buffer(), 3 * _dots.remaining());
					}
				} else
					throw new NotYetImplementedException();
	
			}
        }
	}

	@Override
	public void process() {
	}
	@Override
	public void setDeclarationAndBuffers() { 
		if(m_VertexBuffer == null)
			return;

		gx.setDeclaration(m_Declaration);

        gx.activate(RenderParameter.RENDER_SMOTH_POINT, true);
        gx.setPointSize(m_Size);
        gx.setPolygonMode(polygonMode, faceType);

        if(texture != null) {
            gx.activate(RenderParameter.RENDER_SPRITE_POINT, true);
            texture.enable(0);
        }

        gx.setVertexBuffer(0, m_VertexBuffer);
        gx.drawPrimitives(m_PrimitiveType, 0, m_VertexBuffer.getCount() / 3);

        if(texture != null) {
            gx.activate(RenderParameter.RENDER_SPRITE_POINT, false);
            texture.disable(0);
        }

	}


    ITexture Sprite() {
        return texture;
    }
    void setPointSize(float _s) {
        m_Size = _s;
    }
    void setAttenuation(float _min, float _max, float _a, float _b, float _c) {
        m_MinSize = _min;
        m_MaxSize = _max;
        a = _a;
        b = _b;
        c = _c;
    }

    void build(Point3DDoubleBufferX _vertice) {
    	build(_vertice.Buffer());
    }
    void build(DoubleBuffer _vertice) {
        if(_vertice == null)
            return ;

        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
       		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.PointList;

        m_VertexBuffer  = gx.createVertexBuffer(_vertice, Primitives.DOUBLE, (int) (_vertice.remaining()), (BufferFlag[]) null);

    }


}
