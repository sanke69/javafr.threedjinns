package fr.threedijnns.api.lang.enums;

// TODO:: Check GL TEXTURE 0 ... N
public enum MatrixType {
    MAT_MODELVIEW		(),                       /// Matrice de vue
    MAT_PROJECTION		(),                      /// Matrice de projection
    MAT_TEXTURE_0		(),                          /// Matrice de texture 0
    MAT_TEXTURE_1		(),                          /// Matrice de texture 1
    MAT_TEXTURE_2		(),                          /// Matrice de texture 2
    MAT_TEXTURE_3  		();                          /// Matrice de texture 3

    int glEnum;
    int id;

	private MatrixType() {
		id = 0;
	}

};