package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public abstract class Model {

	protected int vertexBufferID, normalBufferID, textureBufferID, facesBufferID;
	protected FloatBuffer vertexBuffer, normalBuffer, textureMapBuffer;
	protected IntBuffer facesBuffer;
	protected int indexCount;
	private boolean bound;

	public Model() {
	}

	protected abstract void load();

	public void init() {
		load();
		float[] verticesArray = getVertices(),
				normalsArray = getNormals(),
				textureMapArray = getTextureMap();
		int[] facesArray = getFaces();
		vertexBuffer = BufferUtils.createFloatBuffer(verticesArray.length);
		normalBuffer = BufferUtils.createFloatBuffer(normalsArray.length);
		textureMapBuffer = BufferUtils.createFloatBuffer(textureMapArray.length);
		facesBuffer = BufferUtils.createIntBuffer(facesArray.length);
		vertexBuffer.put(getVertices());
		normalBuffer.put(normalsArray);
		textureMapBuffer.put(textureMapArray);
		facesBuffer.put(facesArray);
		vertexBuffer.rewind();
		normalBuffer.rewind();
		facesBuffer.rewind();
		textureMapBuffer.rewind();
		indexCount = facesArray.length;
	}

	private void bind() {
		vertexBufferID = glGenBuffers();
		normalBufferID = glGenBuffers();
		textureBufferID = glGenBuffers();
		facesBufferID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, normalBufferID);
		glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, textureBufferID);
		glBufferData(GL_ARRAY_BUFFER, textureMapBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, facesBufferID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, facesBuffer, GL_STATIC_DRAW);
		bound = true;
	}
	
	public int getIndexCount() {
		return indexCount;
	}
	
	public int getVertexBufferID() {
		if(!bound) bind();
		return vertexBufferID;
	}
	
	public int getTextureBufferID() {
		if(!bound) bind();
		return textureBufferID;
	}
	
	public int getNormalBufferID() {
		if(!bound) bind();
		return normalBufferID;
	}
	
	public int getIndexBufferID() {
		if(!bound) bind();
		return facesBufferID;
	}

	protected abstract float[] getVertices();

	protected abstract float[] getNormals();

	protected abstract float[] getTextureMap();

	protected abstract int[] getFaces();

	public abstract CollisionShape getConvexCollisionShape();

	public abstract CollisionShape getConcaveCollisionShape();
}
