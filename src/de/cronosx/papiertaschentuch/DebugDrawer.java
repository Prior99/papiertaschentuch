

package de.cronosx.papiertaschentuch;

import static com.bulletphysics.demos.opengl.IGL.GL_LINES;
import static com.bulletphysics.demos.opengl.IGL.GL_POINTS;
import com.bulletphysics.linearmath.*;
import javax.vecmath.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class DebugDrawer extends IDebugDraw{
	private int debugMode;
	
	public DebugDrawer() {
	}
	
	public void begin() {
		glUseProgram(0);
		glLineWidth(4.f);
	}
	
	public void end() {
	}
	
	@Override
	public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
		try {
			glColor3f(color.x, color.y, color.z);
			glBegin(GL_LINES);
			glVertex3f(from.x, from.y, from.z);
			glVertex3f(to.x, to.y, to.z);
			glEnd();
		}
		catch(RuntimeException e) {
			
		}
	}

	@Override
	public void drawContactPoint(Vector3f pointOnB, Vector3f normalOnB, float distance, int lifeTime, Vector3f color) {
		/*Vector3f to = new Vector3f();
		to.scaleAdd(distance * 100f, normalOnB, pointOnB);
		Vector3f from = pointOnB;

		to.normalize(normalOnB);
		to.scale(10f);
		to.add(pointOnB);
		glLineWidth(3f);
		glPointSize(6f);
		glBegin(GL_POINTS);
		glColor3f(color.x, color.y, color.z);
		glVertex3f(from.x, from.y, from.z);
		glEnd();
		
		glBegin(GL_LINES);
		glColor3f(color.x, color.y, color.z);
		glVertex3f(from.x, from.y, from.z);
		glVertex3f(to.x, to.y, to.z);
		glEnd();
		
		glLineWidth(1f);
		glPointSize(1f);*/
	}

	@Override
	public void reportErrorWarning(String arg0) {
		Log.error(arg0);
	}

	@Override
	public void draw3dText(Vector3f arg0, String arg1) {
	}

	@Override
	public void setDebugMode(int arg0) {
		debugMode = arg0;
	}

	@Override
	public int getDebugMode() {
		return debugMode;
	}
}
