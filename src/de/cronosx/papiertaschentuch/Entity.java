package de.cronosx.papiertaschentuch;

import javax.vecmath.*;

public class Entity {

	private Vector3f position, rotation;
	private Model model;
	private Texture texture;
	private boolean lightingDeactivated;

	public Entity() {
		position = new Vector3f();
		rotation = new Vector3f();
		lightingDeactivated = false;
	}
	
	public boolean isLightingDeactivated() {
		return lightingDeactivated;
	}
	
	public void activateLighting() {
		lightingDeactivated = false;
	}
	
	public void deactivateLighting() {
		lightingDeactivated = true;
	}

	public Entity(Model model, Texture texture) {
		this();
		this.model = model;
		this.texture = texture;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void move(Vector3f delta) {
		setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public void rotate(Vector3f delta) {
		setRotation(new Vector3f(rotation.x + delta.x, rotation.y + delta.y, rotation.z + delta.z));
	}
	
	public Model getModel() {
		return model;
	}
	
	public Texture getTexture() {
		return texture;
	}
}
