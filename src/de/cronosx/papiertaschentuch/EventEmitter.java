package de.cronosx.papiertaschentuch;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class EventEmitter {
	private final ReentrantLock mutex;
	private final Map<String, List<Listener>> map;
	
	
	public EventEmitter() {
		mutex = new ReentrantLock();
		map = new HashMap<>();
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
		} 
		finally {
			mutex.unlock();
		}
	}
	
	public interface Listener {
		public void invoke();
	}
}
