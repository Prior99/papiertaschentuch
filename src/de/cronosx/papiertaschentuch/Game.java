package de.cronosx.papiertaschentuch;

import java.util.*;

public class Game extends Thread {
	private static final int ticksPerSecond = 60;
	private static final int waitTime = 1000 / ticksPerSecond;
	private static final int msThreshold = 3;
	
	private Entities entities;
	private List<TickListener> tickListeners;
	private List<ShutdownListener> shutdownListeners;
	private boolean exit;
	private int ticksSinceLastCall;
	private long lastCall;

	public Game(Entities entities) {
		super("Gamethread");
		tickListeners = new ArrayList<>();
		shutdownListeners = new ArrayList<>();
		this.entities = entities;
		exit = false;
		ticksSinceLastCall = 0;
		lastCall = System.currentTimeMillis();
	}

	public float retrieveTPSSinceLastCall() {
		long now = System.currentTimeMillis();
		long elapsed = now - lastCall;
		float tps = ticksSinceLastCall / (elapsed / 1000f);
		ticksSinceLastCall = 0;
		lastCall = now;
		return tps;
	}
	
	@Override
	public void run() {
		while (!Input.isClosed() && !exit) {
			long start = System.currentTimeMillis();
			Input.tick();
			ticksSinceLastCall++;
			try {
				tickListeners.stream().forEach((l) -> {
					l.onTick();
				});
			} 
			catch (Exception e) {
				System.out.println("Error in gameloop, shutting down: " + e.getMessage());
				e.printStackTrace();
				shutdown();
				break;
			}
			long elapsed = System.currentTimeMillis() - start;
			if(elapsed < waitTime) {
				try {
					Thread.sleep(waitTime - elapsed);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if(elapsed - waitTime > msThreshold) {
				Log.warn("Could not keep tickrate up. " + (elapsed - waitTime) + "ms (That's more than " + msThreshold + " ms) behind!");
			}
		}
		shutdownListeners.stream().forEach((l) -> {
			l.onShutdown();
		});
	}

	public void shutdown() {
		exit = true;
	}

	public void onTick(TickListener l) {
		tickListeners.add(l);
	}

	public void onShutdown(ShutdownListener l) {
		shutdownListeners.add(l);
	}

	public interface TickListener {

		public void onTick();
	}

	public interface ShutdownListener {

		public void onShutdown();
	}
}
