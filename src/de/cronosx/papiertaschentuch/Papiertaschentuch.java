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
			for(int i = -6; i < 6; i+=2) {
				Entity barrel = new Entity(Models.getModel("barrel.obj"), Textures.getTexture("barrel_nuclear_waste_damaged.png"), 100, CONVEX);
				barrel.setPosition(new Vector3f(i, 2, 0));
				addEntity(barrel);
			} 
			for(float y = 0f; y < 6; y+= 1.) {
				Entity cube = new Entity(Models.getModel("cube.obj"), Textures.getTexture("wood.png"), 50000, CONVEX);
				cube.setPosition(new Vector3f(0, y, 5));
				addEntity(cube);
			} 
		});
		game.onTick(()->{
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
		Entity room = new Entity(Models.getModel("simple_environement.obj"), Textures.getTexture("bricks.png"), 0, CONCAVE);
		p.addEntity(room);
		p.start();
		Light l = Lights.createLight();
		l.setPosition(new Vector3f(0, 20, 0));
	}
}
