package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.Shaders.Shader;
import java.util.*;
import de.cronosx.papiertaschentuch.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import org.lwjgl.opengl.GL15;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
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
	private int framesSinceLastCall;
	private long lastCall;
	private Matrix4f modelMatrix, viewMatrix, projectionMatrix;
	private Stack<Matrix4f> modelMatrixStack;
	
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
		framesSinceLastCall = 0;
		lastCall = System.currentTimeMillis();
		modelMatrixStack = new Stack<>();
		setupMatrices();
	}
	
	private void push() {
		Matrix4f clone = new Matrix4f();
		clone.load(modelMatrix);
		modelMatrixStack.push(clone);
	}	
	
	private void pop() {
		if(!modelMatrixStack.isEmpty()) {
			modelMatrix = modelMatrixStack.pop();
		}
		else {
			throw new IllegalStateException("Trying to pop on an empty stack!");
		}
	}
	
	private void setupMatrices() {
		modelMatrix = new Matrix4f();
		modelMatrix.setIdentity();
		viewMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		projectionMatrix.setIdentity();
		projectionMatrix.setIdentity();
		projectionMatrix.perspective(45.0f, screenWidth / (float) screenHeight, 0.001f, 1000.0f);
	}
	
	public float retrieveFPSSinceLastCall() {
		long now = System.currentTimeMillis();
		long elapsed = now - lastCall;
		float fps = framesSinceLastCall / (elapsed / 1000f);
		framesSinceLastCall = 0;
		lastCall = now;
		return fps;
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
			setupShader();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private void setupShader() {
		defaultShader = Shaders.getShader("default");
		glUseProgram(defaultShader.getID());
		defaultShader.setUniform("uProjectionMatrix", projectionMatrix);
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
		glClearColor(0.5f, 0.5f, 1.0f, 0.5f); // Lightblue Background
		glClearDepth(1.0f); // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST); // Enables Depth Testing
		glDepthFunc(GL_LEQUAL); // The Type Of Depth Testing To Do
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST); // Really Nice Perspective Calculations
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Lights.forEach((l) -> {
			l.bind();
		});
		graphicsTickListeners.stream().forEach((l) -> {
			l.onGraphicsTick();
		});
		drawEntities();
		framesSinceLastCall++;
		Display.update();
	}
	
	private void drawEntities() {
		viewMatrix.setIdentity();
		viewMatrix.rotate(player.getRotation().x, new Vector3f(1.f, 0.f, 0.f));
		viewMatrix.rotate(player.getRotation().y, new Vector3f(0.f, 1.f, 0.f));
		viewMatrix.rotate(player.getRotation().z, new Vector3f(0.f, 0.f, 1.f));
		viewMatrix.translate(new Vector3f(-player.getPosition().x, -player.getPosition().y, -player.getPosition().z));
		glUseProgram(defaultShader.getID());
		defaultShader.setUniform("uViewMatrix", viewMatrix);
		entities.forEach((e) -> {
			drawEntity(e);
		});
	}
	
	private void drawEntity(Entity e) {
		Model model = e.getModel();
		Texture texture = e.getTexture();
		Vector3f position = e.getPosition();
		Vector3f rotation = e.getRotation();
		if (model != null && texture != null) {
			modelMatrix.setIdentity();
			push();
			modelMatrix.translate(position);
			modelMatrix.rotate(rotation.x, new Vector3f(1.f, 0.f, 0.f));
			modelMatrix.rotate(rotation.y, new Vector3f(0.f, 1.f, 0.f));
			modelMatrix.rotate(rotation.z, new Vector3f(0.f, 0.f, 1.f));
			defaultShader.setUniform("uSampler", 0);
			defaultShader.setUniform("uModelMatrix", modelMatrix);
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, texture.retrieveTextureID());
			drawModel(e.getModel());
			pop();
		}
	}
	
	private void drawModel(Model model) {
		defaultShader.setAttribute("aVertexPosition", model.getVertexBufferID(), 3, GL_FLOAT);
		defaultShader.setAttribute("aTextureCoord", model.getTextureBufferID(), 2, GL_FLOAT);
		defaultShader.setAttribute("aNormals", model.getNormalBufferID(), 3, GL_FLOAT);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, model.getIndexBufferID());
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
