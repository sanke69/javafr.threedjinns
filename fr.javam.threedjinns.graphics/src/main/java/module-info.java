module javafr.threedjinns.graphics {
	requires java.desktop;

	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;

	requires javafr;
	requires javafr.sdk;
	requires javafr.graphics;
	requires javafr.threedjinns;

	exports fr.javafx.controller.space.behaviors;
	exports fr.javafx.rendering;
	exports fr.javafx.rendering.skin;
	exports fr.javafx.controller.space;
	exports fr.javafx.scene.image;
	exports fr.javafx.controller;

}