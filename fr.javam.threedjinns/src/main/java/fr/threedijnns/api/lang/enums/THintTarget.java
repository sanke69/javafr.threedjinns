package fr.threedijnns.api.lang.enums;

public enum THintTarget {
	HINT_FOG						(), 
	HINT_GENERATE_MIPMAP			(), 
	HINT_LINE_SMOOTH				(), 
	HINT_PERSPECTIVE_CORRECTION		(), 
	HINT_POINT_SMOOTH				(), 
	HINT_POLYGON_SMOOTH				(), 
	HINT_TEXTURE_COMPRESSION		();
	
	public int GL() {
		return 0;
	}
	
};