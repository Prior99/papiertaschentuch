package de.cronosx.papiertaschentuch;

import java.nio.*;
import javax.vecmath.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;

public class Light
{
	private Vector3f position;
	private final int id;
	
	public Light(int id) {
		this.id = id;
		this.position = new Vector3f(0, 0, 0);
	}
	
    public void setPosition(Vector3f position) {
        this.position = position;
    }
    
    public void move(Vector3f delta) {
        setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
    }
	
	public void applyGL() {
		glEnable(id);
		FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(4);
		positionBuffer.put(position.x);
		positionBuffer.put(position.y);
		positionBuffer.put(position.z);
		positionBuffer.put(1.0f);
		positionBuffer.rewind();
		glLight(id, GL_POSITION, positionBuffer);
	}
}
