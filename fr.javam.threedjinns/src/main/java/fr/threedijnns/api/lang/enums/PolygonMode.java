package fr.threedijnns.api.lang.enums;

public enum PolygonMode {
	OnlyVertex(true),
    OnlySkeleton(true),
    Realistic(true),
    // Extra Mode
    OnlyLand(false),
    OnlyBoundBox(false)
    ;

	boolean standard;

	private PolygonMode(boolean _std) {
		standard = _std;
	}
	
	public boolean isStandard() {
		return standard;
	}

}
