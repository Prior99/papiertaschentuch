package de.cronosx.papiertaschentuch;

import static de.cronosx.papiertaschentuch.Entity.CollisionType.CONCAVE;
import static de.cronosx.papiertaschentuch.Entity.CollisionType.CONVEX;
import javax.vecmath.Vector3f;

public class Papiertaschentuch {

	private final Game game;
	private final Graphics graphics;
	private final Physics physics;
	private final Player player;
	private final Entities entities;

	public Papiertaschentuch() {
		player = new Player();
		entities = new Entities();
		graphics = new Graphics(1440, 1024, entities, player);
		physics = new Physics(entities, player);
		game = new Game(entities);
		graphics.onReady(() -> {
			game.start();
		});
		game.onTick(()->{
			if(Math.random() < 0.01) {
				Entity e = new Entity(Models.getModel("cube.obj"), Textures.getTexture("wood.png"), 100, CONVEX);
				e.setPosition(new Vector3f(0, 0f, 0));
				addEntity(e);
			}
			physics.tick();
		});
		game.onShutdown(() -> {
			graphics.shutdown();
		});
		graphics.onShutdown(() -> {
			game.shutdown();
		});
		activatePlayer(player);
	}
	
	public void activatePlayer(Player player) {
		Input.setPlayerObject(player);
		graphics.setPlayer(player);
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
		Entity plane = new Entity(Models.getModel("cube_world.obj"), Textures.getTexture("tile.png"), 0, CONCAVE);
		p.addEntity(plane);
		p.start();
		Light l = Lights.createLight();
		l.setPosition(new Vector3f(0, 0, 3));
		l = Lights.createLight();
		l.setPosition(new Vector3f(0, 0, -3));
	}
}
