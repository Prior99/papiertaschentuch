package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.Shaders.Shader;
import java.util.*;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.util.glu.GLU.*;

public class Graphics extends Thread {

	private final int screenWidth, screenHeight;
	private final Entities entities;
	private final List<ReadyListener> readyListeners;
	private final List<GraphicsTickListener> graphicsTickListeners;
	private final List<ShutdownListener> shutdownListeners;
	private boolean exit;
	private Player player;
	private Shader defaultShader;

	public Graphics(int width, int height, Entities entities, Player player) {
		super("Graphicsthread");
		this.player = player;
		exit = false;
		this.screenWidth = width;
		this.screenHeight = height;
		this.entities = entities;
		readyListeners = new ArrayList<>();
		graphicsTickListeners = new ArrayList<>();
		shutdownListeners = new ArrayList<>();
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void onReady(ReadyListener listener) {
		this.readyListeners.add(listener);
	}

	public void onGraphicsTick(GraphicsTickListener listener) {
		this.graphicsTickListeners.add(listener);
	}

	public void onShutdown(ShutdownListener listener) {
		this.shutdownListeners.add(listener);
	}

	private void setup() {
		try {
			setupDisplay();
			setupMouse();
			setupGL();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void setupMouse() {
		Mouse.setClipMouseCoordinatesToWindow(true);
		if(Papiertaschentuch.getConfig().getBool("Grab Mouse", true)) {
			Mouse.setGrabbed(true);
		}
	}

	private void setupDisplay() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(screenWidth, screenHeight));
		Display.setVSyncEnabled(true);
		Display.create();
	}

	private void setupGL() {
		glPushAttrib(GL_ENABLE_BIT | GL_TRANSFORM_BIT | GL_HINT_BIT | GL_COLOR_BUFFER_BIT | GL_SCISSOR_BIT | GL_LINE_BIT | GL_TEXTURE_BIT);
		glEnable(GL_TEXTURE_2D);
		glMatrixMode(GL_PROJECTION);
		gluPerspective(45.0f, screenWidth / (float) screenHeight, 0.001f, 1000.0f);
		glShadeModel(GL_SMOOTH); // Enable Smooth Shading
		glClearColor(0.5f, 0.5f, 1.0f, 0.5f); // Lightblue Background
		glClearDepth(1.0f); // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST); // Enables Depth Testing
		glDisable(GL_LIGHTING);
		glDepthFunc(GL_LEQUAL); // The Type Of Depth Testing To Do
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST); // Really Nice Perspective Calculations
		glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		defaultShader = Shaders.getShader("default");
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		player.transform();
		graphicsTickListeners.stream().forEach((l) -> {
			l.onGraphicsTick();
		});
		glUseProgram(defaultShader.getID());
		entities.forEach((e) -> {
			drawEntity(e);
		});
		Display.update();
	}
	
	private void drawEntity(Entity e) {
		Model model = e.getModel();
		Texture texture = e.getTexture();
		Vector3f position = e.getPosition();
		Vector3f rotation = e.getRotation();
		if (model != null && texture != null) {
			glPushMatrix();
			glTranslatef(position.x, position.y, position.z);
			glRotatef(Graphics.radiantToDegree(rotation.x), 1.f, 0.f, 0.f);
			glRotatef(Graphics.radiantToDegree(rotation.y), 0.f, 1.f, 0.f);
			glRotatef(Graphics.radiantToDegree(rotation.z), 0.f, 0.f, 1.f);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, texture.retrieveTextureID());
			drawModel(e.getModel());
			glPopMatrix();
		}
	}
	
	private void drawModel(Model model) {
		model.loadBuffers();
		glDrawElements(GL_TRIANGLES, model.getIndexCount(), GL_UNSIGNED_INT, 0);
	}
	
	@Override
	public void run() {
		setup();
		readyListeners.stream().forEach((l) -> {
			l.onReady();
		});
		while (!Input.isClosed() && !exit) {
			try {
				render();
			} catch (Exception e) {
				System.out.println("Error in renderloop, shutting down: " + e.getMessage());
				e.printStackTrace();
				shutdown();
				break;
			}
		}
		Display.destroy();
		shutdownListeners.stream().forEach((l) -> {
			l.onShutdown();
		});
	}

	public void shutdown() {
		exit = true;
	}

	public interface ReadyListener {

		public void onReady();
	}

	public interface ShutdownListener {

		public void onShutdown();
	}

	public interface GraphicsTickListener {

		public void onGraphicsTick();
	}

	public static float degreeToRadiant(float degree) {
		return (float) ((2 * Math.PI) / 360 * degree);
	}

	public static float radiantToDegree(float degree) {
		return (float) (360 / (2 * Math.PI) * degree);
	}
}
