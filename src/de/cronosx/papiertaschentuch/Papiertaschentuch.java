package de.cronosx.papiertaschentuch;

import com.bulletphysics.linearmath.*;
import static de.cronosx.papiertaschentuch.Entity.CollisionType.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.*;
import javax.vecmath.Vector3f;

public class Papiertaschentuch {

	public static Config config;
	private final Game game;
	private final Graphics graphics;
	private final Physics physics;
	private final Player player;
	private final Entities entities;
	private boolean exited;

	public Game getGame() {
		return game;
	}
	
	public Graphics getGraphics() {
		return graphics;
	}
	
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
		if(getConfig().getBool("Draw Physics Debug", false)) {
			DebugDrawer dDraw = new DebugDrawer();
			physics.setDebugDrawer(dDraw);
			dDraw.setDebugMode(DebugDrawModes.MAX_DEBUG_DRAW_MODE | DebugDrawModes.DRAW_AABB);
			graphics.onGraphicsTick(() -> {
				dDraw.begin();
				physics.debugDraw();
				dDraw.end();
			});
		}
		game = new Game(entities);
		if(getConfig().getBool("Log FPS", false)) {
			AtomicInteger i = new AtomicInteger(0);
			final int tickAmount = getConfig().getInt("FPS Tick Amount", 600);
			game.onTick(() -> {
				if(i.getAndIncrement() > tickAmount) {
					Log.info("FPS in last " + tickAmount + " ticks: " + graphics.retrieveFPSSinceLastCall());
					i.set(0);
				}
			});
		}
		if(getConfig().getBool("Log TPS", false)) {
			AtomicInteger i = new AtomicInteger(0);
			final int tickAmount = getConfig().getInt("TPS Tick Amount", 600);
			game.onTick(() -> {
				if(i.getAndIncrement() > tickAmount) {
					Log.info("TPS in last " + tickAmount + " ticks: " + game.retrieveTPSSinceLastCall());
					i.set(0);
				}
			});
		}
		graphics.onReady(() -> {
			game.start();
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
			Entity room = new Entity(Models.getModel("cube_world.obj"), Textures.getTexture("groundrocks.png"), 0, CONCAVE);
			p.addEntity(room);
			p.start();
			Light l = Lights.createLight();
			l.setPosition(new Vector3f(0, 0, 0));
			l.setColor(new Vector3f(1.f, 1.f, 1.f));
			final AtomicInteger i = new AtomicInteger(0);
			p.getGame().onTick(() -> {
				if(i.incrementAndGet() < 100) {
					Entity cube = new Entity(Models.getModel("crate.obj"), Textures.getTexture("crate.png"), 50f, BOX, new Vector3f(.5f, .5f, .5f));
					p.addEntity(cube);
				}
			});
		} else {
			Log.fatal("Unable to parse config. Aborting.");
		}
	}
}
