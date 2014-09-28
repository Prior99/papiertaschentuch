package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.models.Cube;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Papiertaschentuch {

    public static void main(String[] args) {
        
        List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());
        Entity e = new Entity(new Cube(), Textures.getTexture("bricks.png"));
        entities.add(e);
        Camera.move(new Vector3f(0, 0, -4));
        
        Thread t = new Thread(() -> { 
            while(true) {
                e.rotate(new Vector3f(0f, -(float)Math.PI/20f, 0f));
                try {
                    Thread.sleep(1000/60);
                } 
                catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();
        Graphics g = new Graphics(800, 600, entities);
        g.start();
    }

    private static Vector3f Vector3f(float f, float f0, float f1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
