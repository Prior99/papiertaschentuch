package de.cronosx.papiertaschentuch;

import java.util.*;

public class Game extends Thread {

	private Entities entities;
	private List<TickListener> tickListeners;

	public Game(Entities entities) {
		tickListeners = new ArrayList<>();
		this.entities = entities;
	}

	@Override
	public void run() {
		while(!Input.isClosed()) {
			Input.tick();
			tickListeners.stream().forEach((l) -> {
				l.onTick();
			});
			try {
				Thread.sleep(1000 / 60);
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void onTick(TickListener l) {
		tickListeners.add(l);
	}

	public interface TickListener {

		public void onTick();
	}
}
