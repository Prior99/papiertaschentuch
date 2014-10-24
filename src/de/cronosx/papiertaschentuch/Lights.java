package de.cronosx.papiertaschentuch;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import javax.vecmath.Vector3f;

public class Lights {

	private static final List<PointLight> lights = new ArrayList<>();
	private static final Sun sun = new Sun();

	public static PointLight createPointLight(Vector3f position) {
		return createPointLight()
			.setPosition(position);
	}
	
	public static PointLight createPointLight(Vector3f position, Vector3f color) {
		return createPointLight()
			.setPosition(position)
			.setColor(color);
	}
	
	public static PointLight createPointLight(Vector3f position, Vector3f color, float strength) {
		return createPointLight()
			.setPosition(position)
			.setColor(color)
			.setStrength(strength);
	}
	
	public static PointLight createPointLight() {
		if(lights.size() < 64) {
			PointLight light = new PointLight(lights.size());
			lights.add(light);
			return light;
		}
		else {
			throw new UnsupportedOperationException("Can not create more than 64 lights.");
		}
	}
	
	public static int getAmount() {
		return lights.size();
	}
	
	public static Sun getSun() {
		return sun;
	}

	public static Stream<PointLight> stream() {
		return lights.stream();
	}
	
	public static class Sun implements SceneObject, Lightsource {
		private Vector3f position;
		private Vector3f color;
		private Vector3f ambientColor;
		private boolean changed;
		private Entity debugEntity;
		
		public Sun() {
			position = new Vector3f(1000.f, 1000.f, 1000.f);
			color = new Vector3f(1.f, 1.f, 1.f);
			ambientColor = new Vector3f(0.f, 0.f, 0.f);
			changed = true;
			if(Papiertaschentuch.getConfig().getBool("Debug Lights", false)) {
				Entity e = Entities.createEntity();
				setDebugEntity(e);
			}
		}
	
		private void setDebugEntity(Entity entity) {
			this.debugEntity = entity;
			try {
				entity.setModel(Models.getModel("models/lightsource.obj"));
				entity.setTexture(Textures.getTexture("textures/lightsource.png"));
				entity.deactivateLighting();
				entity.deactivateDepthBuffer();
			}
			catch(IOException e) {
				Log.fatal("Model \"models/lightsource.obj\" or texture \"textures/lightsource.png\" not found. unable to debug lightsources.");
			}
		}
		
		@Override
		public Sun setColor(Vector3f v) {
			this.color = v;
			changed = true;
			return this;
		}
		
		public Sun setAmbientColor(Vector3f v) {
			this.ambientColor = v;
			changed = true;
			return this;
		}
		
		@Override
		public Sun setPosition(Vector3f v) {
			this.position = v;
			changed = true;
			if(debugEntity != null) {
				debugEntity.setPosition(position);
			}
			return this;
		}
		
		@Override
		public Vector3f getPosition() {
			return position;
		}
		
		@Override
		public Vector3f getColor() {
			return color;
		}
		
		public Vector3f getAmbientColor() {
			return ambientColor;
		}
		
		public boolean checkChanged() {
			boolean ch = changed;
			changed = false;
			return ch;
		}
		
		@Override
		public Sun move(Vector3f delta) {
			setPosition(new Vector3f(position.x + delta.x, position.y + delta.y, position.z + delta.z));
			return this;
		}

		@Override
		public SceneObject setRotation(Vector3f rotation) { return this; }

		@Override
		public Vector3f getRotation() { return new Vector3f(0.f, 0.f, 0.f); }
	}
}
