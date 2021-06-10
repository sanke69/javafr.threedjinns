package fr.threedijnns.api.lang.enums;

@Deprecated
public enum Capability {
	CAP_HW_MIPMAPPING,                      /// Mipmapping automatique en hardware
    CAP_DXT_COMPRESSION,                    /// Compression de texture DXTC
    CAP_TEX_NON_POWER_2,                    /// Dimensions de textures pas nécessairement en puissances de 2
    CAP_TEX_CUBE_MAP,                       ///
    CAP_TEX_EDGE_CLAMP,                     /// http://www.gamedev.net/community/forums/topic.asp?topic_id=70253
    CAP_PBO,                                ///
    CAP_ENUM_LENGTH
}
