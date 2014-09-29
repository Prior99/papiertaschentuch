package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.models.*;

public class Models
{
	private static Cube cube;
	private static Ground ground;
	
	public static Cube getCube() {
		if(cube == null) {
			cube = new Cube();
		}
		return cube;
	}
	
	public static Ground getGround() {
		if(ground == null) {
			ground = new Ground();
		}
		return ground;
	}
}
