package de.cronosx.papiertaschentuch;

import javax.vecmath.*;

public interface Lightsource {
	
	public Vector3f getColor();
	
	public Lightsource setColor(Vector3f color);
	
}
