module javafr.threedjinns {
	requires transitive java.desktop;
	requires transitive javafr;
	requires transitive javafr.sdk;
//	requires 			javafr.beans;

	exports fr.threedijnns.api.attributes;
	exports fr.threedijnns.api.interfaces;

	exports fr.threedijnns.api.lang.enums;
	exports fr.threedijnns.api.lang.declarations;
	exports fr.threedijnns.api.lang.buffer;
	exports fr.threedijnns.api.lang.buffer.buffers;
	exports fr.threedijnns.api.lang.buffer.texture;
	exports fr.threedijnns.api.lang.buffer.texture.streamers;
	exports fr.threedijnns.api.lang.stream;
	exports fr.threedijnns.api.lang.stream.handlers;
	exports fr.threedijnns.api.lang.stream.engine;

	exports fr.threedijnns.api.interfaces.nodes;
	exports fr.threedijnns.api.interfaces.nodes.plane;
	exports fr.threedijnns.api.interfaces.nodes.space;

	exports fr.threedijnns.api.utils;
	exports fr.threedijnns.api.graphics;

	exports fr.threedijnns.engine;
	exports fr.threedijnns.engine.renderers;

	exports fr.threedijnns.objects.base;

	exports fr.threedijnns.objects.plane;
	exports fr.threedijnns.objects.plane.camera;
	exports fr.threedijnns.objects.plane.shapes;

	exports fr.threedijnns.objects.space;
	exports fr.threedijnns.objects.space.camera;
	exports fr.threedijnns.objects.space.lights;
	exports fr.threedijnns.objects.space.primitives;
	exports fr.threedijnns.objects.space.shapes;
	exports fr.threedijnns.objects.space.shapes.camera;
	exports fr.threedijnns.objects.space.shapes.meshes;
	exports fr.threedijnns.objects.space.shapes.vbo;
	exports fr.threedijnns.objects.space.wrapper;
	exports fr.threedijnns.objects.space.wrapper.quadrics;

	exports fr.threedijnns.meshes.types;

	exports fr.threedijnns.utils;

	exports fr.threedijnns;

}