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
        final Graphics graphics = new Graphics(800, 600, entities);
        final Game game = new Game(entities);
        graphics.onReady(() -> {
            game.start();
        });
        graphics.start();
    }
}
