package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.models.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Vector3f;

public class Papiertaschentuch {

    private final Game game;
    private final Graphics graphics;
    private final Physics physics;
    private Entities entities;

    public Papiertaschentuch() {
	entities = new Entities();
	graphics = new Graphics(800, 600, entities);
	physics = new Physics(entities);
	game = new Game(entities);
	graphics.onReady(() -> {
	    game.start();
	});
	game.onTick(() -> {
	    physics.tick();
	    if (Math.random() < .05) {
		Entity e = new Entity(Models.getModel("cube.obj"), Textures.getTexture("bricks.png"), 10);
		e.setPosition(new Vector3f(0, 10f, 0));
		addEntity(e);
	    }
	});
    }

    public void addEntity(Entity e) {
	entities.add(e);
	physics.addEntity(e);
    }

    public void start() {
	graphics.start();
    }

    public static void main(String[] args) {
	Papiertaschentuch p = new Papiertaschentuch();
	//Entity plane = new Entity(Models.getGround(), Textures.getTexture("tile.png"), 0);
	//p.addEntity(plane);
	Camera.setPosition(new Vector3f(0, -2f, 0));
	p.start();
	Light l = Lights.createLight();
	l.setPosition(new Vector3f(0f, 3f, 0f));
    }
}
