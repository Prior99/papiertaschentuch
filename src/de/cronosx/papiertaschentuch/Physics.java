package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.*;
import java.util.List;
import javax.vecmath.*;
public class Physics {
    private Entities entities;
    private DefaultCollisionConfiguration collisionConfiguration;
    //private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
    private BroadphaseInterface broadphase;
    private CollisionDispatcher dispatcher;
    private ConstraintSolver solver;
    private DynamicsWorld dynamicsWorld = null;
    
    public Physics(Entities entities) {
		this.entities = entities;
        collisionConfiguration = new DefaultCollisionConfiguration();
        dispatcher = new CollisionDispatcher(collisionConfiguration);
        broadphase = new DbvtBroadphase();
        solver = new SequentialImpulseConstraintSolver();
		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));
	
    }
	
	public void addEntity(Entity e) {
		dynamicsWorld.addRigidBody(e.getRigidBody());
	}
    
    public void tick() {
        dynamicsWorld.stepSimulation(1f/60f);
		entities.forEach((entity) -> {
			Transform nextTransform = new Transform();
			RigidBody rigidBody = entity.getRigidBody();
			rigidBody.getMotionState().getWorldTransform(nextTransform);
			Quat4f quat = new Quat4f();
			nextTransform.getRotation(quat);
			entity.updatePhysicsTransform(nextTransform.origin, new Vector3f(quat.w * quat.x, quat.w * quat.y, quat.w * quat.z));
		});
    }
}
