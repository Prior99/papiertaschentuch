package de.cronosx.papiertaschentuch;

import java.io.*;
import javax.vecmath.*;

public class PointLight implements SceneObject, Lightsource {

	private Vector3f position;
	private Vector3f color;
	private boolean changed;
	private boolean enabled;
	private float strength;
	private Entity debugEntity;
	private final int id;
	
	public PointLight(int id) {
		this.id = id;
		this.color = new Vector3f(1.f, 1.f, 1.f);
		this.position = new Vector3f(0, 0, 0);
		this.strength = 1.f;
		this.enabled = true;
		changed = true;
		if(Papiertaschentuch.getConfig().getBool("Debug Lights", false)) {
			Entity e = Entities.createEntity();
			setDebugEntity(e);
		}
	}
	
	public int getID() {
		return id;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean checkChanged() {
		boolean ch = changed;
		changed = false;
		return ch;
	}
	
	private void setDebugEntity(Entity entity) {
		this.debugEntity = entity;
		try {
			entity.setModel(Models.getModel("models/lightsource.obj"));
			entity.setTexture(Textures.getTexture("textures/lightsource.png"));
			entity.deactivateLighting();
			entity.deactivateDepthBuffer();
		}
		catch(IOException e) {
			Log.fatal("Model \"models/lightsource.obj\" or texture \"textures/lightsource.png\" not found. unable to debug lightsources.");
		}
	}
	
	public PointLight enable() {
		enabled = true;
		changed = true;
		return this;
	}
	
	public PointLight disable() {
		enabled = false;
		changed = true;
		return this;
	}
	
	public float getStrength() {
		return strength;
	}
	
	public PointLight setStrength(float f) {
		this.strength = f;
		changed = true;
		return this;
	}
	
	@Override
	public Vector3f getColor() {
		return color;
	}
	
	@Override
	public PointLight setColor(Vector3f v) {
		this.color = v;
		changed = true;
		return this;
	}

	@Override
	public PointLight setPosition(Vector3f position) {
		this.position = position;
		changed = true;
		if(debugEntity != null) {
			debugEntity.setPosition(position);
		}
		return this;
	}
	
	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public PointLight move(Vector3f delta) {
		setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
		return this;
	}

	@Override
	public SceneObject setRotation(Vector3f rotation) { return this; }

	@Override
	public Vector3f getRotation() { return new Vector3f(0.f, 0.f, 0.f); }
}
