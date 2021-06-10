package fr.threedijnns.objects.plane;

import java.util.ArrayList;
import java.util.List;

import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.GxCamera;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.interfaces.nodes.GxScene;
import fr.threedijnns.api.interfaces.nodes.plane.GxCamera2D;
import fr.threedijnns.api.interfaces.nodes.space.GxLight;
import fr.threedijnns.api.lang.enums.BlendEquation;
import fr.threedijnns.api.lang.enums.EngineOption;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.objects.base.GxGroupBase;
import fr.threedijnns.objects.plane.camera.DefaultCamera2D;

public class Scene2D extends GxGroupBase implements GxScene {

	protected GxCamera2D			m_Camera;
	protected List<GxRenderable>	m_Renderables;
	protected List<GxLight>			m_Lights;

	protected Integer				m_BackgroundColor;
	
	public Scene2D(float _w, float _h) {
		super();
		m_Camera = new DefaultCamera2D(0, _w, 0, _h);

		m_Renderables 		= new ArrayList<GxRenderable>();
		m_Lights      		= new ArrayList<GxLight>();
		m_BackgroundColor 	= null;
	}

	public void setBackgroundColor(Integer _backgroundColor) {
		m_BackgroundColor = _backgroundColor;
	}
	public void backgroundColor(byte _r, byte _g, byte _b, byte _a) {
		m_BackgroundColor = ((_r & 0xFF) << 24) + ((_g & 0xFF) << 16) + ((_b & 0xFF) << 8) + _a;
	}
	public Integer getBackgroundColor() {
		return m_BackgroundColor;
	}

	public void setCamera(GxCamera<?,?> _camera) {
		if(!(_camera instanceof GxCamera2D))
			throw new IllegalAccessError("No permission to change Camera in 2D");
		setCamera((GxCamera2D) _camera);
	}
	public void setCamera(GxCamera2D _camera) {
		throw new IllegalAccessError("No permission to change Camera in 2D");
	}
	public GxCamera2D getCamera() {
		return m_Camera;
	}

	public void addRenderable(GxRenderable _renderable) {
		if(_renderable != null)
			m_Renderables.add(_renderable);
	}
	public void rmvRenderable(GxRenderable _r) {
		m_Renderables.remove( _r );
	}

	public void addRenderables(GxRenderable... _renderables) {
		for(GxRenderable rederable : _renderables)
			m_Renderables.add(rederable);
	}
	public void rmvRenderables(GxRenderable... _renderables) {
		for(GxRenderable rederable : _renderables)
			m_Renderables.remove(rederable);
	}
	
	public void addLight(GxLight _light) {
		throw new IllegalAccessError("No lighting in 2D");
	}
	public void rmvLight(GxLight _light) {
		throw new IllegalAccessError("No lighting in 2D");
	}
	public void addLights(GxLight... _lights) {
		throw new IllegalAccessError("No lighting in 2D");
	}
	public void rmvLights(GxLight... _lights) {
		throw new IllegalAccessError("No lighting in 2D");
	}

	@Override
	public void process() {
		for(GxRenderable renderable : m_Renderables)
			renderable.process();
	}

	@Override
	public void render() {
		if(m_Camera == null)
			return ;

		gx.enable(EngineOption.GX_BLEND);
		gx.setBlendEquation(BlendEquation.BLEND_ADD);

		gx.pushMatrix(MatrixType.MAT_PROJECTION);
		gx.loadMatrix(MatrixType.MAT_PROJECTION, m_Camera.projectionMatrix());
		gx.pushMatrix(MatrixType.MAT_MODELVIEW);
		gx.loadMatrix(MatrixType.MAT_MODELVIEW, m_Camera.modelviewMatrix());

		// RENDU DE LA SCENE NON-VBO
		for(GxRenderable renderable : m_Renderables)
			renderable.render();

		gx.popMatrix(MatrixType.MAT_PROJECTION);
		gx.popMatrix(MatrixType.MAT_MODELVIEW);

		gx.disable(EngineOption.GX_BLEND);
	}

}
