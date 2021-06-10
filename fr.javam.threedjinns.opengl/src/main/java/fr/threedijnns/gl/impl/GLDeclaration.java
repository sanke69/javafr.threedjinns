package fr.threedijnns.gl.impl;

import java.util.ArrayList;
import java.util.List;

import fr.threedijnns.api.lang.declarations.IDeclaration;
import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;

public class GLDeclaration implements IDeclaration {
	class TElement {
		public ElementUsage	Usage;
		public ElementType	Type;
		public int			Offset;
	};
	
	class TElementArray extends ArrayList<TElement> {};
	class TElementMatrix extends ArrayList<TElementArray> {};

	public GLDeclaration() {
		m_Elements = new TElementMatrix();
	}

	public void addElement(int _stream, TElement _element) {
		if(m_Elements.size() <= _stream)
			do {
				m_Elements.add(new TElementArray());
			} while(m_Elements.size() < _stream);
		
		m_Elements.set(_stream, new TElementArray());
		m_Elements.get(_stream).add(_element);
	}

	public TElementArray getStreamElements(int _stream) {
		return m_Elements.get(_stream);
	}

	public List<TElement> getElements() {
		List<TElement> result = new ArrayList<TElement>();
		
		for(TElementArray array : m_Elements)
			for(TElement elt : array)
				result.add(elt);
		
		return result;
	}
	
	private TElementMatrix m_Elements;
	
	
}
