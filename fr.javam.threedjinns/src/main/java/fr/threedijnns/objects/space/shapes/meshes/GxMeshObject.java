package fr.threedijnns.objects.space.shapes.meshes;

import java.util.ArrayList;
import java.util.List;

import fr.java.math.algebra.vector.generic.Vector2D;
import fr.java.math.algebra.vector.generic.Vector3D;
import fr.java.math.geometry.plane.Point2D;
import fr.java.math.geometry.space.Point3D;
import fr.java.math.geometry.space.shapes.Cube3D;
import fr.threedijnns.gx;
import fr.threedijnns.api.attributes.Material;
import fr.threedijnns.api.interfaces.nodes.GxRenderable;
import fr.threedijnns.api.lang.enums.PrimitiveType;
import fr.threedijnns.objects.base.GxObject3DBase;

public /*abstract*/ class GxMeshObject extends GxObject3DBase implements GxRenderable {

	public static class Vertex {
		public Vector3D	p;
		public Vector3D	n;
		public Vector2D	t;
		public int		c;
	}
	public static class Face {
		PrimitiveType type;
		public int v[], vn[], vt[], vc[];
	
		public Face() {
			super();
			type = PrimitiveType.TriangleList;
			v  = new int[3];
			vn = new int[3];
			vt = new int[3];
			vc = new int[3];
		}
		public Face(PrimitiveType _type) {
			super();
			type = _type;
			switch(type) {
			case LineList:			v  = new int[2];
									vn = new int[2];
									vt = new int[2];
									vc = new int[2];
									break;
			case TriangleList:		v  = new int[3];
									vn = new int[3];
									vt = new int[3];
									vc = new int[3];
									break;
			case QuadList:			v  = new int[4];
									vn = new int[4];
									vt = new int[4];
									vc = new int[4];
									break;
			case PointList:
			case LineLoop:
			case LineStrip:
			case TriangleFan:
			case TriangleStrip:
			case QuadStrip:
			case Polygon:
			default:				break;
			}
		}
	}
	public static class Mesh {
		public String				name;
		public List<Face> 			faces;

		public Material				material;
//		public OrientedBoundingBox	boundBox;
		public Cube3D				boundBox;

		public Mesh() {
			super();
			faces = new ArrayList<Face>();
		}

		public List<Face> faces() {
			return faces;	
		}
		public Face faces(int _i) {
			return faces.get(_i);	
		}

	}

	protected List<Point3D>		vertices;
	protected List<Vector3D>	normals;
	protected List<Point2D>		texCoords;
	protected List<Integer>		colors;

	protected List<Face>		faces;

	protected List<Mesh>		meshes;

	public GxMeshObject() {
		super();
		vertices  = new ArrayList<Point3D>();
		normals   = new ArrayList<Vector3D>();
		texCoords = new ArrayList<Point2D>();
		colors    = new ArrayList<Integer>();
		faces     = new ArrayList<Face>();
		meshes    = new ArrayList<Mesh>();
	}

	public List<Point3D> vertices() {
		return vertices;	
	}
	public Point3D vertex(int _i) {
		return vertices.get(_i);	
	}
	
	public List<Vector3D> normals() {
		return normals;	
	}
	public Vector3D normal(int _i) {
		return normals.get(_i);	
	}
	
	public List<Point2D> texCoords() {
		return texCoords;	
	}
	public Point2D texCoord(int _i) {
		return texCoords.get(_i);
	}
	
	public List<Integer> colors() {
		return colors;	
	}
	public Integer color(int _i) {
		return colors.get(_i);
	}

	public List<Face> faces() {
		return faces;	
	}
	public Face face(int _i) {
		return faces.get(_i);
	}
	
	public List<Mesh> meshes() {
		return meshes;	
	}
	public Mesh mesh(int _i) {
		return meshes.get(_i);
	}

	public void process() {
		;
	}
	public void render() {
		Point3D  p;
		Vector3D v;

		if(material != null)
			material.enable();

		gx.begin(PrimitiveType.TriangleList);
		for(int i = 0; i < vertices().size(); ++i) {
			gx.vertex(vertex(i));
		}
		gx.end();

		if(material != null)
			material.disable();
	}

	public void renderOLD() {
		Point3D  p;
		Vector3D v;

		if(material != null)
			material.enable();

		for(int i = 0; i < meshes().size(); ++i) {
			for(int j = 0; j < mesh(i).faces.size(); ++j) {
				gx.begin(PrimitiveType.TriangleList);
				if(normals().size() != 0) {
					v = normal(mesh(i).faces.get(j).vn[0]);
// TODO					v = Locate3D.local2world(v, land());
//					p = get_world_coordinate(p, land(), true);
					gx.normal(v);
				}

				p = vertex(mesh(i).faces.get(j).v[0]).times(getScale());
				// TODO				p = Locate3D.local2world(p, land());
//				p = get_world_coordinate(p, land(), false);
				gx.vertex(p);

				if(normals().size() != 0) {
					v = normal(mesh(i).faces.get(j).vn[1]);
					// TODO					v = Locate3D.local2world(v, land());
//					p = get_world_coordinate(p, land(), true);
					gx.normal(v);
				}

				p = vertex(mesh(i).faces.get(j).v[1]).times(getScale());
				// TODO				p = Locate3D.local2world(p, land());
//				p = get_world_coordinate(p, land(), false);
				gx.vertex(p);

				if(normals().size() != 0) {
					v = normal(mesh(i).faces.get(j).vn[2]);
					// TODO					v = Locate3D.local2world(v, land());
//					p = get_world_coordinate(p, land(), true);
					gx.normal(v);
				}

				p = vertex(mesh(i).faces.get(j).v[2]).times(getScale());
				// TODO				p = Locate3D.local2world(p, land());
//				p = get_world_coordinate(p, land(), false);
				gx.vertex(p);
				gx.end();
			}
		}

		if(material != null)
			material.disable();
	}

}
