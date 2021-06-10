package fr.threedijnns.api.lang.enums;

public enum BlendChannel {
    AlphaSource		(),					/// Transparence source
    BLEND_SRCALPHASAT	(),					/// Transparence source saturée
    BLEND_INVSRCALPHA	(),					/// Inverse de la transparence source
    BLEND_DESTALPHA		(),					/// Transparence de destination
    BLEND_INVDESTALPHA	(),					/// Inverse de la transparence de destination
    BLEND_CSTALPHA		(),					/// Transparence constante
    BLEND_INVCSTALPHA	(),					/// Inverse de la transparence constante
    BLEND_SRCCOLOR		(),					/// Couleur source
    BLEND_INVSRCCOLOR	(),					/// Inverse de la couleur source
    BLEND_DESTCOLOR		(),					/// Couleur de destination
    BLEND_INVDESTCOLOR	(),					/// Inverse de la couleur de destination
    BLEND_CSTCOLOR		(),					/// Couleur constante
    BLEND_INVCSTCOLOR	(),					/// Inverse de couleur constante
    One			(),					/// Un
    Zero			();					/// Zéro

	private BlendChannel() {
		;
	}

};
