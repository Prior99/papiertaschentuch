package de.cronosx.papiertaschentuch;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class EventEmitter {
	private final ReentrantLock mutex;
	private final Map<String, List<Listener>> map;
	private final List<EventEmitter> children;
	
	
	public EventEmitter() {
		children = new ArrayList<>();
		mutex = new ReentrantLock();
		map = new HashMap<>();
	}
	
	public void addChild(EventEmitter emitter) {
		mutex.lock();
		try {
			children.add(emitter);
		}
		finally {
			mutex.unlock();
		}
	}
	
	public void on(String string, Listener l) {
		mutex.lock();
		try {
			if(!map.containsKey(string)) {
				map.put(string, new ArrayList<>());
			}
			map.get(string).add(l);
		} 
		finally {
			mutex.unlock();
		}
	}
	
	public void emit(String string) {
		mutex.lock();
		try {
			if(map.containsKey(string)) {
				List<Listener> l = map.get(string);
				l.stream().forEach((listener) -> {
					listener.invoke();
				});
			}
			children.stream().forEach((emitter) -> {
				emitter.emit(string);
			});
		} 
		finally {
			mutex.unlock();
		}
	}
	
	public interface Listener {
		public void invoke();
	}
}
