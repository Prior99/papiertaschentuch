package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.EventEmitter.Listener;
import static de.cronosx.papiertaschentuch.Papiertaschentuch.*;
import java.util.*;

public class Game extends Thread {
	
	private Entities entities;
	private boolean exit;
	private int ticksSinceLastCall;
	private long lastCall;
	private EventEmitter emitter;
	private boolean ready;
	
	public Game(Entities entities) {
		super("Gamethread");
		emitter = new EventEmitter();
		this.entities = entities;
		exit = false;
		ticksSinceLastCall = 0;
		lastCall = System.currentTimeMillis();
		ready = false;
	}
	
	public EventEmitter getEmitter() {
		return emitter;
	}

	public float retrieveTPSSinceLastCall() {
		long now = System.currentTimeMillis();
		long elapsed = now - lastCall;
		float tps = ticksSinceLastCall / (elapsed / 1000f);
		ticksSinceLastCall = 0;
		lastCall = now;
		return tps;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	@Override
	public void run() {
		ready = true;
		emitter.emit("gameready");
		while (!Input.isClosed() && !exit) {
			long start = System.currentTimeMillis();
			Input.tick();
			ticksSinceLastCall++;
			try {
				emitter.emit("gametick");
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
		emitter.emit("gameshutdown");
	}

	public void shutdown() {
		exit = true;
	}
}
