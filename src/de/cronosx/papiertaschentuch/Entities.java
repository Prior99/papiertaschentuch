/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cronosx.papiertaschentuch;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

/**
 *
 * @author prior
 */
public class Entities {

	private List<Entity> entities;
	private ReentrantLock mutex;

	public Entities() {
		mutex = new ReentrantLock();
		entities = new ArrayList<>();
	}

	public void forEach(Consumer<? super Entity> it) {
		mutex.lock();
		try {
			entities.stream().forEach(it);
		}
		finally {
			mutex.unlock();
		}
	}

	public void add(Entity e) {
		mutex.lock();
		try {
			entities.add(e);
		}
		finally {
			mutex.unlock();
		}
	}
}
