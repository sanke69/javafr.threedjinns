package fr.java.io.file.format.ply;

public class LoaderPLY {

/**********************************************
 constantes symboliques a activer ou desactiver 
***********************************************/

/* codes definis pour analyse du retour */
public static final int PLY_XYZ               = 0x01;  /* coordonnées des sommets */
public static final int PLY_NORMAL            = 0x02;  /* normale réelle */
public static final int PLY_TOLER             = 0x04;  /* tolérance */
public static final int PLY_CONF              = 0x08;  /* confiance */
public static final int PLY_COLOR             = 0x10;  /* couleur */
public static final int PLY_NOTCHECKED        = 0x20;  /* le modele a subit le test de coherence */
public static final int PLY_FULLCOHERENCE     = 0x40;  /* le modele est completement coherent */

/* constantes symboliques */
public static final int PLYFLD_COORD          = 0;
public static final int PLYFLD_CONF           = 1;
public static final int PLYFLD_COLOR          = 2;
public static final int PLYFLD_UV             = 3;
public static final int PLYFLD_NORM           = 4;
public static final int PLYFLD_INTENS         = 5;
public static final int NFIELDS               = 6;	// <<==

public static final int PLYFMT_OFF            = 0;
public static final int PLYFMT_UCHAR          = 1;
public static final int PLYFMT_USHORT         = 2; 
public static final int PLYFMT_INT            = 3;
public static final int PLYFMT_FLOAT          = 4;
public static final int PLYFMT_DOUBLE         = 5;


public static final int PLY_SKIP              = 1;

public static final int PLYERR_INVALIDINDICES = 0x01;
public static final int PLYERR_UNCONNECTEDPTS = 0x02;
public static final int PLYERR_UNSHAREDGES    = 0x04;
public static final int PLYERR_REVFACETS      = 0x08;
public static final int PLYERR_INVNORMALS     = 0x10;
public static final int PLYERR_BADNORMALS     = 0x20;
public static final int PLYERR_WARNING        = 0x40;
public static final int PLYERR_FATAL          = 0x80;

public static final int PLY_FAILED  = 0;
public static final int PLY_SUCCESS = 1;

public static final int PLY_LITTLE  = 1;
public static final int PLY_BIG     = 0;
public static final int PLY_UNDEF   = 2;

public static final int PLY_BINARY  = 0;
public static final int PLY_ASCII   = 1;

}
