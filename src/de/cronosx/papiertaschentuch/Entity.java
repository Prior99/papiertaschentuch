package de.cronosx.papiertaschentuch;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.util.vector.Vector3f;

public class Entity {
    private Vector3f position, rotation, scale;
    private Model model;
    private Texture texture;
    
    public Entity() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1.f, 1.f, 1.f);
    }
    
    public Entity(Model model) {
        this();
        this.model = model;
    }
    
    public Entity(Model model, Texture texture) {
        this();
        this.model = model;
        this.texture = texture;
    }
    
    public void setModel(Model model) {
        this.model = model;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    public void move(Vector3f delta) {
        position = new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z);
    }
    
    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    
    public void rotate(Vector3f delta) {
        rotation = new Vector3f(rotation.x + delta.x, rotation.y + delta.y, rotation.z + delta.z);
    }
    
    public void scale(Vector3f delta) {
        scale = new Vector3f(scale.x + delta.x, scale.y + delta.y, scale.z + delta.z);
    }
    
    public void setScale(Vector3f scale) {
        this.scale = scale;
    }
    
    public void draw() {
        if(model != null && texture != null) {
            glPushMatrix();
            glTranslatef(position.x, position.y, position.z);
            glScalef(scale.x, scale.y, scale.z);
            glTranslatef((1 - scale.x) / 2, (1 - scale.y) / 2, (1 - scale.z) / 2);
            glRotatef(rotation.x, 1.f, 0.f, 0.f);
            glRotatef(rotation.y, 0.f, 1.f, 0.f);
            glRotatef(rotation.z, 0.f, 0.f, 1.f);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture.retrieveTextureID());
            model.draw();
            glPopMatrix();
        }
    }
}
