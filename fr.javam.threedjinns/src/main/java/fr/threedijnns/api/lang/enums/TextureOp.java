package fr.threedijnns.api.lang.enums;

public enum TextureOp {
    TXO_COLOR_FIRSTARG		(),					/// Sélection du premier paramètre de couleur
    TXO_COLOR_ADD			(),					/// Addition entre les deux paramètres de couleur
    TXO_COLOR_MODULATE		(),					/// Modulation entre les deux paramètres de couleur
    TXO_ALPHA_FIRSTARG		(),					/// Sélection du premier paramètre de transparence
    TXO_ALPHA_ADD			(),					/// Addition entre les deux paramètres de transparence
    TXO_ALPHA_MODULATE		();					/// Modulation entre les deux paramètres de transparence

	private TextureOp() {
		;
	}

};