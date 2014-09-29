package de.cronosx.papiertaschentuch;

import java.util.function.*;
import org.lwjgl.opengl.*;

public class Lights
{
	private static Light[] lights = new Light[8];
	
	public static Light createLight() {
		for(int i = 0; i < lights.length; i++) {
			if(lights[i] == null) {
				int id = GL11.GL_LIGHT0 + i;
				Light light = new Light(id);
				lights[i] = light;
				return light;
			}
		}
		throw new UnsupportedOperationException("Only 8 lightsource are possible at a time.");
	}
	
	public static void forEach(Consumer<? super Light> l) {
		for(int i = 0; i < lights.length; i++) {
			if(lights[i] != null) {
				l.accept(lights[i]);
			}
		}
	}
}
