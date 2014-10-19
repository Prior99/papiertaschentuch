package de.cronosx.papiertaschentuch;

import java.nio.FloatBuffer;
import javax.vecmath.*;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class Light {

	private Vector3f position;
	private Vector3f color;
	private final int id;
	private boolean changed;
	private Entity debugEntity;

	public Light(int id) {
		this.id = id;
		this.color = new Vector3f(1.f, 1.f, 1.f);
		this.position = new Vector3f(0, 0, 0);
		changed = true;
		if(Papiertaschentuch.getConfig().getBool("Debug Lights", false)) {
			Entity e = new Entity();
			setDebugEntity(e);
			Papiertaschentuch.getInstance().addEntity(e);
		}
	}
	
	private void setDebugEntity(Entity entity) {
		this.debugEntity = entity;
		entity.setModel(Models.getModel("lightsource.obj"));
		entity.setTexture(Textures.getTexture("lightsource.png"));
		entity.deactivateLighting();
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public void setColor(Vector3f v) {
		this.color = v;
		changed = true;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		changed = true;
		if(debugEntity != null) {
			debugEntity.setPosition(position);
		}
	}
	
	public void bind() {
		if(changed) {
			changed = false;
			FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(4);
			positionBuffer.put(position.x);
			positionBuffer.put(position.y);
			positionBuffer.put(position.z);
			positionBuffer.put(1.0f);
			positionBuffer.rewind();
			glLight(id, GL_POSITION, positionBuffer);
			FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(4);
			colorBuffer.put(color.x);
			colorBuffer.put(color.y);
			colorBuffer.put(color.z);
			colorBuffer.put(1.0f);
			colorBuffer.rewind();
			glLight(id, GL_DIFFUSE, colorBuffer);
			glLight(id, GL_SPECULAR, colorBuffer);
		}
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void move(Vector3f delta) {
		setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
	}
}
