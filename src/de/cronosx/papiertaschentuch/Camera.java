package de.cronosx.papiertaschentuch;

import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import javax.vecmath.Vector3f;

public class Camera {

	private static Vector3f position = new Vector3f(0, 0, 0),
			rotation = new Vector3f(0, 0, 0);

	public static void setRotation(Vector3f rotation) {
		Camera.rotation = rotation;
	}

	public static void rotate(Vector3f delta) {
		Camera.rotation = new Vector3f(rotation.x + delta.x, rotation.y + delta.y, rotation.z + delta.z);
	}

	public static void setPosition(Vector3f position) {
		Camera.position = position;
	}

	public static void move(Vector3f delta) {
		Camera.position = new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z);
	}

	public static Vector3f getPosition() {
		return position;
	}

	public static void transform() {
		glRotatef(Graphics.radiantToDegree(rotation.x), 1.f, 0.f, 0.f);
		glRotatef(Graphics.radiantToDegree(rotation.y), 0.f, 1.f, 0.f);
		glRotatef(Graphics.radiantToDegree(rotation.z), 0.f, 0.f, 1.f);
		glTranslatef(position.x, position.y, position.z);
	}

	public static Vector3f getRotation() {
		return rotation;
	}
}
