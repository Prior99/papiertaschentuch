package de.cronosx.papiertaschentuch;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import javax.vecmath.Vector3f;

public class Input {
    private static final float speed = .1f,
        turnSpeedFactor = .1f;
    private static float joggingFactor = .25f,
        joggingScale = .05f,
        joggingAngle = 0;
    private static boolean closed = false;
    public static void tick() {
        if(Display.isActive()) {
            boolean moved = false;
            if(Keyboard.isKeyDown(Keyboard.KEY_W)) { //Move forward
                moved = true;
                Camera.move(new Vector3f(
                    (float) Math.sin(-degToRad(Camera.getRotation().y)) * speed, 0f,
                    (float) Math.cos(-degToRad(Camera.getRotation().y)) * speed)); 
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S)) { //Move back
                moved = true;
                Camera.move(new Vector3f(
                    -(float) Math.sin(-degToRad(Camera.getRotation().y)) * speed, 0f,
                    -(float) Math.cos(-degToRad(Camera.getRotation().y)) * speed)); 
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A)) { //Move left
                moved = true;
                Camera.move(new Vector3f(
                    (float) Math.sin(-degToRad(Camera.getRotation().y - 90)) * speed, 0f,
                    (float) Math.cos(-degToRad(Camera.getRotation().y - 90)) * speed)); 
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D)) { //Move right
                moved = true;
                Camera.move(new Vector3f(
                    (float) Math.sin(-degToRad(Camera.getRotation().y + 90)) * speed, 0f,
                    (float) Math.cos(-degToRad(Camera.getRotation().y + 90)) * speed)); 
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                closed = true;
            }
            /*if(moved) {
                Camera.setPosition(new Vector3f(Camera.getPosition().x, 
                    (float)(Math.sin(joggingAngle += joggingFactor) * joggingScale),
                    Camera.getPosition().z));
            }*/
            int dx = Mouse.getDX(),
                dy = Mouse.getDY();
            Camera.rotate(new Vector3f(-dy * turnSpeedFactor, dx * turnSpeedFactor, 0f));
        }
    }
        
    public static boolean isClosed() {
        return closed;
    }
	
    private static float degToRad(float degree) {
        return (float)((2 * Math.PI) / 360 * degree);
    }
}
