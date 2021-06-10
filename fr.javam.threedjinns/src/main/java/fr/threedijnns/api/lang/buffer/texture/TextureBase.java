package fr.threedijnns.api.lang.buffer.texture;

import fr.java.lang.enums.PixelFormat;
import fr.threedijnns.api.lang.buffer.BufferBase;
import fr.threedijnns.api.lang.enums.FrameType;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public interface TextureBase extends BufferBase {

	public void			enable(int _unit);
	public void			disable(int _unit);

	public default void	bind(StreamHandler _handler) {}
	public void			bind(StreamHandler2D _handler);

	public default void destroy() {};

	public default long	getLength() 
	{ return getWidth() * getHeight() * getPixelFormat().bytesPerPixel(); }
//	{ return getStride() * getHeight(); }
	public long			getWidth();
	public long			getHeight();
	public PixelFormat	getPixelFormat();
	public default long	getStride()
	{ return getWidth() * getPixelFormat().bytesPerPixel(); }

	public void			setKeyColor(Integer _key);
	public Integer		getKeyColor();

	public boolean		hasMipmaps();
	public boolean		autoMipmaps();
	public int			getMipmapsLevels();

	public void			setActiveFrame(FrameType _frame);

}
