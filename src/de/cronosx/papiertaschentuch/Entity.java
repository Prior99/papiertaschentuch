package de.cronosx.papiertaschentuch;

import com.bulletphysics.dynamics.*;
import com.bulletphysics.linearmath.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import javax.vecmath.Vector3f;

public class Entity {
    private Vector3f position, rotation;
    private Model model;
    private Texture texture;
	private RigidBody rigidBody;
	private float mass;
    
    public Entity() {
        position = new Vector3f();
        rotation = new Vector3f();
    }
	
	public RigidBody getRigidBody() {
		return rigidBody;
	}
    
    public Entity(Model model, Texture texture, float mass) {
        this();
        this.model = model;
        this.texture = texture;
		Vector3f inertia = new Vector3f(0, 0, 0);
		Transform transform = new Transform();
		transform.setIdentity();
		transform.origin.set(new Vector3f(0, 0, 0));
		model.getCollisionShape().calculateLocalInertia(mass, inertia);
		DefaultMotionState motionState = new DefaultMotionState(transform);
		RigidBodyConstructionInfo rigidBodyInfo = new RigidBodyConstructionInfo(
			mass, motionState, model.getCollisionShape(), inertia);
		rigidBody = new RigidBody(rigidBodyInfo);
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
    }
    
    public void rotate(Vector3f delta) {
        rotation = new Vector3f(rotation.x + delta.x, rotation.y + delta.y, rotation.z + delta.z);
    }
    
    public void draw() {
        if(model != null && texture != null) {
            glPushMatrix();
            glTranslatef(position.x, position.y, position.z);
			glRotatef(Graphics.radiantToDegree(rotation.x), 1.f, 0.f, 0.f);
			glRotatef(Graphics.radiantToDegree(rotation.y), 0.f, 1.f, 0.f);
			glRotatef(Graphics.radiantToDegree(rotation.z), 0.f, 0.f, 1.f);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.retrieveTextureID());
            model.draw();
            glPopMatrix();
        }
    }
}
