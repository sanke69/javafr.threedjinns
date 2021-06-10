package fr.threedijnns.api.lang.enums;

public enum StencilOperator {
    STENCIL_KEEP			(),
    STENCIL_ZERO			(),
    STENCIL_REPLACE			(),
    STENCIL_INCR			(),
    STENCIL_INCR_WRAP		(),
    STENCIL_DECR			(),
    STENCIL_DECR_WRAP		(),
    STENCIL_INVERT			();

	private StencilOperator() {
		;
	}

};