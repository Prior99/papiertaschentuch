package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
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
	private RigidBody rigidBody;
	private MotionState motionState;
	private static final float height = 2.f,
			width = .75f,
			stepHeight = height / 2.f,
			speed = .1f,
			mass = 90f,
			jumpHeight = .5f;

	public Player() {
		rotation = new Vector3f();
		ghostObject = new PairCachingGhostObject();
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(0.f, 0.f, 0.f);
		ghostObject.setWorldTransform(transform);
		shape = new CapsuleShape(width, height);
		ghostObject.setCollisionShape(shape);
		ghostObject.setCollisionFlags(CollisionFlags.CHARACTER_OBJECT);
		character = new KinematicCharacterController(ghostObject, shape, stepHeight);
		character.setMaxJumpHeight(1f);
		character.setFallSpeed(100f);
		character.setJumpSpeed(3f);
		motionState = new KinematicMotionState();
		RigidBodyConstructionInfo rigidBodyInfo = new RigidBodyConstructionInfo(
				mass, motionState, shape, new Vector3f(0, 0, 0));
		rigidBody = new RigidBody(rigidBodyInfo);
		rigidBody.setCollisionFlags(rigidBody.getCollisionFlags() | CollisionFlags.KINEMATIC_OBJECT);
		rigidBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}
	
	public void jump() {
		character.jump();
	}
	
	public RigidBody getRigidBody() {
		return rigidBody;
	}

	public KinematicCharacterController getKinematicCharacterController() {
		return character;
	}

	public PairCachingGhostObject getGhostObject() {
		return ghostObject;
	}

	public void setWalkDirection(Vector3f direction) {
		character.setWalkDirection(new Vector3f(-direction.x * speed, 0.0f, -direction.z * speed));
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
	
	public void tick() {
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(getPosition());
		motionState.setWorldTransform(transform);
		rigidBody.setCenterOfMassTransform(motionState.getWorldTransform(new Transform()));
	}

	public void rotate(Vector3f delta) {
		float nx = rotation.x + delta.x;
		if (nx > Math.PI / 2) {
			nx = (float) Math.PI / 2.f;
		}
		if (nx < -Math.PI / 2) {
			nx = -(float) Math.PI / 2.f;
		}
		setRotation(new Vector3f(nx, rotation.y + delta.y, rotation.z + delta.z));
	}
	
	public static class KinematicMotionState extends MotionState {
		private final Transform transform;

		public KinematicMotionState() {
			transform = new Transform();
			transform.setIdentity();
		}

		@Override
		public Transform getWorldTransform(Transform out) {
			out.set(transform);
			return out;
		}

		@Override
		public void setWorldTransform(Transform worldTrans) {
			transform.set(worldTrans);
		}
	}
}
