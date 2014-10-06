package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.*;
import javax.vecmath.*;

public class Physics {

    private Entities entities;
    private DefaultCollisionConfiguration collisionConfiguration;
    //private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
    private CollisionDispatcher dispatcher;
    private ConstraintSolver solver;
    private DynamicsWorld dynamicsWorld = null;
    private Player player;

    public Physics(Entities entities, Player player) {
	this.entities = entities;
	this.player = player;
	Vector3f worldMin = new Vector3f(1000f, 1000f, 1000f);
	Vector3f worldMax = new Vector3f(-1000f, -1000f, -1000f);
	AxisSweep3 sweepBP = new AxisSweep3(worldMin, worldMax);
	collisionConfiguration = new DefaultCollisionConfiguration();
	dispatcher = new CollisionDispatcher(collisionConfiguration);
	solver = new SequentialImpulseConstraintSolver();
	dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, sweepBP, solver, collisionConfiguration);
	dynamicsWorld.addAction(player.getKinematicCharacterController());
	dynamicsWorld.addCollisionObject(player.getGhostObject(), CollisionFilterGroups.CHARACTER_FILTER, (short) (CollisionFilterGroups.STATIC_FILTER | CollisionFilterGroups.DEFAULT_FILTER));
	sweepBP.getOverlappingPairCache().setInternalGhostPairCallback(new GhostPairCallback());
	dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));

    }

    public void addEntity(Entity e) {
	dynamicsWorld.addRigidBody(e.getRigidBody());
    }

    public void tick() {
	dynamicsWorld.stepSimulation(1f / 60f);
	entities.forEach((entity) -> {
	    Transform nextTransform = new Transform();
	    RigidBody rigidBody = entity.getRigidBody();
	    rigidBody.getMotionState().getWorldTransform(nextTransform);
	    Quat4f quat = new Quat4f();
	    nextTransform.getRotation(quat);
	    entity.updatePhysicsTransform(nextTransform.origin, quadRotationToEulerAngles(quat));
	});
    }

    public static Vector3f quadRotationToEulerAngles(Quat4f quat) {
	return new Vector3f(
		(float) Math.atan2(2 * (quat.w * quat.x + quat.y * quat.z), 1 - 2 * (quat.x * quat.x + quat.y * quat.y)),
		(float) Math.asin(2 * (quat.w * quat.y - quat.z * quat.x)),
		(float) Math.atan2(2 * (quat.x * quat.z + quat.x * quat.y), 1 - 2 * (quat.y * quat.y + quat.z * quat.z)));
    }
}
