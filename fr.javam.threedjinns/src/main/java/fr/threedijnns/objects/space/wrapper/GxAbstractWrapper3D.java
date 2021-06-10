package fr.threedijnns.objects.space.wrapper;

import fr.threedijnns.gx;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.FaceType;
import fr.threedijnns.api.lang.enums.PolygonMode;
import fr.threedijnns.objects.base.GxObject3DBase;

public abstract class GxAbstractWrapper3D<MODEL> extends GxObject3DBase {
	final private MODEL model;

	public GxAbstractWrapper3D(MODEL _model) {
		super();
		setVisible(true);

		model = _model;
	}

	@Override
	public void render() {
		if(!isVisible())
			return ;

		preRender();

		gx.disable(EngineOption.GX_LIGHTING);
		gx.setPolygonMode(PolygonMode.OnlySkeleton, FaceType.FrontAndBack);

		renderModel();

		gx.setPolygonMode(PolygonMode.Realistic, FaceType.FrontAndBack);
		gx.enable(EngineOption.GX_LIGHTING);

		postRender();
	}

	public MODEL model() { return model; }

	public abstract void renderModel();

}
