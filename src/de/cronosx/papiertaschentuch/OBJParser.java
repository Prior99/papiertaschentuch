package de.cronosx.papiertaschentuch;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.vecmath.*;

public class OBJParser {

	private final List<Float> finalVertices, finalTextureMappings, finalNormals;
	private final List<Integer> finalFaces;
	private final List<Vector3f> vertices, vertexNormals;
	private final List<Vector2f> vertexTextureMappings;
	private final List<Face> faces;
	private final File file;
	private boolean fNormals, fTextureMapping;

	public OBJParser(File file) {
		this.file = file;
		vertices = new ArrayList<>();
		vertexNormals = new ArrayList<>();
		vertexTextureMappings = new ArrayList<>();
		faces = new ArrayList<>();

		finalVertices = new ArrayList<>();
		finalTextureMappings = new ArrayList<>();
		finalNormals = new ArrayList<>();
		finalFaces = new ArrayList<>();
	}

	private class Face {

		private int[] vertices, textureMappings, normals;

		public Face(int... vertices) {
			this.vertices = vertices;
		}

		public void setTextureMappings(int... mappings) {
			this.textureMappings = mappings;
		}

		public void setNormals(int... normals) {
			this.normals = normals;
		}

		public boolean hasNormals() {
			return normals != null;
		}

		public boolean hasTextureMappings() {
			return textureMappings != null;
		}

		public int[] getVertices() {
			return vertices;
		}

		public int[] getNormals() {
			return normals;
		}

		public int[] getTextureMappings() {
			return textureMappings;
		}
	}

	private void parseVertex(String[] parts) {
		if(parts.length == 4) {
			try {
				Vector3f v = new Vector3f(
						Float.parseFloat(parts[1]),
						Float.parseFloat(parts[2]),
						Float.parseFloat(parts[3]));
				vertices.add(v);
			}
			catch(NumberFormatException e) {
				throw new IllegalArgumentException("Bad numberformat.");
			}
		}
		else {
			throw new IllegalArgumentException("Wrong amount of floats for vertex. Expected 3.");
		}
	}

	private void parseVertexNormal(String[] parts) {
		if(parts.length == 4) {
			try {
				Vector3f v = new Vector3f(
						Float.parseFloat(parts[1]),
						Float.parseFloat(parts[2]),
						Float.parseFloat(parts[3]));
				vertexNormals.add(v);
			}
			catch(NumberFormatException e) {
				throw new IllegalArgumentException("Bad numberformat.");
			}
		}
		else {
			throw new IllegalArgumentException("Wrong amount of floats for vertexnormal. Expected 3.");
		}
	}

	private void parseVertexTextureMap(String[] parts) {
		if(parts.length == 3) {
			try {
				Vector2f v = new Vector2f(
						Float.parseFloat(parts[1]),
						Float.parseFloat(parts[2]));
				vertexTextureMappings.add(v);
			}
			catch(NumberFormatException e) {
				throw new IllegalArgumentException("Bad numberformat.");
			}
		}
		else {
			throw new IllegalArgumentException("Wrong amount of floats. Expected 2.");
		}
	}

	private void parseFace(String[] parts) {
		Face f;
		if(parts.length == 3) {
			String[][] partStruct = new String[][]{
				parts[1].split("/"),
				parts[2].split("/"),
				parts[3].split("/")};
			if(partStruct[0].length == partStruct[1].length && partStruct[1].length == partStruct[2].length) {
				f = new Face(
						Integer.parseInt(partStruct[0][0]),
						Integer.parseInt(partStruct[1][0]),
						Integer.parseInt(partStruct[2][0])
				);
				if(partStruct[0].length >= 2) { //vertices and texturemappings
					if(!partStruct[0][1].isEmpty() && !partStruct[1][1].isEmpty() && !partStruct[2][1].isEmpty()) {
						f.setTextureMappings(
								Integer.parseInt(partStruct[0][1]),
								Integer.parseInt(partStruct[1][1]),
								Integer.parseInt(partStruct[2][1])
						);
					}
				}
				if(partStruct[0].length >= 3) { //vertices, normals and texturemappings
					if(!partStruct[0][2].isEmpty() && !partStruct[1][2].isEmpty() && !partStruct[2][2].isEmpty()) {
						f.setNormals(
								Integer.parseInt(partStruct[0][2]),
								Integer.parseInt(partStruct[1][2]),
								Integer.parseInt(partStruct[2][2])
						);
					}
				}
				faces.add(f);
			}
			else {
				throw new IllegalArgumentException("Inconsistent face. Amount of parameters varies for each vertex.");
			}
		}
		else {
			throw new UnsupportedOperationException("Faces using polygons with other than 3 vertices are currently not supported.");
		}
	}

	private void parseGroup(String[] parts) {
		throw new UnsupportedOperationException("Groups are not yet implemented in this parser.");
	}

	private void parseUsemtl(String[] parts) {
		throw new UnsupportedOperationException("Materials are not yet implemented in this parser.");
	}

	private void parseNIL(String[] parts) {
		throw new UnsupportedOperationException("Unknown Token.");
	}

	public boolean parse() {
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			int num = 0;
			while((line = reader.readLine()) != null) {
				num++;
				line = line.trim();
				if(!line.startsWith("#") && !line.isEmpty()) {
					String[] parts = line.split("\\s+");
					if(parts.length >= 1) {
						try {
							switch(parts[0]) {
								case "v":
									parseVertex(parts);
									break;
								case "vn":
									parseVertexNormal(parts);
									break;
								case "vt":
									parseVertexTextureMap(parts);
									break;
								case "f":
									parseFace(parts);
									break;
								case "g":
									parseGroup(parts);
									break;
								case "usemtl":
									parseUsemtl(parts);
									break;
								default:
									parseNIL(parts);
									break;
							}
						}
						catch(UnsupportedOperationException e) {
							System.out.println("Unsupported operation in line " + line + " of OBJ-File: " + e.getMessage());
						}
						catch(IllegalArgumentException e) {
							System.out.println("Illegal argument in line " + line + " of OBJ-File: " + e.getMessage());
						}
					}
				}
			}
			reader.close();
			try {
				verify();
				build();
				return true;
			}
			catch(IllegalArgumentException e) {
				System.out.println("Verification failed: " + e.getMessage());
				return false;
			}
		}
		catch(FileNotFoundException e) {
			System.out.println("Could not find file.");
			return false;
		}
		catch(IOException e) {
			System.out.println("Error while reading file.");
			return false;
		}
	}

	private void verify() {
		boolean first = true;
		for(Face f : faces) {
			if(first) {
				first = false;
				fNormals = f.hasNormals();
				fTextureMapping = f.hasTextureMappings();
			}
			else {
				if(f.hasNormals() != fNormals || f.hasTextureMappings() != fTextureMapping) {
					throw new IllegalArgumentException("Some faces have more attributes then others.");
				}
			}
			for(int i : f.getNormals()) {
				if(i >= vertexNormals.size()) {
					throw new IllegalArgumentException("Face is referring to normal that is out of bounds.");
				}
			}
			for(int i : f.getVertices()) {
				if(i >= vertices.size()) {
					throw new IllegalArgumentException("Face is referring to vertex that is out of bounds.");
				}
			}
			for(int i : f.getTextureMappings()) {
				if(i >= vertexTextureMappings.size()) {
					throw new IllegalArgumentException("Face is referring to texturemapping that is out of bounds.");
				}
			}
		}
	}

	public boolean hasTextureMapping() {
		return fTextureMapping;
	}

	public boolean hasNormals() {
		return fNormals;
	}

	public List<Float> getNormals() {
		return finalNormals;
	}

	public List<Float> getVertices() {
		return finalVertices;
	}

	public List<Float> getTextureMappings() {
		return finalTextureMappings;
	}

	public List<Integer> getFaces() {
		return finalFaces;
	}

	private void build() {
		int index = 0;
		for(Face f : faces) {
			if(hasNormals()) {
				for(int vn : f.getNormals()) {
					Vector3f vnv = vertexNormals.get(vn);
					finalNormals.add(vnv.x);
					finalNormals.add(vnv.y);
					finalNormals.add(vnv.z);
				}
			}
			if(hasTextureMapping()) {
				for(int vt : f.getTextureMappings()) {
					Vector2f vtv = vertexTextureMappings.get(vt);
					finalTextureMappings.add(vtv.x);
					finalTextureMappings.add(vtv.y);
				}
			}
			for(int v : f.getVertices()) {
				Vector3f vv = vertices.get(v);
				finalVertices.add(vv.x);
				finalVertices.add(vv.y);
				finalVertices.add(vv.z);
			}
			finalFaces.add(index++);
			finalFaces.add(index++);
			finalFaces.add(index++);
		}
	}
}
