package de.cronosx.papiertaschentuch;

import com.bulletphysics.linearmath.*;
import de.cronosx.papiertaschentuch.EventEmitter.Listener;
import static de.cronosx.papiertaschentuch.PhysicalEntity.CollisionType.*;
import de.cronosx.papiertaschentuch.vecmath.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import javax.vecmath.*;

public class Papiertaschentuch {
	public static final int ticksPerSecond = 60;
	public static final int waitTime = 1000 / ticksPerSecond;
	public static final int msThreshold = 3;
	
	private static Papiertaschentuch instance;
	public static Config config;
	private final Game game;
	private final Graphics graphics;
	private final Physics physics;
	private final Player player;
	private final Entities entities;
	private boolean exited;
	private final EventEmitter emitter;
	private final Scripts scripts;

	public Papiertaschentuch() {
		emitter = new EventEmitter();
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
		scripts = new Scripts();
		exited = false;
		player = new Player();
		entities = new Entities();
		graphics = new Graphics(getConfig().getInt("Screen width", 800), getConfig().getInt("Screen height", 600), entities, player);
		physics = new Physics(entities, player);
		if(getConfig().getBool("Draw Physics Debug", false)) {
			DebugDrawer dDraw = new DebugDrawer();
			physics.setDebugDrawer(dDraw);
			dDraw.setDebugMode(DebugDrawModes.MAX_DEBUG_DRAW_MODE | DebugDrawModes.DRAW_AABB);
			graphics.getEmitter().on("graphicstick", () -> {
				dDraw.begin();
				physics.debugDraw();
				dDraw.end();
			});
		}
		game = new Game(entities);
		if(getConfig().getBool("Log FPS", false)) {
			AtomicInteger i = new AtomicInteger(0);
			final int tickAmount = getConfig().getInt("FPS Tick Amount", 600);
			game.getEmitter().on("gametick", () -> {
				if(i.getAndIncrement() > tickAmount) {
					Log.info("FPS in last " + tickAmount + " ticks: " + graphics.retrieveFPSSinceLastCall());
					i.set(0);
				}
			});
		}
		if(getConfig().getBool("Log TPS", false)) {
			AtomicInteger i = new AtomicInteger(0);
			final int tickAmount = getConfig().getInt("TPS Tick Amount", 600);
			game.getEmitter().on("gametick", () -> {
				if(i.getAndIncrement() > tickAmount) {
					Log.info("TPS in last " + tickAmount + " ticks: " + game.retrieveTPSSinceLastCall());
					i.set(0);
				}
			});
		}
		graphics.getEmitter().on("graphicsready", () -> {
			checkReady();
		});
		game.getEmitter().on("gameready", () -> {
			checkReady();
		});
		game.getEmitter().on("gametick", () -> {
			physics.tick();
		});
		game.getEmitter().on("gameshutdown", () -> {
			shutdown();
		});
		graphics.getEmitter().on("graphicsshutdown", () -> {
			shutdown();
		});
		this.emitter.on("ready", () -> {
			Input.start();
		});
		activatePlayer(player);
		Log.debug("Papiertaschentuch initialized.");
	}
	
	public static Papiertaschentuch getInstance() {
		return instance;
	}

	public Game getGame() {
		return game;
	}
	
	public Scripts getScripts() {
		return scripts;
	}
	
	public Graphics getGraphics() {
		return graphics;
	}
	
	public static Config getConfig() {
		return config;
	}
	
	private void checkReady() {
		if(game.isReady() && graphics.isReady()) {
			emitter.emit("ready");
		}
	}
	
	public EventEmitter getEmitter() {
		return emitter;
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
		game.start();
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
		if(config != null) {
			instance = new Papiertaschentuch();
			instance.start();
			instance.getScripts()
				.addBinding("PPTT", new JSBinding())
				.addBinding("vec2i", Vector2i.class)
				.finalizeBindings()
				.loadDirectory(getConfig().getString("Scripts folder", "scripts/"));
				
		} 
		else {
			Log.fatal("Unable to parse config. Aborting.");
		}
	}
}
