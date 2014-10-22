package de.cronosx.papiertaschentuch;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import javax.vecmath.Vector3f;

public class Input {

	private static float turnSpeedFactor = .003f;
	private static boolean closed = false;

	private static Player player;
	private static boolean started = false;
	
	public static void start() {
		Mouse.setClipMouseCoordinatesToWindow(true);
		if(Papiertaschentuch.getConfig().getBool("Grab Mouse", true)) {
			Mouse.setGrabbed(true);
		}
		started = true;
	}

	public static void setPlayerObject(Player player) {
		Input.player = player;
	}

	public static void tick() {
		if(player != null && started) {
			Vector3f walkDirection = new Vector3f(0, 0, 0);
			if (Keyboard.isKeyDown(Keyboard.KEY_W)) { //Move forward
				walkDirection.add(new Vector3f(
						(float) Math.sin(-player.getRotation().y), 0f,
						(float) Math.cos(-player.getRotation().y)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_S)) { //Move back
				walkDirection.add(new Vector3f(
						-(float) Math.sin(-player.getRotation().y), 0f,
						-(float) Math.cos(-player.getRotation().y)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) { //Move left
				walkDirection.add(new Vector3f(
						(float) Math.sin(-player.getRotation().y + Math.PI / 2), 0f,
						(float) Math.cos(-player.getRotation().y + Math.PI / 2)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) { //Move right
				walkDirection.add(new Vector3f(
						(float) Math.sin(-player.getRotation().y - Math.PI / 2), 0f,
						(float) Math.cos(-player.getRotation().y - Math.PI / 2)));
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { //Jump
				player.jump();
			}
			player.setWalkDirection(walkDirection);
			int dx = Mouse.getDX(),
					dy = Mouse.getDY();
			player.rotate(new Vector3f(-dy * turnSpeedFactor, dx * turnSpeedFactor, 0f));
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				closed = true;
			}
		}
	}

	public static boolean isClosed() {
		return closed;
	}
}
