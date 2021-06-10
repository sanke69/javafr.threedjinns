package fr.threedijnns.api.lang.enums;

public enum BufferNature {
	VertexBuffer 		(),
	NormalBuffer 		(),	// same as VertexBuffer for storage, but different option for engine...
	ColorBuffer 		(),	// same as VertexBuffer for storage, but different option for engine...
	MapBuffer 			(),	// same as VertexBuffer for storage, but different option for engine...

	IndexBuffer 		(),
	
	Texture				(),

	RenderBuffer	 	(),
	FrameBuffer	 		(),
	PixelBufferPack 	(),
	PixelBufferUnpack 	(),
	
	CustomBuffer		();	// add extra parameter to buffer that use this type to be able to use it !!!

	private BufferNature() {
		;
	}

}
