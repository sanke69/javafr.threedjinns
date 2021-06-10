package fr.threedijnns.objects.space.shapes.vbo;

import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.interfaces.nodes.space.GxNode3D;
import fr.threedijnns.api.lang.buffer.gxBuffer;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.base.GxObject3DBase;
import fr.threedijnns.objects.space.wrapper.GxFrame3D;

public abstract class AbstractVBObject extends GxObject3DBase {
	public static final boolean buildAtOrigin = false;
	public static final boolean buildUnitMesh = false;

    protected IDeclaration	m_Declaration;
    protected PrimitiveType	m_PrimitiveType;

    protected gxBuffer		m_VertexBuffer;		// <Point3D>
    protected gxBuffer		m_NormalBuffer;		// <Vector3D>
    protected gxBuffer		m_MapBuffer;		// <Vector2d>

    protected gxBuffer		m_IndexBuffer;		// <Integer>

	protected AbstractVBObject() {
		super();
	}
	protected AbstractVBObject(double _w, double _h, double _d) {
		super(_w, _h, _d);
    }

	@Override
	public void render() {
		preRender();

		gx.setPointSize(12.5f);
		gx.setPointAttenuation(5, 10, 1, 1, 1);

		if(polygonMode.isStandard())
			setDeclarationAndBuffers();
		else if(polygonMode == PolygonMode.OnlyLand)
			new GxFrame3D(getFrame(), 1, false).render();

		postRender();
	}
	public abstract void setDeclarationAndBuffers();

//	@Override
	public void preRender() {
		if(polygonMode.isStandard()) {
			gx.pushMatrix(MatrixType.MAT_MODELVIEW);
			gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, frame.getModelMatrix());
//			gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, m_Scale);

			gx.setPolygonMode(polygonMode, faceType);

			if(isColorEnabled())
				gx.setColor(color);
			if(isMaterialEnabled())
				material.enable();
			if(isTextureEnabled() && texture != null)
				texture.enable(0);
		}

//		gx.setDeclaration(m_Declaration);
	}

//	@Override
    public void postRender() {
		if(polygonMode.isStandard()) {
	        if(isColorEnabled())
	        	gx.setColor(0xFFFFFFFF);
	        if(isMaterialEnabled())
	        	material.disable();
	        if(isTextureEnabled() && texture != null)
	        	texture.disable(0);

	        gx.popMatrix(MatrixType.MAT_MODELVIEW);
		}
		

		if(getChildren().size() > 0) {
			gx.pushMatrix(MatrixType.MAT_MODELVIEW);
			gx.loadMatrixMult(MatrixType.MAT_MODELVIEW, frame.getModelMatrix());
			for(GxNode3D child : getChildren())
				if(child instanceof GxRenderable)
					((GxRenderable) child).render();
	        gx.popMatrix(MatrixType.MAT_MODELVIEW);
		}
		
		
    }

}
