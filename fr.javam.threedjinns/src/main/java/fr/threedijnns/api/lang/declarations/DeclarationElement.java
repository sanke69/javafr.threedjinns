package fr.threedijnns.api.lang.declarations;

import fr.threedijnns.api.lang.enums.ElementType;
import fr.threedijnns.api.lang.enums.ElementUsage;

public class DeclarationElement {
	public int   			Stream;    /// Numéro du stream
    public ElementUsage  	Usage;     /// Rôle du composant
    public ElementType   	DataType;  /// Type du composant

    public DeclarationElement(int i, ElementUsage eltUsagePosition, ElementType eltTypeFloat3) {
    	Stream = i;
    	Usage = eltUsagePosition;
    	DataType = eltTypeFloat3;
	}
    
}

