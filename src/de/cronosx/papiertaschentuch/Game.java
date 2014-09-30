package de.cronosx.papiertaschentuch;

import java.util.*;

public class Game extends Thread {

	private Entities entities;
	private List<TickListener> tickListeners;
	private List<ShutdownListener> shutdownListeners;
	private boolean exit;
	
	public Game(Entities entities) {
		super("Gamethread");
		tickListeners = new ArrayList<>();
		shutdownListeners = new ArrayList<>();
		this.entities = entities;
		exit = false;
	}

	@Override
	public void run() {
		while(!Input.isClosed() && !exit) {
			Input.tick();
			try {
				tickListeners.stream().forEach((l) -> {
					l.onTick();
				});
			}
			catch(Exception e) {
				System.out.println("Error in gameloop, shutting down: " + e.getMessage());
				e.printStackTrace();
				shutdown();
				break;
			}
			try {
				Thread.sleep(1000 / 60);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
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
