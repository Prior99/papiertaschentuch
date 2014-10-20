package de.cronosx.papiertaschentuch;

import com.bulletphysics.linearmath.*;
import static de.cronosx.papiertaschentuch.PhysicalEntity.CollisionType.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.*;
import javax.vecmath.Vector3f;

public class Papiertaschentuch {
	private static Papiertaschentuch instance;
	public static Config config;
	private final Game game;
	private final Graphics graphics;
	private final Physics physics;
	private final Player player;
	private final Entities entities;
	private boolean exited;
	
	public static Papiertaschentuch getInstance() {
		return instance;
	}

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
			if(graphics != null) graphics.shutdown();
			if(game != null) game.shutdown();
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
		} 
		catch (FileNotFoundException e) {
			Log.warn("Unable to open configfile: " + e.getMessage());
		} 
		catch (IOException e) {
			Log.warn("Unable to read configfile. An IOException occured: " + e.getMessage());
		}
		if (config != null) {
			instance = new Papiertaschentuch();
			PhysicalEntity room = new PhysicalEntity(Models.getModel("models/cube_world.obj"), Textures.getTexture("textures/groundrocks.png"), 0, CONCAVE);
			instance.addEntity(room);
			instance.start();
			instance.getGraphics().getGUI().addText("Hello, world!", 10, 30);
			Light l = Lights.createLight();
			l.setPosition(new Vector3f(5, -5, 0));
			l.setColor(new Vector3f(1.f, 1.f, 1.f));
			l = Lights.createLight();
			l.setPosition(new Vector3f(-5, -5, 0));
			l.setColor(new Vector3f(.5f, .5f, 1.f));
			final AtomicInteger i = new AtomicInteger(0);
			instance.getGame().onTick(() -> {
				if(i.incrementAndGet() < 100) {
					PhysicalEntity cube = new PhysicalEntity(Models.getModel("models/crate.obj"), Textures.getTexture("textures/crate.png"), 50000f, BOX, new Vector3f(.5f, .5f, .5f));
					instance.addEntity(cube);
				}
			});
		} else {
			Log.fatal("Unable to parse config. Aborting.");
		}
	}
}
