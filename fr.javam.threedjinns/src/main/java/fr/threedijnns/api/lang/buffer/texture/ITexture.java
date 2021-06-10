package fr.threedijnns.api.lang.buffer.texture;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import fr.java.math.algebra.tensor.ByteTensor;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public interface ITexture {

	public void enable(int _unit);
	public void disable(int _unit);

	public void update(ByteBuffer    _bytes, int _width, int _height, int _stride, SimpleRectangle2D _in, SimpleRectangle2D _out);
	public void update(ByteTensor    _img, SimpleRectangle2D _in, SimpleRectangle2D _out);
	public void update(BufferedImage _img, SimpleRectangle2D _in, SimpleRectangle2D _out);

	public void bind(StreamHandler2D _handler);
	
	// MipMaps
/*
	public void updateLevel(int _mipMapLvl, ByteBuffer    _bytes, int _width, int _height, int _stride, Rectangle2D _in, Rectangle2D _out);
	public void updateLevel(int _mipMapLvl, BufferedImage   _img, Rectangle2D _in, Rectangle2D _out);
	public void updateLevel(int _mipMapLvl, ByteImage       _img, Rectangle2D _in, Rectangle2D _out);
	
	public void bindLevel(int _mipMapLvl, StreamHandler2D _handler);
*/
}

