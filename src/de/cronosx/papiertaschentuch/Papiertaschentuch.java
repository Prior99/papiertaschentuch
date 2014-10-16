package de.cronosx.papiertaschentuch;

import static de.cronosx.papiertaschentuch.Entity.CollisionType.CONCAVE;
import static de.cronosx.papiertaschentuch.Entity.CollisionType.CONVEX;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.vecmath.Vector3f;

public class Papiertaschentuch {

	public static Config config;
	private final Game game;
	private final Graphics graphics;
	private final Physics physics;
	private final Player player;
	private final Entities entities;
	private boolean exited;

	public static Config getConfig() {
		return config;
	}

	public Papiertaschentuch() {
		Log.onError((msg) -> {
			if (getConfig().getBool("Exit on errors", true)) {
				Log.debug("An error occured. Shutting down.");
				shutdown();
			}
		});
		Log.onFatal((msg) -> {
			if (getConfig().getBool("Exit on fatal errors", true)) {
				Log.debug("A fatal error occured. Shutting down.");
				shutdown();
			}
		});
		exited = false;
		player = new Player();
		entities = new Entities();
		graphics = new Graphics(getConfig().getInt("Screen width", 800), getConfig().getInt("Screen height", 600), entities, player);
		physics = new Physics(entities, player);
		game = new Game(entities);
		graphics.onReady(() -> {
			game.start();
			for (int i = -6; i < 6; i += 2) {
				Entity barrel = new Entity(Models.getModel("barrel.obj"), Textures.getTexture("barrel_nuclear_waste_damaged.png"), 100, CONVEX);
				barrel.setPosition(new Vector3f(i, 2, 0));
				addEntity(barrel);
			}
			for (float y = 0f; y < 6; y += 1.) {
				Entity cube = new Entity(Models.getModel("cube.obj"), Textures.getTexture("wood.png"), 50000, CONVEX);
				cube.setPosition(new Vector3f(0, y, 5));
				addEntity(cube);
			}
		});
		game.onTick(() -> {
			physics.tick();
		});
		game.onShutdown(() -> {
			shutdown();
		});
		graphics.onShutdown(() -> {
			shutdown();
		});
		activatePlayer(player);
		Log.debug("Papiertaschentuch initialized.");
	}

	private void shutdown() {
		if (!exited) {
			exited = true;
			Log.debug("Shutting down.");
			graphics.shutdown();
			game.shutdown();
			Log.debug("Good bye!");
			Log.shutdown();
		}
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
		config = null;
		try {
			config = new Config("engine.cfg");
			config.parse();
		} catch (FileNotFoundException e) {
			Log.warn("Unable to open configfile: " + e.getMessage());
		} catch (IOException e) {
			Log.warn("Unable to read configfile. An IOException occured: " + e.getMessage());
		}
		if (config != null) {
			Papiertaschentuch p = new Papiertaschentuch();
			Entity room = new Entity(Models.getModel("simple_environement.obj"), Textures.getTexture("bricks.png"), 0, CONCAVE);
			p.addEntity(room);
			p.start();
			Light l = Lights.createLight();
			l.setPosition(new Vector3f(0, 20, 0));
		} else {
			Log.fatal("Unable to parse config. Aborting.");
		}
	}
}
