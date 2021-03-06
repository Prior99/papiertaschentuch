package de.cronosx.papiertaschentuch;

import javax.vecmath.*;

public class Entity implements SceneObject{

	private Vector3f position, rotation;
	private Model model;
	private Texture texture;
	private boolean lightingDeactivated;
	private boolean deactivatedDepthBuffer;

	public Entity() {
		position = new Vector3f();
		rotation = new Vector3f();
		deactivatedDepthBuffer = false;
		lightingDeactivated = false;
	}
	
	public Entity activateDepthBuffer() {
		deactivatedDepthBuffer = false;
		return this;
	}
	
	public Entity deactivateDepthBuffer() {
		deactivatedDepthBuffer = true;
		return this;
	}
	
	public boolean istDepthBufferDeactivated() {
		return deactivatedDepthBuffer;
	}
	
	public boolean isLightingDeactivated() {
		return lightingDeactivated;
	}
	
	public Entity activateLighting() {
		lightingDeactivated = false;
		return this;
	}
	
	public Entity deactivateLighting() {
		lightingDeactivated = true;
		return this;
	}

	public Entity(Model model, Texture texture) {
		this();
		this.model = model;
		this.texture = texture;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	public Entity setModel(Model model) {
		this.model = model;
		return this;
	}

	public Entity setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}

	@Override
	public Entity setPosition(Vector3f position) {
		this.position = position;
		return this;
	}

	@Override
	public Entity move(Vector3f delta) {
		setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
		return this;
	}

	@Override
	public Entity setRotation(Vector3f rotation) {
		this.rotation = rotation;
		return this;
	}

	public Entity rotate(Vector3f delta) {
		setRotation(new Vector3f(rotation.x + delta.x, rotation.y + delta.y, rotation.z + delta.z));
		return this;
	}
	
	public Model getModel() {
		return model;
	}
	
	public Texture getTexture() {
		return texture;
	}
}
