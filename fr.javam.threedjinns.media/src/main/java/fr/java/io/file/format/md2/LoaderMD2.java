package fr.java.io.file.format.md2;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.Vector3D;
import fr.java.maths.Points;
import fr.java.maths.algebra.Vectors;
import fr.java.utils.Bytes;
import fr.threedijnns.objects.space.shapes.meshes.GxMeshObject;

// http://tfc.duke.free.fr/coding/md2-specs-en.html
public class LoaderMD2 {

	private static final int MagicId    = ('2' << 24) + ('P' << 16) + ('D' << 8) + 'I'; /// Identifiant magique "IDP2" servant à valider les modèles
	private static final int MD2Version = 8; 											/// Numéro de version géré

	private static final Vector3D[] NormalTable = new Vector3D[] { Vectors.of(-0.525731f, 0.000000f, 0.850651f),
			Vectors.of(-0.442863f, 0.238856f, 0.864188f), Vectors.of(-0.295242f, 0.000000f, 0.955423f),
			Vectors.of(-0.309017f, 0.500000f, 0.809017f), Vectors.of(-0.162460f, 0.262866f, 0.951056f),
			Vectors.of(0.000000f, 0.000000f, 1.000000f), Vectors.of(0.000000f, 0.850651f, 0.525731f),
			Vectors.of(-0.147621f, 0.716567f, 0.681718f), Vectors.of(0.147621f, 0.716567f, 0.681718f),
			Vectors.of(0.000000f, 0.525731f, 0.850651f), Vectors.of(0.309017f, 0.500000f, 0.809017f),
			Vectors.of(0.525731f, 0.000000f, 0.850651f), Vectors.of(0.295242f, 0.000000f, 0.955423f),
			Vectors.of(0.442863f, 0.238856f, 0.864188f), Vectors.of(0.162460f, 0.262866f, 0.951056f),
			Vectors.of(-0.681718f, 0.147621f, 0.716567f), Vectors.of(-0.809017f, 0.309017f, 0.500000f),
			Vectors.of(-0.587785f, 0.425325f, 0.688191f), Vectors.of(-0.850651f, 0.525731f, 0.000000f),
			Vectors.of(-0.864188f, 0.442863f, 0.238856f), Vectors.of(-0.716567f, 0.681718f, 0.147621f),
			Vectors.of(-0.688191f, 0.587785f, 0.425325f), Vectors.of(-0.500000f, 0.809017f, 0.309017f),
			Vectors.of(-0.238856f, 0.864188f, 0.442863f), Vectors.of(-0.425325f, 0.688191f, 0.587785f),
			Vectors.of(-0.716567f, 0.681718f, -0.147621f), Vectors.of(-0.500000f, 0.809017f, -0.309017f),
			Vectors.of(-0.525731f, 0.850651f, 0.000000f), Vectors.of(0.000000f, 0.850651f, -0.525731f),
			Vectors.of(-0.238856f, 0.864188f, -0.442863f), Vectors.of(0.000000f, 0.955423f, -0.295242f),
			Vectors.of(-0.262866f, 0.951056f, -0.162460f), Vectors.of(0.000000f, 1.000000f, 0.000000f),
			Vectors.of(0.000000f, 0.955423f, 0.295242f), Vectors.of(-0.262866f, 0.951056f, 0.162460f),
			Vectors.of(0.238856f, 0.864188f, 0.442863f), Vectors.of(0.262866f, 0.951056f, 0.162460f),
			Vectors.of(0.500000f, 0.809017f, 0.309017f), Vectors.of(0.238856f, 0.864188f, -0.442863f),
			Vectors.of(0.262866f, 0.951056f, -0.162460f), Vectors.of(0.500000f, 0.809017f, -0.309017f),
			Vectors.of(0.850651f, 0.525731f, 0.000000f), Vectors.of(0.716567f, 0.681718f, 0.147621f),
			Vectors.of(0.716567f, 0.681718f, -0.147621f), Vectors.of(0.525731f, 0.850651f, 0.000000f),
			Vectors.of(0.425325f, 0.688191f, 0.587785f), Vectors.of(0.864188f, 0.442863f, 0.238856f),
			Vectors.of(0.688191f, 0.587785f, 0.425325f), Vectors.of(0.809017f, 0.309017f, 0.500000f),
			Vectors.of(0.681718f, 0.147621f, 0.716567f), Vectors.of(0.587785f, 0.425325f, 0.688191f),
			Vectors.of(0.955423f, 0.295242f, 0.000000f), Vectors.of(1.000000f, 0.000000f, 0.000000f),
			Vectors.of(0.951056f, 0.162460f, 0.262866f), Vectors.of(0.850651f, -0.525731f, 0.000000f),
			Vectors.of(0.955423f, -0.295242f, 0.000000f), Vectors.of(0.864188f, -0.442863f, 0.238856f),
			Vectors.of(0.951056f, -0.162460f, 0.262866f), Vectors.of(0.809017f, -0.309017f, 0.500000f),
			Vectors.of(0.681718f, -0.147621f, 0.716567f), Vectors.of(0.850651f, 0.000000f, 0.525731f),
			Vectors.of(0.864188f, 0.442863f, -0.238856f), Vectors.of(0.809017f, 0.309017f, -0.500000f),
			Vectors.of(0.951056f, 0.162460f, -0.262866f), Vectors.of(0.525731f, 0.000000f, -0.850651f),
			Vectors.of(0.681718f, 0.147621f, -0.716567f), Vectors.of(0.681718f, -0.147621f, -0.716567f),
			Vectors.of(0.850651f, 0.000000f, -0.525731f), Vectors.of(0.809017f, -0.309017f, -0.500000f),
			Vectors.of(0.864188f, -0.442863f, -0.238856f), Vectors.of(0.951056f, -0.162460f, -0.262866f),
			Vectors.of(0.147621f, 0.716567f, -0.681718f), Vectors.of(0.309017f, 0.500000f, -0.809017f),
			Vectors.of(0.425325f, 0.688191f, -0.587785f), Vectors.of(0.442863f, 0.238856f, -0.864188f),
			Vectors.of(0.587785f, 0.425325f, -0.688191f), Vectors.of(0.688191f, 0.587785f, -0.425325f),
			Vectors.of(-0.147621f, 0.716567f, -0.681718f), Vectors.of(-0.309017f, 0.500000f, -0.809017f),
			Vectors.of(0.000000f, 0.525731f, -0.850651f), Vectors.of(-0.525731f, 0.000000f, -0.850651f),
			Vectors.of(-0.442863f, 0.238856f, -0.864188f), Vectors.of(-0.295242f, 0.000000f, -0.955423f),
			Vectors.of(-0.162460f, 0.262866f, -0.951056f), Vectors.of(0.000000f, 0.000000f, -1.000000f),
			Vectors.of(0.295242f, 0.000000f, -0.955423f), Vectors.of(0.162460f, 0.262866f, -0.951056f),
			Vectors.of(-0.442863f, -0.238856f, -0.864188f), Vectors.of(-0.309017f, -0.500000f, -0.809017f),
			Vectors.of(-0.162460f, -0.262866f, -0.951056f), Vectors.of(0.000000f, -0.850651f, -0.525731f),
			Vectors.of(-0.147621f, -0.716567f, -0.681718f), Vectors.of(0.147621f, -0.716567f, -0.681718f),
			Vectors.of(0.000000f, -0.525731f, -0.850651f), Vectors.of(0.309017f, -0.500000f, -0.809017f),
			Vectors.of(0.442863f, -0.238856f, -0.864188f), Vectors.of(0.162460f, -0.262866f, -0.951056f),
			Vectors.of(0.238856f, -0.864188f, -0.442863f), Vectors.of(0.500000f, -0.809017f, -0.309017f),
			Vectors.of(0.425325f, -0.688191f, -0.587785f), Vectors.of(0.716567f, -0.681718f, -0.147621f),
			Vectors.of(0.688191f, -0.587785f, -0.425325f), Vectors.of(0.587785f, -0.425325f, -0.688191f),
			Vectors.of(0.000000f, -0.955423f, -0.295242f), Vectors.of(0.000000f, -1.000000f, 0.000000f),
			Vectors.of(0.262866f, -0.951056f, -0.162460f), Vectors.of(0.000000f, -0.850651f, 0.525731f),
			Vectors.of(0.000000f, -0.955423f, 0.295242f), Vectors.of(0.238856f, -0.864188f, 0.442863f),
			Vectors.of(0.262866f, -0.951056f, 0.162460f), Vectors.of(0.500000f, -0.809017f, 0.309017f),
			Vectors.of(0.716567f, -0.681718f, 0.147621f), Vectors.of(0.525731f, -0.850651f, 0.000000f),
			Vectors.of(-0.238856f, -0.864188f, -0.442863f), Vectors.of(-0.500000f, -0.809017f, -0.309017f),
			Vectors.of(-0.262866f, -0.951056f, -0.162460f), Vectors.of(-0.850651f, -0.525731f, 0.000000f),
			Vectors.of(-0.716567f, -0.681718f, -0.147621f), Vectors.of(-0.716567f, -0.681718f, 0.147621f),
			Vectors.of(-0.525731f, -0.850651f, 0.000000f), Vectors.of(-0.500000f, -0.809017f, 0.309017f),
			Vectors.of(-0.238856f, -0.864188f, 0.442863f), Vectors.of(-0.262866f, -0.951056f, 0.162460f),
			Vectors.of(-0.864188f, -0.442863f, 0.238856f), Vectors.of(-0.809017f, -0.309017f, 0.500000f),
			Vectors.of(-0.688191f, -0.587785f, 0.425325f), Vectors.of(-0.681718f, -0.147621f, 0.716567f),
			Vectors.of(-0.442863f, -0.238856f, 0.864188f), Vectors.of(-0.587785f, -0.425325f, 0.688191f),
			Vectors.of(-0.309017f, -0.500000f, 0.809017f), Vectors.of(-0.147621f, -0.716567f, 0.681718f),
			Vectors.of(-0.425325f, -0.688191f, 0.587785f), Vectors.of(-0.162460f, -0.262866f, 0.951056f),
			Vectors.of(0.442863f, -0.238856f, 0.864188f), Vectors.of(0.162460f, -0.262866f, 0.951056f),
			Vectors.of(0.309017f, -0.500000f, 0.809017f), Vectors.of(0.147621f, -0.716567f, 0.681718f),
			Vectors.of(0.000000f, -0.525731f, 0.850651f), Vectors.of(0.425325f, -0.688191f, 0.587785f),
			Vectors.of(0.587785f, -0.425325f, 0.688191f), Vectors.of(0.688191f, -0.587785f, 0.425325f),
			Vectors.of(-0.955423f, 0.295242f, 0.000000f), Vectors.of(-0.951056f, 0.162460f, 0.262866f),
			Vectors.of(-1.000000f, 0.000000f, 0.000000f), Vectors.of(-0.850651f, 0.000000f, 0.525731f),
			Vectors.of(-0.955423f, -0.295242f, 0.000000f), Vectors.of(-0.951056f, -0.162460f, 0.262866f),
			Vectors.of(-0.864188f, 0.442863f, -0.238856f), Vectors.of(-0.951056f, 0.162460f, -0.262866f),
			Vectors.of(-0.809017f, 0.309017f, -0.500000f), Vectors.of(-0.864188f, -0.442863f, -0.238856f),
			Vectors.of(-0.951056f, -0.162460f, -0.262866f), Vectors.of(-0.809017f, -0.309017f, -0.500000f),
			Vectors.of(-0.681718f, 0.147621f, -0.716567f), Vectors.of(-0.681718f, -0.147621f, -0.716567f),
			Vectors.of(-0.850651f, 0.000000f, -0.525731f), Vectors.of(-0.688191f, 0.587785f, -0.425325f),
			Vectors.of(-0.587785f, 0.425325f, -0.688191f), Vectors.of(-0.425325f, 0.688191f, -0.587785f),
			Vectors.of(-0.425325f, -0.688191f, -0.587785f), Vectors.of(-0.587785f, -0.425325f, -0.688191f),
			Vectors.of(-0.688191f, -0.587785f, -0.425325f) };

	private static class MD2Header {
		int Ident; 				/// Numéro magique "IDP2"
		int Version; 			/// Version du format
		int TexWidth; 			/// Largeur de la texture
		int TexHeight; 			/// Hauteur de la texture
		int FrameSize; 			/// Taille d'une frame en octets
		int NbTextures; 		/// Nombre de textures
		int NbVertices; 		/// Nombre de vertices par frame
		int NbTexCoords; 		/// Nombre de coordonnées de texture
		int NbTriangles; 		/// Nombre de triangles
		int NbGLCommands; 		/// Nombre de commandes OpenGL
		int NbFrames;			/// Nombre de frames
		int OffsetTextures; 	/// Offset données textures
		int OffsetTexCoords;	/// Offset données coordonnées de texture
		int OffsetTriangles; 	/// Offset données triangles
		int OffsetFrames; 		/// Offset données frames
		int OffsetGLCommands; 	/// Offset données commandes OpenGL
		int OffsetEnd; 			/// Offset fin de fichier
	};

	private static class MD2Vertex {
		byte x, y, z;
		byte n;
	};
	private static class MD2TexCoord {
		short u, v;
	};
	private static class MD2Triangle {
		short vertices[];
		short texCoords[];
		
		MD2Triangle() {
			vertices = new short[3];
			texCoords = new short[3];
		}
	};
	private static class MD2Frame {
		Point3D   scale; 		/// Facteur d'échelle
		Point3D   translate; 	/// Position
		String    name; 		/// Nom de la frame: 16 bytes
		MD2Vertex vertices[]; 	/// Liste des sommets
		
		MD2Frame() {
			scale     = Points.zero3();
			translate = Points.zero3();
		}
	};

	static class MD2Shape {
		Point3D  vertices[];
		Vector3D normals[];
		Point2D  texCoords[];
		int      colors[];
		int      indices[];
	};

	public static GxMeshObject load(String _filename) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(new File(_filename), "r");

		MD2Header header = readHeader(raf);

		if (header.Ident != MagicId)
			throw new InvalidObjectException("Le fichier n'est pas un modèle MD2 valide");

		if (header.Version != MD2Version)
			throw new InvalidObjectException("Le modèle possède une version différente de celle requise (v.8)");

		String[]      textureNames = readTextureName(raf, header);
		MD2TexCoord[] texCoords    = readTextureCoords(raf, header);
		MD2Triangle[] triangles    = readTriangles(raf, header);
		MD2Frame[]    frames       = readFrames(raf, header);

		MD2Shape shape = restoreShape(frames[0], header, triangles, texCoords);
		
		return shape2mesh(shape);
	}

	protected static GxMeshObject shape2mesh(MD2Shape _shape) {
		GxMeshObject meshe = new GxMeshObject();

		for(int i = 0; i < _shape.vertices.length; ++i) {
			meshe.vertices().add(_shape.vertices[i]);
			meshe.normals().add(_shape.normals[i]);
			meshe.texCoords().add(_shape.texCoords[i]);
			meshe.colors().add(_shape.colors[i]);
		}
		
		GxMeshObject.Mesh mesh = new GxMeshObject.Mesh();
		
		for(int i = 0; i < _shape.indices.length; i += 3) {
			GxMeshObject.Face face = new GxMeshObject.Face();

			face.v[0] = face.vn[0] = face.vt[0] = face.vc[0] = _shape.indices[i];
			face.v[1] = face.vn[1] = face.vt[1] = face.vc[1] = _shape.indices[i + 1];
			face.v[2] = face.vn[2] = face.vt[2] = face.vc[2] = _shape.indices[i + 2];
			
			mesh.faces().add(face);
		}
		
		meshe.meshes().add(mesh);
		
		return meshe;
	}

	protected static MD2Header readHeader(RandomAccessFile content) throws IOException {
		content.seek(0);
		MD2Header header = new MD2Header();
		header.Ident = Bytes.swap(content.readInt());			// must be 844121161
		header.Version = Bytes.swap(content.readInt());
		header.TexWidth = Bytes.swap(content.readInt());
		header.TexHeight = Bytes.swap(content.readInt());
		header.FrameSize = Bytes.swap(content.readInt());
		header.NbTextures = Bytes.swap(content.readInt());
		header.NbVertices = Bytes.swap(content.readInt());
		header.NbTexCoords = Bytes.swap(content.readInt());
		header.NbTriangles = Bytes.swap(content.readInt());
		header.NbGLCommands = Bytes.swap(content.readInt());
		header.NbFrames = Bytes.swap(content.readInt());
		header.OffsetTextures = Bytes.swap(content.readInt());
		header.OffsetTexCoords = Bytes.swap(content.readInt());
		header.OffsetTriangles = Bytes.swap(content.readInt());
		header.OffsetFrames = Bytes.swap(content.readInt());
		header.OffsetGLCommands = Bytes.swap(content.readInt());
		header.OffsetEnd = Bytes.swap(content.readInt());

		return header;
	}
	protected static String[] readTextureName(RandomAccessFile _raf, MD2Header _header) throws IOException {
		String[] TextureNames  = new String[_header.NbTextures];
		byte[]   textureBuffer = new byte[64];

		_raf.seek(_header.OffsetTextures);
		for (int i = 0; i < _header.NbTextures; ++i) {
			_raf.read(textureBuffer);
			TextureNames[i] = new String(textureBuffer, StandardCharsets.UTF_8);
		}
		
		return TextureNames;
	}
	protected static MD2TexCoord[] readTextureCoords(RandomAccessFile _raf, MD2Header _header) throws IOException {
		MD2TexCoord[] TextureCoords = new MD2TexCoord[_header.NbTexCoords];

		_raf.seek(_header.OffsetTexCoords);
		for (int i = 0; i < _header.NbTexCoords; ++i) {
			TextureCoords[i] = new MD2TexCoord();
			TextureCoords[i].u = Bytes.swap(_raf.readShort());
			TextureCoords[i].v = Bytes.swap(_raf.readShort());
		}
		
		return TextureCoords;
	}
	protected static MD2Triangle[] readTriangles(RandomAccessFile _raf, MD2Header _header) throws IOException {
		MD2Triangle[] Triangles = new MD2Triangle[_header.NbTriangles];

		_raf.seek(_header.OffsetTriangles);
		for (int i = 0; i < _header.NbTriangles; ++i) {
			Triangles[i] = new MD2Triangle();
			Triangles[i].vertices[0]  = Bytes.swap(_raf.readShort());
			Triangles[i].vertices[1]  = Bytes.swap(_raf.readShort());
			Triangles[i].vertices[2]  = Bytes.swap(_raf.readShort());
			Triangles[i].texCoords[0] = Bytes.swap(_raf.readShort());
			Triangles[i].texCoords[1] = Bytes.swap(_raf.readShort());
			Triangles[i].texCoords[2] = Bytes.swap(_raf.readShort());
		}
		
		return Triangles;
	}
	protected static MD2Frame[] readFrames(RandomAccessFile _raf, MD2Header _header) throws IOException {
		MD2Frame[] Frames    = new MD2Frame[_header.NbFrames];
		byte[]     frameName = new byte[16];

		_raf.seek(_header.OffsetFrames);

		for(int i = 0; i < _header.NbFrames; ++i) {
			Frames[i] = new MD2Frame();
/*
			Frames[i].scale.setX( Bytes.swap(_raf.readFloat()) );
			Frames[i].scale.setY( Bytes.swap(_raf.readFloat()) );
			Frames[i].scale.setZ( Bytes.swap(_raf.readFloat()) );
			Frames[i].translate.setX( Bytes.swap(_raf.readFloat()) );
			Frames[i].translate.setY( Bytes.swap(_raf.readFloat()) );
			Frames[i].translate.setZ( Bytes.swap(_raf.readFloat()) );
*/
			Frames[i].scale     = Points.of( Bytes.swap(_raf.readFloat()), Bytes.swap(_raf.readFloat()), Bytes.swap(_raf.readFloat()) );
			Frames[i].translate = Points.of( Bytes.swap(_raf.readFloat()), Bytes.swap(_raf.readFloat()), Bytes.swap(_raf.readFloat()) );
			_raf.read(frameName);
			Frames[i].name = new String(frameName, StandardCharsets.UTF_8);
	
			Frames[i].vertices = new MD2Vertex[_header.NbVertices];
			for (int j = 0; j < _header.NbVertices; ++j) {
				Frames[i].vertices[j] = new MD2Vertex();
				Frames[i].vertices[j].x = _raf.readByte();
				Frames[i].vertices[j].y = _raf.readByte();
				Frames[i].vertices[j].z = _raf.readByte();
				Frames[i].vertices[j].n = _raf.readByte();
				
			}
		}
		
		return Frames;
	}

	protected static MD2Shape restoreShape(MD2Frame _frame, MD2Header _header, MD2Triangle[] _triangles, MD2TexCoord[] _texCoords) {
		MD2Shape shape = new MD2Shape();

		shape.vertices  = new Point3D[3 * _header.NbTriangles];
		shape.normals   = new Vector3D[3 * _header.NbTriangles];
		shape.texCoords = new Point2D[3 * _header.NbTriangles];
		shape.colors    = new int[3 * _header.NbTriangles];
		shape.indices   = new int[3 * _header.NbTriangles];

		for (int i = 0; i < _header.NbTriangles; ++i) {
			int offset = 3 * i;
			for (int j = 0; j < 3; ++j) {
				MD2Vertex   vertex   = _frame.vertices[_triangles[i].vertices[j]];
				MD2TexCoord texCoord = _texCoords[_triangles[i].texCoords[j]];

				Point3D v = Points.of(  vertex.x * _frame.scale.getX() + _frame.translate.getX() ,
										vertex.y * _frame.scale.getY() + _frame.translate.getY() ,
										vertex.z * _frame.scale.getZ() + _frame.translate.getZ() );
				Point2D m = Points.of(  texCoord.u / (float) _header.TexWidth ,
										texCoord.v / (float) _header.TexHeight );

				shape.vertices  [j + offset] = v;
				shape.normals   [j + offset] = NormalTable[ vertex.n&0xff ];
				shape.texCoords [j + offset] = m;
				shape.colors    [j + offset] = 0;

				shape.indices   [j + offset] = offset + j;
			}
		}
		
		return shape;
	}

}
