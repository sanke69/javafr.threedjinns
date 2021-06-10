package fr.threedijnns.objects.space.shapes.vbo;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.math.geometry.space.Point3D;
import fr.java.utils.Buffers3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public class GxCurve3D extends AbstractVBObject {

	protected int m_Precision;

	protected boolean m_LoopCurve;

	public GxCurve3D(Point3D[] _data, boolean _loop) {
		super();
		build(_data, _loop);
	}

	public void setLoop(boolean _enable) {
		m_LoopCurve = _enable;
	}

	@Override
	public void process() {
	}
	@Override
	public void setDeclarationAndBuffers() {
		if(m_VertexBuffer == null)
			return;

		gx.setDeclaration(m_Declaration);

		gx.setVertexBuffer(0, m_VertexBuffer);

		gx.drawPrimitives(m_PrimitiveType, 0, m_VertexBuffer.getCount());

	}

	private void build(Point3D[] _data, boolean _loop) {
		if(_data == null)
			return;

		/// Declaration
		m_Declaration = gx.createDeclaration(new DeclarationElement[] {
				new DeclarationElement(0, ElementUsage.Position, ElementType.Float3)
		});

		/// Primitive Type
		m_PrimitiveType = (m_LoopCurve ? PrimitiveType.LineLoop : PrimitiveType.LineStrip);

		m_VertexBuffer = gx.createVertexBuffer(Buffers3D.asBuffer(_data), Primitives.DOUBLE, (int) (_data.length), (BufferFlag[]) null);

	}

}
