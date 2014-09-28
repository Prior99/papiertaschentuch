package de.cronosx.papiertaschentuch;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Input {
    public static final float speed = .1f;
    public static void tick() {
        if(Display.isActive()) {
            if(Keyboard.isKeyDown(Keyboard.KEY_W)) { //Move foreward
                Camera.move(new Vector3f((float) Math.sin(-Camera.getRotation().y) * speed, 0f,
                    (float) Math.cos(-Camera.getRotation().y) * speed)); 
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A)) { //Move left
                Camera.move(new Vector3f((float) Math.sin(-Camera.getRotation().y + Math.PI / 2) * speed, 0f,
                    (float) Math.cos(-Camera.getRotation().y + Math.PI / 2) * speed)); 
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S)) { //Move back
                Camera.move(new Vector3f(-(float) Math.sin(-Camera.getRotation().y) * speed, 0f,
                    -(float) Math.cos(-Camera.getRotation().y) * speed)); 
               
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D)) { //Move right
                Camera.move(new Vector3f((float) Math.sin(-Camera.getRotation().y - Math.PI / 2) * speed, 0f,
                    (float) Math.cos(-Camera.getRotation().y - Math.PI / 2) * speed)); 
            }
        }
    }
}
