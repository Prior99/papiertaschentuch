package de.cronosx.papiertaschentuch;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import javax.vecmath.Vector3f;

public class Entities {

	private final List<Entity> entities;
	private final ReentrantLock mutex;

	public static Entity createEntity() {
		Entity e = new Entity();
		Papiertaschentuch.getInstance().addEntity(e);
		return e;
	}
	
	public static Entity createEntity(Model model, Texture texture) {
		Entity e = new Entity(model, texture);
		Papiertaschentuch.getInstance().addEntity(e);
		return e;
	}
	
	public static PhysicalEntity createPhysicalEntity(Model model, Texture texture, float mass, PhysicalEntity.CollisionType collisionType, Vector3f boxDim) {
		PhysicalEntity pe = new PhysicalEntity(model, texture, mass, collisionType, boxDim);
		Papiertaschentuch.getInstance().addEntity(pe);
		return pe;
	}
	
	public static PhysicalEntity createPhysicalEntity(Model model, Texture texture, float mass, PhysicalEntity.CollisionType collisionType) {
		PhysicalEntity pe = new PhysicalEntity(model, texture, mass, collisionType);
		Papiertaschentuch.getInstance().addEntity(pe);
		return pe;	
	}
	
	public Entities() {
		mutex = new ReentrantLock();
		entities = new ArrayList<>();
	}

	public void forEach(Consumer<? super Entity> it) {
		mutex.lock();
		try {
			entities.stream().forEach(it);
		} finally {
			mutex.unlock();
		}
	}
	
	public void parallelForEach(Consumer<? super Entity> it) {
		mutex.lock();
		try {
			entities.parallelStream().forEach(it);
		} finally {
			mutex.unlock();
		}
	}

	public void add(Entity e) {
		mutex.lock();
		try {
			entities.add(e);
		} finally {
			mutex.unlock();
		}
	}
}
