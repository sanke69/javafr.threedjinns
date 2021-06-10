package fr.threedijnns.objects.space.wrapper;

import fr.java.math.geometry.space.BoundingBox3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public class GxBoundingBox3D extends GxAbstractWrapper3D<BoundingBox3D> {

	public GxBoundingBox3D(BoundingBox3D _bb) {
		super(_bb);
	}

	@Override
	public void renderModel() {
		double left   = model().getLeft();
		double right  = model().getRight();
		double top    = model().getTop();
		double bottom = model().getBottom();
		double front  = model().getFront();
		double back   = model().getBack();

		gx.setColor(1.0f, 1.0f, 1.0f, 0.2f);
		gx.setLineStipple(3,(short) 0xFFFF);
		gx.setLineWidth(3.0f);

		gx.begin(PrimitiveType.QuadList);
			gx.vertex(left,		bottom, 	front);
			gx.vertex(right,	bottom, 	front);
			gx.vertex(right,	top, 		front);
			gx.vertex(left,		top, 		front);
	
			gx.vertex(left,		bottom, 	back);
			gx.vertex(right,	bottom, 	back);
			gx.vertex(right,	top, 		back);
			gx.vertex(left,		top, 		back);
		gx.end();

		gx.setColor(1.0f, 1.0f, 1.0f);
		gx.setLineStipple(3, (short) 0xFFFF);
		gx.setLineWidth(1.0f);
		
	}

}
