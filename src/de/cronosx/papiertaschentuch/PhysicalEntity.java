package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import static de.cronosx.papiertaschentuch.PhysicalEntity.CollisionType.*;
import javax.vecmath.*;

public class PhysicalEntity extends Entity {

	public enum CollisionType {
		CONCAVE,
		CONVEX, 
		BOX
	}

	private RigidBody rigidBody;
	protected float mass;
	protected CollisionType collisionType;
	private Vector3f boxBoundries;

	public PhysicalEntity() {
		super();
		collisionType = CollisionType.CONVEX;
	}

	public RigidBody getRigidBody() {
		return rigidBody;
	}

	public PhysicalEntity(Model model, Texture texture, float mass, CollisionType collisionType) {
		super(model, texture);
		this.mass = mass;
		this.collisionType = collisionType;
		initPhysics();
		if(collisionType == BOX) {
			throw new IllegalArgumentException("If collision type is box, you have to specify it's boundries.");
		}
	}
	
	public PhysicalEntity(Model model, Texture texture, float mass, CollisionType collisionType, Vector3f boxDim) {
		super(model, texture);
		this.mass = mass;
		this.collisionType = collisionType;
		this.boxBoundries = boxDim;
		initPhysics();
		
	}

	private void initPhysics() {
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

	public PhysicalEntity(Model model, Texture texture, float mass) {
		this(model, texture, mass, mass == 0 ? CollisionType.CONCAVE : CollisionType.CONVEX);
	}

	protected CollisionShape getCollisionShape() {
		switch(collisionType) {
			case CONVEX:
				return getModel().getConvexCollisionShape();
			case CONCAVE:
				return getModel().getConcaveCollisionShape();
			case BOX:
				return new BoxShape(this.boxBoundries);
			default:
				throw new IllegalArgumentException("Unsupported Collisionshape");
		}
	}

	public void updatePhysicsTransform(Vector3f position, Vector3f rotation) {
		super.setPosition(position);
		super.setRotation(rotation);
	}

	public float getMass() {
		return mass;
	}

	@Override
	public void setPosition(Vector3f position) {
		super.setPosition(position);
		Transform tmp = new Transform();
		rigidBody.getCenterOfMassTransform(tmp);
		tmp.origin.set(position);
		rigidBody.setCenterOfMassTransform(tmp);
	}

	@Override
	public void setRotation(Vector3f rotation) {
		super.setRotation(rotation);
		Transform tmp = new Transform();
		rigidBody.getCenterOfMassTransform(tmp);
		Quat4f quat = new Quat4f();
		QuaternionUtil.setEuler(quat, rotation.x, rotation.y, rotation.z);
		tmp.setRotation(quat);
		rigidBody.setCenterOfMassTransform(tmp);
	}
}
