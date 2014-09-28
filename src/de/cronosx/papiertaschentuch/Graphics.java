/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.models.Cube;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.util.glu.GLU.*;

/**
 *
 * @author Prior
 */
public class Graphics extends Thread{
	private int screenWidth, screenHeight;
	
	Texture bricks;
	Cube cube;
	float angle;
	
	public Graphics(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
	}
	
	private void setup() {
		try 
		{
			setupDisplay();
			setupMouse();
			setupGL();
		} 
		catch (LWJGLException e) 
		{
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
		glPushAttrib(GL_ENABLE_BIT|GL_TRANSFORM_BIT|GL_HINT_BIT| GL_COLOR_BUFFER_BIT|GL_SCISSOR_BIT|GL_LINE_BIT|GL_TEXTURE_BIT);
		//glEnable(GL_NORMALIZE);
		glEnable(GL_TEXTURE_2D);
		glMatrixMode(GL_PROJECTION);
		gluPerspective(45.0f, screenWidth/(float)screenHeight, 0.1f, 100.0f);
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
		glPushMatrix();
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, bricks.getTextureID());
		glTranslatef(0, 0, -6);
		glRotatef(angle, .71f, .71f, 0);
		cube.draw();
		angle += (float)Math.PI/10f;
		
		glPopMatrix();
		Display.update();
	}
	
	@Override
	public void run() {
		setup();
		cube = new Cube();
		cube.init();
		try {
			bricks = new Texture(new File("bricks.jpg"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		while(!Display.isCloseRequested()) {
			render();
		}
	}
}
