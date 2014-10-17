package de.cronosx.papiertaschentuch;

import javax.vecmath.*;

public class Light {

	private Vector3f position;
	private Vector3f color;
	private final int id;
	private float strength;

	public Light(int id) {
		this.id = id;
		this.strength = 20f;
		this.color = new Vector3f(1.f, 1.f, 1.f);
		this.position = new Vector3f(0, 0, 0);
	}
	
	public float getStrength() {
		return strength;
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public void setStrength(float strength) {
		this.strength = strength;
	}
	
	public void setColor(Vector3f v) {
		this.color = v;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public void move(Vector3f delta) {
		setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
	}
}
