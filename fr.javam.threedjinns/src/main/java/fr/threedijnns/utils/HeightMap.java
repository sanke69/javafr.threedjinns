package fr.threedijnns.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.java.math.algebra.NumberTensor;
import fr.java.maths.Angles;

public class HeightMap {

	double maxAltitude, maxDepth;
	NumberTensor altitude,    bathymetry;
	
	public HeightMap() {
		super();
	}
	
	public void loadImages(Path _altitudeMap, Path _bathymetryMap) throws IOException {
//		altitude   = TensorImages.loadImage(_altitudeMap, Primitive.BYTE, PixelFormat.PXF_L8);
//		bathymetry = TensorImages.loadImage(_bathymetryMap, Primitive.BYTE, PixelFormat.PXF_L8);
	}
	public void setMaxima(double _maxDepth, double _maxAltitude) {
		maxDepth    = _maxDepth;
		maxAltitude = _maxAltitude;
	}

	public double getHeightRadian(double _lg_rad, double _lt_rad) {
		if(_lt_rad < -Math.PI/2d || _lt_rad > Math.PI/2d)
			throw new IllegalArgumentException();

		if(altitude != null) {
			int alt_width  = -1; //altitude.getWidth();
			int alt_height = -1; //altitude.getHeight();

			int pixel_x = (int) ( (_lg_rad + Math.PI)    / (2d * Math.PI) * alt_width );
			int pixel_y = (int) ( (_lt_rad + Math.PI/2d) / (Math.PI)      * alt_height );

			byte alt = 0; //altitude.getLuminance(pixel_y, pixel_x).byteValue();

			if(alt != 0)
				return (alt & 0xFF) / 255d * maxAltitude;
		}

		if(bathymetry != null) {
			int depth_width  = -1; //bathymetry.getWidth();
			int depth_height = -1; //bathymetry.getHeight();

			int pixel_x = (int) ( (_lg_rad + Math.PI)    / (2d * Math.PI) * depth_width );
			int pixel_y = (int) ( (_lt_rad + Math.PI/2d) / (Math.PI)      * depth_height );

			double depth = 0; //(bathymetry.getLuminance(pixel_y, pixel_x).byteValue() & 0xFF) * maxDepth / 255d;

			return depth;
		}

		return 0d;
	}
	public double getHeightDegree(double _lg_deg, double _lt_deg) {
		return getHeightRadian(Angles.reduceDegree( _lg_deg ) * Math.PI / 180d, Angles.reduceDegree( _lt_deg ) * Math.PI / 180d);
	}

	public static void main(String[] args) throws IOException {
		Path bathymetry = Paths.get("/media/sanke/Yang/res/VirtualEarth/HeightMaps/EarthBathymetryMap.png");
		Path altimetry  = Paths.get("/media/sanke/Yang/res/VirtualEarth/HeightMaps/EarthAltimetryMap.png");

		HeightMap hMap = new HeightMap();
		hMap.loadImages(altimetry, bathymetry);
		hMap.setMaxima(-10_994, 8_848);

		for(int i = -180; i < 180; ++i)
			for(int j = -90; j < 90; ++j)
				System.out.println("(" + i + ", " + j + ")\t-> " + hMap.getHeightDegree(i, j) + "\t" + hMap.getHeightRadian(i * Math.PI / 180d, j * Math.PI / 180d));
	}

}
