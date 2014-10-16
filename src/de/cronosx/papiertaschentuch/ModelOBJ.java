package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.util.*;
import java.io.*;
import java.nio.*;
import javax.vecmath.*;
import org.lwjgl.*;

public class ModelOBJ extends Model {

	private File file;
	private float[] vertices, textureMap, normals;
	private int[] faces;
	private CollisionShape concaveShape, convexShape;

	public ModelOBJ(File file) {
		this.file = file;

	}

	@Override
	protected void load() {
		OBJParser parser = new OBJParser(file);
		if (parser.parse()) {
			vertices = new float[parser.getVertices().size()];
			for (int i = 0; i < parser.getVertices().size(); i++) {
				vertices[i] = parser.getVertices().get(i);
			}
			textureMap = new float[parser.getTextureMappings().size()];
			for (int i = 0; i < parser.getTextureMappings().size(); i++) {
				textureMap[i] = parser.getTextureMappings().get(i);
			}
			normals = new float[parser.getNormals().size()];
			for (int i = 0; i < parser.getNormals().size(); i++) {
				normals[i] = parser.getNormals().get(i);
			}
			faces = new int[parser.getFaces().size()];
			for (int i = 0; i < parser.getFaces().size(); i++) {
				faces[i] = parser.getFaces().get(i);
			}
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

	private void createConvexShape() {
		ObjectArrayList<Vector3f> objArrayList = new ObjectArrayList<>();
		for (int i = 0; i < vertices.length; i += 3) {
			objArrayList.add(new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
		}
		convexShape = new ConvexHullShape(objArrayList);
	}

	private void createConcaveShape() {
		ByteBuffer bbVertices = BufferUtils.createByteBuffer(Float.BYTES * vertices.length);
		ByteBuffer bbIndexes = BufferUtils.createByteBuffer(Integer.BYTES * faces.length);
		for (int i = 0; i < faces.length; i++) {
			bbIndexes.putInt(faces[i]);
		}
		for (int i = 0; i < vertices.length; i += 3) {
			bbVertices.putFloat(-vertices[i]);
			bbVertices.putFloat(vertices[i + 1]);
			bbVertices.putFloat(vertices[i + 2]);
		}
		bbVertices.rewind();
		bbIndexes.rewind();
		int numTriangles = faces.length / 3;
		int numVertices = vertices.length;
		TriangleIndexVertexArray triangleArray = new TriangleIndexVertexArray(numTriangles, bbIndexes, 3 * Integer.BYTES, numVertices, bbVertices, 3 * Float.BYTES);
		concaveShape = new BvhTriangleMeshShape(triangleArray, true);
	}

	@Override
	public CollisionShape getConvexCollisionShape() {
		if (convexShape == null) {
			createConvexShape();
		}
		return convexShape;
	}

	@Override
	public CollisionShape getConcaveCollisionShape() {
		if (concaveShape == null) {
			createConcaveShape();
		}
		return concaveShape;
	}

}
