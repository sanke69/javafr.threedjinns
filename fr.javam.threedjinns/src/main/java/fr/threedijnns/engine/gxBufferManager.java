package fr.threedijnns.engine;

import java.nio.Buffer;

import fr.java.beans.reflect.utils.Primitives;
import fr.java.lang.enums.PixelFormat;
import fr.java.math.geometry.plane.Dimension2D;
import fr.java.maths.geometry.plane.shapes.SimpleRectangle2D;
import fr.threedijnns.api.lang.buffer.BufferBase;
import fr.threedijnns.api.lang.buffer.texture.TextureBase;
import fr.threedijnns.api.lang.buffer.texture.TextureStream;
import fr.threedijnns.api.lang.declarations.DeclarationElement;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.BufferFlag;
import fr.threedijnns.api.lang.enums.LockFlag;
import fr.threedijnns.api.lang.stream.handlers.StreamHandler2D;

public interface gxBufferManager {

	public IDeclaration 	createDecl					(DeclarationElement[] _elts);
	
	// VERTEX BUFFER
	public BufferBase 		createVBO					(Primitives _type, long _size, BufferFlag... _flags);
	public void 			deleteVBO					(BufferBase _buffer);

	// INDEX BUFFER
	public BufferBase 		createIBO					(Primitives _type, long _size, BufferFlag... _flags);
	public void 			deleteIBO					(BufferBase _buffer);

	// TEXTURE BUFFER
	public TextureBase 		createTexture2D				(Dimension2D _size, PixelFormat _format, LockFlag _flags);
	public void 			deleteTexture2D				(TextureBase _base);
	public TextureBase 		createTextureCubeMap		(Dimension2D _size, PixelFormat _format, LockFlag _flags);

	public TextureStream 	createTextureStream2D		(TextureBase _texture, StreamHandler2D handler, int nbSlots);

	// PIXEL BUFFER
	public BufferBase 		createPBO					(Primitives _type, long _size, BufferFlag... _flags);
	public void 			setPBO						(int _stream, BufferBase _buffer, long _stride);
	public void 			setPBO						(int _stream, BufferBase _buffer, long _stride, SimpleRectangle2D _in, SimpleRectangle2D _out);
	public void 			deletePBO					(BufferBase _buffer);

	// FRAME BUFFER
	public BufferBase 		createFBO					(Primitives _type, long _size, BufferFlag... _flags);
	public void 			setFBO						(int _stream, BufferBase _buffer, long _stride);
	public void 			setFBO						(int _stream, BufferBase _buffer, long _stride, SimpleRectangle2D _in, SimpleRectangle2D _out);
	public void 			deleteFBO					(BufferBase _buffer);

	public Buffer 			readPBO						(int _w, int _h);

}
