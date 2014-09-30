package de.cronosx.papiertaschentuch.models;

import com.bulletphysics.collision.shapes.*;
import de.cronosx.papiertaschentuch.Model;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.vecmath.*;

public class Cube extends Model {

	private float[] vertices, textureMap, normals;
	private int[] faces;

	@Override
	protected void load() {
		vertices = new float[]{
			-.5f, -.5f, .5f,//Front
			.5f, -.5f, .5f,
			.5f, .5f, .5f,
			-.5f, .5f, .5f,
			-.5f, -.5f, -.5f,//Back
			-.5f, .5f, -.5f,
			.5f, .5f, -.5f,
			.5f, -.5f, -.5f,
			-.5f, .5f, -.5f,//Top
			-.5f, .5f, .5f,
			.5f, .5f, .5f,
			.5f, .5f, -.5f,
			-.5f, -.5f, -.5f,//Bottom
			.5f, -.5f, -.5f,
			.5f, -.5f, .5f,
			-.5f, -.5f, .5f,
			.5f, -.5f, -.5f,//Right
			.5f, .5f, -.5f,
			.5f, .5f, .5f,
			.5f, -.5f, .5f,
			-.5f, -.5f, -.5f,//Left
			-.5f, -.5f, .5f,
			-.5f, .5f, .5f,
			-.5f, .5f, -.5f
		};
		normals = new float[]{
			0.f, 0.f, 1.f, //Front
			0.f, 0.f, 1.f,
			0.f, 0.f, 1.f,
			0.f, 0.f, 1.f,
			0.f, 0.f, -1.f, //Back
			0.f, 0.f, -1.f,
			0.f, 0.f, -1.f,
			0.f, 0.f, -1.f,
			0.f, 1.f, 0.f, //Top
			0.f, 1.f, 0.f,
			0.f, 1.f, 0.f,
			0.f, 1.f, 0.f,
			0.f, -1.f, 0.f, //Bottom
			0.f, -1.f, 0.f,
			0.f, -1.f, 0.f,
			0.f, -1.f, 0.f,
			1.f, 0.f, 0.f, //Right
			1.f, 0.f, 0.f,
			1.f, 0.f, 0.f,
			1.f, 0.f, 0.f,
			-1.f, 0.f, 0.f, //Left
			-1.f, 0.f, 0.f,
			-1.f, 0.f, 0.f,
			-1.f, 0.f, 0.f,};
		textureMap = new float[]{
			0.f, 0.f,//Front
			1.f, 0.f,
			1.f, 1.f,
			0.f, 1.f,
			1.f, 0.f,//Back
			1.f, 1.f,
			0.f, 1.f,
			0.f, 0.f,
			0.f, 1.f,//Top
			0.f, 0.f,
			1.f, 0.f,
			1.f, 1.f,
			1.f, 1.f,//Bottom
			0.f, 1.f,
			0.f, 0.f,
			1.f, 0.f,
			1.f, 0.f,//Right
			1.f, 1.f,
			0.f, 1.f,
			0.f, 0.f,
			0.f, 0.f,//Left
			1.f, 0.f,
			1.f, 1.f,
			0.f, 1.f
		};
		faces = new int[]{
			0, 1, 2,//Front
			0, 2, 3,
			4, 5, 6,//Back
			4, 6, 7,
			8, 9, 10,//Top
			8, 10, 11,
			12, 13, 14,//Bottom
			12, 14, 15,
			16, 17, 18,//Right
			16, 18, 19,
			20, 21, 22,//Left
			20, 22, 23
		};
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
		return new BoxShape(new Vector3f(.5f, .5f, .5f));
	}
}
