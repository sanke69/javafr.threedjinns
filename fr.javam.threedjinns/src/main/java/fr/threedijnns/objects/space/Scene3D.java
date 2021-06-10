package fr.threedijnns.objects.space;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.java.lang.exceptions.InvalidArgumentException;
import fr.threedijnns.gx;
import fr.threedijnns.api.interfaces.nodes.GxCamera;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.interfaces.nodes.GxScene;
import fr.threedijnns.api.interfaces.nodes.space.GxCamera3D;
import fr.threedijnns.api.interfaces.nodes.space.GxLight;
import fr.threedijnns.api.lang.enums.MatrixType;
import fr.threedijnns.objects.base.GxGroupBase;

public class Scene3D extends GxGroupBase implements GxScene {

	protected GxCamera3D			m_Camera;
	protected List<GxRenderable>	m_Renderables;
	protected List<GxLight>			m_Lights;

	protected Integer				m_BackgroundColor;
	
	public Scene3D() {
		super();
		m_Renderables 		= new CopyOnWriteArrayList<GxRenderable>();
		m_Lights      		= new CopyOnWriteArrayList<GxLight>();
		m_BackgroundColor 	= null;
	}

	public void 		backgroundColor(byte _a, byte _r, byte _g, byte _b) {
		m_BackgroundColor = ((_r & 0xFF) << 24) + ((_g & 0xFF) << 16) + ((_b & 0xFF) << 8) + _a;
	}
	public void 		setBackgroundColor(Integer _backgroundColor) {
		m_BackgroundColor = _backgroundColor;
	}
	public Integer 		getBackgroundColor() {
		return m_BackgroundColor;
	}

	public void 		setCamera(GxCamera<?,?> _camera) {
		if(!(_camera instanceof GxCamera3D))
			throw new InvalidArgumentException("No permission to change Camera in 2D");
		setCamera((GxCamera3D) _camera);
	}
	public void 		setCamera(GxCamera3D _camera) {
		m_Camera = _camera;
	}
	public GxCamera3D 	getCamera() {
		return m_Camera;
	}

	public void 		addRenderable(GxRenderable _renderable) {
		if(_renderable != null)
			m_Renderables.add(_renderable);
	}
	public void 		rmvRenderable(GxRenderable _r) {
		m_Renderables.remove( _r );
	}
	public void 		addRenderables(GxRenderable... _renderables) {
		for(GxRenderable rederable : _renderables)
			m_Renderables.add(rederable);
	}
	public void 		rmvRenderables(GxRenderable... _renderables) {
		for(GxRenderable rederable : _renderables)
			m_Renderables.remove(rederable);
	}
	public void 		addRenderables(List<GxRenderable> _renderables) {
		for(GxRenderable rederable : _renderables)
			m_Renderables.add(rederable);
	}
	public void 		rmvRenderables(List<GxRenderable> _renderables) {
		for(GxRenderable rederable : _renderables)
			m_Renderables.remove(rederable);
	}
	
	public void 		addLight(GxLight _light) {
		if(_light != null)
			m_Lights.add(_light);
	}
	public void 		rmvLight(GxLight _light) {
		m_Lights.remove( _light );
	}
	public void 		addLights(GxLight... _lights) {
		for(GxLight light : _lights)
			m_Lights.add(light);
	}
	public void 		rmvLights(GxLight... _lights) {
		for(GxLight light : _lights)
			m_Lights.remove(light);
	}

	@Override
	public void 		process() {
		for(GxRenderable renderable : m_Renderables)
			renderable.process();
	}

	@Override
	public void 		render() {
		if(m_Camera == null)
			return ;

		// RENDERING
		gx.pushMatrix(MatrixType.MAT_PROJECTION);
		gx.loadMatrix(MatrixType.MAT_PROJECTION, m_Camera.projectionMatrix());

		gx.pushMatrix(MatrixType.MAT_MODELVIEW);
		gx.loadMatrix(MatrixType.MAT_MODELVIEW, m_Camera.modelviewMatrix());

		for(GxRenderable renderable : m_Renderables)
			renderable.render();

		gx.popMatrix(MatrixType.MAT_PROJECTION);
		gx.popMatrix(MatrixType.MAT_MODELVIEW);
	}

}
