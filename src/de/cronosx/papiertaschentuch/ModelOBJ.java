package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.util.*;
import java.io.*;
import javax.vecmath.*;

public class ModelOBJ extends Model {

	private File file;
	private float[] vertices, textureMap, normals;
	private int[] faces;
	private ConvexHullShape shape;

	public ModelOBJ(File file) {
		this.file = file;

	}

	@Override
	protected void load() {
		OBJParser parser = new OBJParser(file);
		if(parser.parse()) {
			vertices = new float[parser.getVertices().size()];
			for(int i = 0; i < parser.getVertices().size(); i++) {
				vertices[i] = parser.getVertices().get(i);
			}
			textureMap = new float[parser.getTextureMappings().size()];
			for(int i = 0; i < parser.getTextureMappings().size(); i++) {
				textureMap[i] = parser.getTextureMappings().get(i);
			}
			normals = new float[parser.getNormals().size()];
			for(int i = 0; i < parser.getNormals().size(); i++) {
				normals[i] = parser.getNormals().get(i);
			}
			faces = new int[parser.getFaces().size()];
			for(int i = 0; i < parser.getFaces().size(); i++) {
				faces[i] = parser.getFaces().get(i);
			}
			ObjectArrayList<Vector3f> objArrayList = new ObjectArrayList<>();
			objArrayList.addAll(parser.getVertexList());
			shape = new ConvexHullShape(objArrayList);
		}
	}

	@Override
	protected float[] getTextureMap() {
		return textureMap;
	}

	@Override
	protected float[] getVertices() {
		return vertices;
	}

	@Override
	protected float[] getNormals() {
		return normals;
	}

	@Override
	protected int[] getFaces() {
		return faces;
	}

	@Override
	public CollisionShape getCollisionShape() {
		return shape;
	}

}
