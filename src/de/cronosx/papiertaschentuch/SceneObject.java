package de.cronosx.papiertaschentuch;

import javax.vecmath.*;

public interface SceneObject {
	public SceneObject move(Vector3f delta);
	public SceneObject setPosition(Vector3f position);
	public SceneObject setRotation(Vector3f rotation);
	public Vector3f getRotation();
	public Vector3f getPosition();
}
