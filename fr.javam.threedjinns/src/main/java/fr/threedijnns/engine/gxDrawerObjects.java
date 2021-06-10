package fr.threedijnns.engine;

import fr.threedijnns.api.lang.buffer.BufferBase;
import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.BufferNature;
import fr.threedijnns.api.lang.enums.PrimitiveType;

public interface gxDrawerObjects {

	public void		enable						(BufferNature _type);

	public void 	setDeclaration				(IDeclaration _declaration);

	public void 	setVB						(int _stream, BufferBase _buffer, long _stride, long _min, long _max);
	public void 	setIB						(BufferBase _buffer, long _stride);

	public void 	drawPrimitives				(PrimitiveType _type, long _offset, long _count);
	public void 	drawIndexedPrimitives		(PrimitiveType _type, long _offset, long _count);

}
