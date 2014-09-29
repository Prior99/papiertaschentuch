package de.cronosx.papiertaschentuch;

import java.util.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class Graphics extends Thread {

    private int screenWidth, screenHeight;
    private Entities entities;
    private List<ReadyListener> readyListeners;
    private List<GraphicsTickListener> graphicsTickListeners;
    private List<ShutdownListener> shutdownListeners;

    public Graphics(int width, int height, Entities entities) {
        this.screenWidth = width;
        this.screenHeight = height;
        this.entities = entities;
		readyListeners = new ArrayList<>();
		graphicsTickListeners = new ArrayList<>();
		shutdownListeners = new ArrayList<>();
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
        Mouse.setGrabbed(true);
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
        gluPerspective(45.0f, screenWidth / (float) screenHeight, 0.1f, 100.0f);
        glShadeModel(GL_SMOOTH); // Enable Smooth Shading
        glClearColor(0.5f, 0.5f, 1.0f, 0.5f); // Lightblue Background
        glClearDepth(1.0f); // Depth Buffer Setup
        glEnable(GL_DEPTH_TEST); // Enables Depth Testing
        //glEnable(GL_LIGHTING);
        glDepthFunc(GL_LEQUAL); // The Type Of Depth Testing To Do
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST); // Really Nice Perspective Calculations
        glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glEnableClientState(GL_NORMAL_ARRAY);
        glEnableClientState(GL_VERTEX_ARRAY);
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        Camera.transform();
		graphicsTickListeners.stream().forEach((l) -> {
			l.onGraphicsTick();
		});
		entities.forEach((e) -> {
			e.draw();
		});
        Display.update();
    }

    @Override
    public void run() {
        setup();
		readyListeners.stream().forEach((l) -> {
			l.onReady();
		});
        while(!Input.isClosed()) {
            render();
        }
		shutdownListeners.stream().forEach((l) -> {
			l.onShutdown();
		});
        Display.destroy();
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
        return (float)((2 * Math.PI) / 360 * degree);
    }
	
    public static float radiantToDegree(float degree) {
        return (float)(360 / (2 * Math.PI) * degree);
    }
}
