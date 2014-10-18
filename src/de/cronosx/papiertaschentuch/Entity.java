package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import static de.cronosx.papiertaschentuch.Entity.CollisionType.*;
import javax.vecmath.*;

public class Entity {

	public enum CollisionType {
		CONCAVE,
		CONVEX, 
		BOX
	}

	private Vector3f position, rotation;
	private Model model;
	private Texture texture;
	private RigidBody rigidBody;
	protected float mass;
	protected CollisionType collisionType;
	private Vector3f boxBoundries;

	public Entity() {
		position = new Vector3f();
		rotation = new Vector3f();
		collisionType = CollisionType.CONVEX;
	}

	public RigidBody getRigidBody() {
		return rigidBody;
	}

	public Entity(Model model, Texture texture, float mass, CollisionType collisionType) {
		this();
		this.mass = mass;
		this.collisionType = collisionType;
		this.model = model;
		this.texture = texture;
		initPhysics();
		if(collisionType == BOX) {
			throw new IllegalArgumentException("If collision type is box, you have to specify it's boundries.");
		}
	}
	
	public Entity(Model model, Texture texture, float mass, CollisionType collisionType, Vector3f boxDim) {
		this();
		this.mass = mass;
		this.collisionType = collisionType;
		this.model = model;
		this.texture = texture;
		this.boxBoundries = boxDim;
		initPhysics();
		
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getPosition() {
		return position;
	}

	protected void initPhysics() {
		Vector3f inertia = new Vector3f(0, 0, 0);
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(getPosition());
		getCollisionShape().calculateLocalInertia(mass, inertia);
		DefaultMotionState motionState = new DefaultMotionState(transform);
		RigidBodyConstructionInfo rigidBodyInfo = new RigidBodyConstructionInfo(
				mass, motionState, getCollisionShape(), inertia);
		rigidBody = new RigidBody(rigidBodyInfo);
	}

	public Entity(Model model, Texture texture, float mass) {
		this(model, texture, mass, mass == 0 ? CollisionType.CONCAVE : CollisionType.CONVEX);
	}

	protected CollisionShape getCollisionShape() {
		switch(collisionType) {
			case CONVEX:
				return model.getConvexCollisionShape();
			case CONCAVE:
				return model.getConcaveCollisionShape();
			case BOX:
				return new BoxShape(this.boxBoundries);
			default:
				throw new IllegalArgumentException("Unsupported Collisionshape");
		}
	}

	public void updatePhysicsTransform(Vector3f position, Vector3f rotation) {
		this.position = position;
		this.rotation = rotation;
	}

	public float getMass() {
		return mass;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
		Transform tmp = new Transform();
		rigidBody.getCenterOfMassTransform(tmp);
		tmp.origin.set(position);
		rigidBody.setCenterOfMassTransform(tmp);
	}

	public void move(Vector3f delta) {
		setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
		Transform tmp = new Transform();
		rigidBody.getCenterOfMassTransform(tmp);
		Quat4f quat = new Quat4f();
		QuaternionUtil.setEuler(quat, rotation.x, rotation.y, rotation.z);
		tmp.setRotation(quat);
		rigidBody.setCenterOfMassTransform(tmp);
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
