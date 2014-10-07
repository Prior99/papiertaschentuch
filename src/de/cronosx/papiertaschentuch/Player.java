package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.character.*;
import com.bulletphysics.linearmath.*;
import javax.vecmath.*;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class Player {
	
	private final KinematicCharacterController character;
	private final PairCachingGhostObject ghostObject;
	private final ConvexShape shape;
	private Vector3f rotation;
	private static final float 
		height = 1.f, 
		width = .75f,
		stepHeight = height/2.f, 
		speed = 0.2f;
	
	public Player() {
		rotation = new Vector3f();
		ghostObject = new PairCachingGhostObject();
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(0.f, 0.f, 0.f);
		ghostObject.setWorldTransform(transform);
		shape = new BoxShape(new Vector3f(width, height, width));
		ghostObject.setCollisionShape(shape);
		ghostObject.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
		character = new KinematicCharacterController(ghostObject, shape, stepHeight);
		
	}
	
	public KinematicCharacterController getKinematicCharacterController() {
		return character;
	}
	
	public PairCachingGhostObject getGhostObject() {
		return ghostObject;
	}
	
	public void setWalkDirection(Vector3f direction) {
		character.setWalkDirection(new Vector3f(direction.x * speed, direction.y * speed, direction.z * speed));
	}
	
	public Vector3f getPosition() {
		Transform ghostTransform = new Transform();
		ghostObject.getWorldTransform(ghostTransform);
		return ghostTransform.origin;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

	public void rotate(Vector3f delta) {
		float nx = rotation.x + delta.x;
		if(nx > Math.PI /2) nx = (float)Math.PI / 2.f;
		if(nx < -Math.PI /2) nx = -(float)Math.PI / 2.f;
		rotation = new Vector3f(nx, rotation.y + delta.y, rotation.z + delta.z);
	}

	public void transform() {
		glRotatef(Graphics.radiantToDegree(getRotation().x), 1.f, 0.f, 0.f);
		glRotatef(Graphics.radiantToDegree(getRotation().y), 0.f, 1.f, 0.f);
		glRotatef(Graphics.radiantToDegree(getRotation().z), 0.f, 0.f, 1.f);
		glTranslatef(getPosition().x, -getPosition().y - height*2, getPosition().z);
	}
}