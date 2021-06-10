package fr.java.io.file.format.obj;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import fr.java.maths.algebra.vectors.DoubleVector2D;
import fr.java.maths.algebra.vectors.DoubleVector3D;
import fr.java.maths.geometry.Space;
import fr.threedijnns.objects.space.shapes.meshes.GxMeshObject;

public class LoaderOBJ {

	public static GxMeshObject read(Path _file) {
		try {
			return loadASCII(new FileInputStream(_file.toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static GxMeshObject read(String filename) {
		return loadASCII(LoaderOBJ.class.getResourceAsStream(filename));
	}

	public static GxMeshObject loadASCII(InputStream content) {
		GxMeshObject obj = new GxMeshObject();
		
		GxMeshObject.Mesh mesh = null;
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(content))) {
		    String line = br.readLine();
		    while(line != null) {
		    	if(line.length() > 1) {
			    	boolean isComment = line.charAt(0) == '#';
			    	
			    	if(!isComment && line.length() > 1) {
			    		line = line.replaceAll("  ", " ");

				        int    typeSep = line.indexOf(' ');
				        String type    = line.substring(0, typeSep);
				        
				        switch(type) {
				        case "o"  : if(mesh != null) {
				        				finalizeMesh(obj, mesh);
				        				obj.meshes().add(mesh);
				        			}
				        			mesh = new GxMeshObject.Mesh();
				        			break;
				        case "g"  : System.out.println("NEW GROUP"); break;
				        case "v"  : {
				        			String   vcoords = line.substring(typeSep+1).replaceAll("  ", " ");
				        			String[] coords  = vcoords.split(" ");
				        			
				        			if(coords.length != 3)
				        				System.err.println("EPIC FAILED >> " + coords.length);

				        			obj.vertices().add( new DoubleVector3D(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), Float.parseFloat(coords[2])) );
				        			}
				        			break;
				        case "vn" : {
		        					String   vcoords = line.substring(typeSep+1).replaceAll("  ", " ");
				        			String[] coords  = vcoords.split(" ");
				        			
				        			if(coords.length != 3)
				        				System.err.println("EPIC FAILED >> " + coords.length);
			
				        			obj.normals().add( new DoubleVector3D(Float.parseFloat(coords[0]), Float.parseFloat(coords[1]), Float.parseFloat(coords[2])) );
	        						}
				        			break;
				        case "vt" : {
				        			String   vcoords = line.substring(typeSep+1).replaceAll("  ", " ");
				        			String[] coords  = vcoords.split(" ");
				        			
				        			if(coords.length != 2)
				        				System.err.println("EPIC FAILED >> " + coords.length);
			
				        			obj.texCoords().add( new DoubleVector2D(Float.parseFloat(coords[0]), Float.parseFloat(coords[1])) );
							        }
							        break;
				        case "f"  : {
				        			String   vcoords = line.substring(typeSep+1).replaceAll("  ", " ");
				        			String[] coords  = vcoords.split(" ");
				        			
				        			if(coords.length != 3)
				        				System.err.println("EPIC FAILED >> " + coords.length);

				        			GxMeshObject.Face face = new GxMeshObject.Face();

				        			if(vcoords.contains("/")) {
					        			String[] f1 = coords[0].split("/");
					        			String[] f2 = coords[1].split("/");
					        			String[] f3 = coords[2].split("/");
	
					        			face.v[0] = Integer.parseInt(f1[0]) - 1;
					        			face.vt[0] = Integer.parseInt(f1[1]) - 1;
					        			face.vn[0] = Integer.parseInt(f1[2]) - 1;
	
					        			face.v[1] = Integer.parseInt(f2[0]) - 1;
					        			face.vt[1] = Integer.parseInt(f2[1]) - 1;
					        			face.vn[1] = Integer.parseInt(f2[2]) - 1;
	
					        			face.v[2] = Integer.parseInt(f3[0]) - 1;
					        			face.vt[2] = Integer.parseInt(f3[1]) - 1;
					        			face.vn[2] = Integer.parseInt(f3[2]) - 1;
				        			} else {
					        			face.v = new int[3];
					        			face.v[0] = Integer.parseInt(coords[0]) - 1;
					        			face.v[1] = Integer.parseInt(coords[1]) - 1;
					        			face.v[2] = Integer.parseInt(coords[2]) - 1;
				        			}

				        			if(mesh == null)
					        			mesh = new GxMeshObject.Mesh();
				        			mesh.faces.add(face);
							        }
							        break;
				        default   :  System.err.println("UNKNOWN TYPE >> " + line); break;
				        }
			    	}
		    	}	

		        line = br.readLine();
		    }
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		if(mesh.faces.size() != 0) {
			finalizeMesh(obj, mesh);
			obj.meshes().add(mesh);
		}

		return obj;
	}

	static void finalizeMesh(GxMeshObject _o, GxMeshObject.Mesh _m) {
		// calculate the bounding box
		_m.boundBox = Space.newCube(-1e9,-1e9,-1e9, 2e9, 2e9, 2e9);
		for(int i = 0; i < _m.faces.size(); i++) {
			for(int j = 0; j < 3; j++) {
				//update min value
				_m.boundBox.setMinX( MIN(_o.vertex(_m.faces.get(i).v[j]).getX(), _m.boundBox.getMinX()) );
				_m.boundBox.setMinY( MIN(_o.vertex(_m.faces.get(i).v[j]).getY(), _m.boundBox.getMinY()) );
				_m.boundBox.setMinZ( MIN(_o.vertex(_m.faces.get(i).v[j]).getZ(), _m.boundBox.getMinZ()) );

				//update max value
				_m.boundBox.setMaxX( MAX(_o.vertex(_m.faces.get(i).v[j]).getX(), _m.boundBox.getMaxX()) );
				_m.boundBox.setMaxY( MAX(_o.vertex(_m.faces.get(i).v[j]).getY(), _m.boundBox.getMaxY()) );
				_m.boundBox.setMaxZ( MAX(_o.vertex(_m.faces.get(i).v[j]).getZ(), _m.boundBox.getMaxZ()) );
			};
		};
	}
	
	static final double MIN(double _a, double _b) {
		return _a < _b ? _a : _b;
	}
	static final double MAX(double _a, double _b) {
		return _a > _b ? _a : _b;
	}

}
