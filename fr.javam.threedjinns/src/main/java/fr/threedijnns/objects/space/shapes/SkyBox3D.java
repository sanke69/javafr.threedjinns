package fr.threedijnns.objects.space.shapes;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.Points;
import fr.java.maths.algebra.Vectors;
import fr.java.utils.Buffers3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.attributes.TextureCubeMap;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.lang.buffer.gxBuffer;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public class SkyBox3D implements GxRenderable {

	private IDeclaration	m_Declaration;
	private PrimitiveType	m_PrimitiveType;

	private gxBuffer		m_VertexBuffer;
	private gxBuffer		m_MapBuffer;
	private gxBuffer		m_IndexBuffer;

	private TextureCubeMap 	m_Texture;

	public SkyBox3D() {
		super();

		gx.runLater(() -> build());
	}
	public SkyBox3D(float _w, float _h, float _d) {
		super();
	}

	public TextureCubeMap Texture() {
		if(m_Texture == null)
			m_Texture = new TextureCubeMap();
		return m_Texture;
	}

//	@Override
	public void process() {
		;		
	}

//	@Override
	public void render0() {
		gx.pushMatrix(MatrixType.MAT_MODELVIEW);

		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);

		if(m_Texture != null && m_Texture.getTextureBase() != null)
			m_Texture.enable(0);

		gx.begin(PrimitiveType.TriangleList);
		/* Xneg */ drawFace3D(0, 1, 2, 1, 3, 2);
		/* Xpos */ drawFace3D(4, 5, 6, 5, 7, 6);
        /* Yneg */ drawFace3D(0, 4, 2, 2, 4, 6);
        /* Ypos */ drawFace3D(1, 5, 3, 3, 5, 7);
        /* Zneg */ drawFace3D(0, 1, 4, 1, 4, 5);
        /* Zpos */ drawFace3D(2, 3, 6, 3, 6, 7);
        /**/
		gx.end();

		if(m_Texture != null && m_Texture.getTextureBase() != null)
			m_Texture.disable(0);

		gx.popMatrix(MatrixType.MAT_MODELVIEW);
	}
	
	public void render() {
/*
		float d = 0.1f;
		Camera cam;
		cam.pitched(d);
		cam.yawed(d);
		cam.rolled(d);

		gx.pushMatrix(TMatrixType.MAT_MODELVIEW);
		gx.loadMatrix(TMatrixType.MAT_MODELVIEW, cam.modelview());
*/
		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);
		gx.setDeclaration(m_Declaration);

		if(m_Texture != null && m_Texture.getTextureBase() != null)
			m_Texture.enable(0);

		/**/
		gx.setVertexBuffer(0, m_VertexBuffer);
		gx.setVertexBuffer(1, m_MapBuffer);
		gx.setIndexBuffer(m_IndexBuffer);

		gx.drawIndexedPrimitives(m_PrimitiveType, 0L, m_IndexBuffer.getCount());
		/**/

		if(m_Texture != null && m_Texture.getTextureBase() != null)
			m_Texture.disable(0);

//		gx.popMatrix(TMatrixType.MAT_MODELVIEW);
	}

	void build() {
        float	t = 5.0f;

        /// Création des Vertex
        Point3D[]  vertex_tab = new Point3D[8];
        Vector3D[]    map_tab = new Vector3D[8];
        int[]       index_tab = new int[36];

        vertex_tab[0] = Points.of(-t, -t, -t);
        map_tab[0]    = Vectors.of(0.0f, 0.0f, 0.0f);

        vertex_tab[1] = Points.of(-t,  t, -t);
        map_tab[1]    = Vectors.of(0.0f, 1.0f, 0.0f);

        vertex_tab[2] = Points.of(-t, -t,  t);
        map_tab[2]    = Vectors.of(0.0f, 0.0f, 1.0f);

        vertex_tab[3] = Points.of(-t,  t,  t);
        map_tab[3]    = Vectors.of(0.0f, 1.0f, 1.0f);

        vertex_tab[4] = Points.of( t, -t, -t);
        map_tab[4]    = Vectors.of(1.0f, 0.0f, 0.0f);

        vertex_tab[5] = Points.of( t,  t, -t);
        map_tab[5]    = Vectors.of(1.0f, 1.0f, 0.0f);

        vertex_tab[6] = Points.of( t, -t,  t);
        map_tab[6]    = Vectors.of(1.0f, 0.0f, 1.0f);

        vertex_tab[7] = Points.of( t,  t,  t);
        map_tab[7]    = Vectors.of(1.0f, 1.0f, 1.0f);

        /* Xneg */ index_tab[ 0] = 0; index_tab[ 1] = 1; index_tab[ 2] = 2; index_tab[ 3] = 1; index_tab[ 4] = 3; index_tab[ 5] = 2;
        /* Xpos */ index_tab[ 6] = 4; index_tab[ 7] = 5; index_tab[ 8] = 6; index_tab[ 9] = 5; index_tab[10] = 7; index_tab[11] = 6;
        /* Yneg */ index_tab[12] = 0; index_tab[13] = 4; index_tab[14] = 2; index_tab[15] = 2; index_tab[16] = 4; index_tab[17] = 6;
        /* Ypos */ index_tab[18] = 1; index_tab[19] = 5; index_tab[20] = 3; index_tab[21] = 3; index_tab[22] = 5; index_tab[23] = 7;
        /* Zneg */ index_tab[24] = 0; index_tab[25] = 1; index_tab[26] = 4; index_tab[27] = 1; index_tab[28] = 4; index_tab[29] = 5;
        /* Zpos */ index_tab[30] = 2; index_tab[31] = 3; index_tab[32] = 6; index_tab[33] = 3; index_tab[34] = 6; index_tab[35] = 7;

        /// Declaration
        m_Declaration	= gx.createDeclaration(new DeclarationElement[] {
    		new DeclarationElement(0, ElementUsage.Position,  ElementType.Double3),
    		new DeclarationElement(1, ElementUsage.TexCoord0, ElementType.Double3)
        });

        /// Primitive Type
        m_PrimitiveType = PrimitiveType.TriangleList;

        m_VertexBuffer  = gx.createVertexBuffer(Buffers3D.asBuffer(vertex_tab), Primitives.DOUBLE, (int) 3 * vertex_tab.length, (BufferFlag[]) null);
        m_MapBuffer     = gx.createVertexBuffer(Buffers3D.asBuffer(map_tab),    Primitives.DOUBLE, (int) 3 * vertex_tab.length, (BufferFlag[]) null);

        m_IndexBuffer   = gx.createIndexBuffer(Buffers3D.getIntBuffer(index_tab), (int) index_tab.length, (BufferFlag[]) null);
    }

	private void drawFace3D(int _i0, int _j0, int _k0, int _i1, int _j1, int _k1) {
		float t = 500.0f;

		Vector3D[] v = new Vector3D[8];
		Vector3D[] m = new Vector3D[8];

        v[0] = Vectors.of(-t, -t, -t);
        m[0] = Vectors.of(-1, -1, -1);

        v[1] = Vectors.of(-t,  t, -t);
        m[1] = Vectors.of(-1,  1, -1);

        v[2] = Vectors.of(-t, -t,  t);
        m[2] = Vectors.of(-1, -1,  1);

        v[3] = Vectors.of(-t,  t,  t);
        m[3] = Vectors.of(-1, 1, 1);

        v[4] = Vectors.of( t, -t, -t);
        m[4] = Vectors.of( 1, -1, -1);

        v[5] = Vectors.of( t,  t, -t);
        m[5] = Vectors.of( 1,  1, -1);

        v[6] = Vectors.of( t, -t,  t);
        m[6] = Vectors.of( 1, -1,  1);

        v[7] = Vectors.of( t,  t,  t);
        m[7] = Vectors.of( 1,  1,  1);

		gx.texCoords(m[_i0].getX(), m[_i0].getY(), m[_i0].getZ());
		gx.vertex	(v[_i0].getX(), v[_i0].getY(), v[_i0].getZ());
		gx.texCoords(m[_j0].getX(), m[_j0].getY(), m[_j0].getZ());
		gx.vertex	(v[_j0].getX(), v[_j0].getY(), v[_j0].getZ());
		gx.texCoords(m[_k0].getX(), m[_k0].getY(), m[_k0].getZ());
		gx.vertex	(v[_k0].getX(), v[_k0].getY(), v[_k0].getZ());

		gx.texCoords(m[_i1].getX(), m[_i1].getY(), m[_i1].getZ());
		gx.vertex	(v[_i1].getX(), v[_i1].getY(), v[_i1].getZ());
		gx.texCoords(m[_j1].getX(), m[_j1].getY(), m[_j1].getZ());
		gx.vertex	(v[_j1].getX(), v[_j1].getY(), v[_j1].getZ());
		gx.texCoords(m[_k1].getX(), m[_k1].getY(), m[_k1].getZ());
		gx.vertex	(v[_k1].getX(), v[_k1].getY(), v[_k1].getZ());
	}

}
