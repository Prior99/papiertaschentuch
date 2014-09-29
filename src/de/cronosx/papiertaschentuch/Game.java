package de.cronosx.papiertaschentuch;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;

public class Game extends Thread {
    private List<Entity> entities;
    private Physics physics;
    
    public Game(List<Entity> entities) {
        this.entities = entities;
        physics = new Physics(entities);
        
    }
    @Override
    public void run() {
        while(!Input.isClosed()) {
            Input.tick();
            physics.tick();
            try {
                Thread.sleep(1000/60);
            } 
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
