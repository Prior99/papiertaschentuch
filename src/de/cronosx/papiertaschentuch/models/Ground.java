package de.cronosx.papiertaschentuch.models;

import com.bulletphysics.collision.shapes.*;
import de.cronosx.papiertaschentuch.*;
import javax.vecmath.*;

public class Ground extends Model {
	private float[] vertices, textureMap, normals;
	private int[] faces;
	
	@Override
	protected void load() {
		vertices = new float[] {
			-100f, 0.f, -100f,//Bottom
			 100f, 0.f, -100f,
			 100f, 0.f,  100f,
			-100f, 0.f,  100f
		};
		normals = new float[] {
			0.f, -1.f,  0.f, //Top
			0.f, -1.f,  0.f,
			0.f, -1.f,  0.f,
			0.f, -1.f,  0.f
		};
		textureMap = new float[] {
			100f, 100f,//Bottom
			 0.f, 100f,
			 0.f, 0.f,
			100f, 0.f
		};
		faces = new int[] {
			0,  1,  2,//Front
			0,  2,  3
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
		return new com.bulletphysics.collision.shapes.BoxShape(new Vector3f(100f, 0f, 100f));
	}
}
