package de.cronosx.papiertaschentuch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.*;
import de.cronosx.papiertaschentuch.vecmath.Matrix4f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;

public class Shaders {

	private static final Map<String, Shader> shaders = new HashMap<>();

	public static Shader getShader(String filename) {
		if (shaders.containsKey(filename)) {
			return shaders.get(filename);
		} else {
			Shader shader = new Shader(filename);
			shaders.put(filename, shader);
			return shader;
		}
	}
	
	public static class Shader {

		private final int id;
		private final Map<String, Integer> uniformLocations;
		private final Map<String, Integer> attributeLocations;

		public Shader(String filename) {
			uniformLocations = new HashMap<>();
			attributeLocations = new HashMap<>();
			id = glCreateProgram();
			try {
				int vertID = loadShader("shader/" + filename + ".vert", GL_VERTEX_SHADER);
				int fragID = loadShader("shader/" + filename + ".frag", GL_FRAGMENT_SHADER);
				glAttachShader(id, vertID);
				glAttachShader(id, fragID);
				glLinkProgram(id);
				String log = glGetProgramInfoLog(id, 128000);
				if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
					Log.fatal("Error linking shaders of \"" + filename + "\" together, please see the following log:\n" + log);
				}
				glValidateProgram(id);
				if (glGetProgrami(id, GL_VALIDATE_STATUS) == GL_FALSE) {
					Log.fatal("Error validating shader \"" + filename + "\", please see the following log:\n" + log);
				}
			} catch (FileNotFoundException e) {
				Log.fatal("Could not open file for shader \"" + filename + "\": " + e.getMessage());
			} catch (IOException e) {
				Log.fatal("Unable to read file for shader \"" + filename + "\": " + e.getMessage());
			} catch (IllegalStateException e) {
				Log.fatal("Could not compile shader \"" + filename + "\": " + e.getMessage());
			}
		}
		
		public void setAttribute(String name, int arrayID, int elemSize, int type) {
			if(!attributeLocations.containsKey(name)) {
				int address = enableAttribute(name);
				attributeLocations.put(name, address);
			}
			glBindBuffer(GL_ARRAY_BUFFER, arrayID);
			glVertexAttribPointer(attributeLocations.get(name), elemSize, type, false, 0, 0);
		}
		
		private int enableAttribute(String name) {
			int attribLocation = glGetAttribLocation(getID(), name);
			glEnableVertexAttribArray(attribLocation);
			return attribLocation;
		}
		
		public void setUniform(String name, Vector3f param) {
			if(!uniformLocations.containsKey(name))  {
				int address = glGetUniformLocation(getID(), name);
				uniformLocations.put(name, address);
			}
			glUniform3f(uniformLocations.get(name), param.x, param.y, param.z);
		}
		
		public void setUniform(String name, Vector4f param) {
			if(!uniformLocations.containsKey(name))  {
				int address = glGetUniformLocation(getID(), name);
				uniformLocations.put(name, address);
			}
			glUniform4f(uniformLocations.get(name), param.x, param.y, param.z, param.w);
		}
		
		public void setUniform(String name, Matrix4f param) {
			if(!uniformLocations.containsKey(name))  {
				int address = glGetUniformLocation(getID(), name);
				uniformLocations.put(name, address);
			}
			FloatBuffer matrix = BufferUtils.createFloatBuffer(4 * 4);
			matrix.put(param.m00); matrix.put(param.m01); matrix.put(param.m02); matrix.put(param.m03);
			matrix.put(param.m10); matrix.put(param.m11); matrix.put(param.m12); matrix.put(param.m13);
			matrix.put(param.m20); matrix.put(param.m21); matrix.put(param.m22); matrix.put(param.m23);
			matrix.put(param.m30); matrix.put(param.m31); matrix.put(param.m32); matrix.put(param.m33);			
			matrix.rewind();
			glUniformMatrix4(uniformLocations.get(name), false, matrix);
		}
		
		public void setUniform(String name, float param) {
			if(!uniformLocations.containsKey(name)) {
				int address = glGetUniformLocation(getID(), name);
				uniformLocations.put(name, address);
			}
			glUniform1f(uniformLocations.get(name), param);
		}

		public void setUniform(String name, int param) {
			if(!uniformLocations.containsKey(name)) {
				int address = glGetUniformLocation(getID(), name);
				uniformLocations.put(name, address);
			}
			glUniform1i(uniformLocations.get(name), param);
		}

		public void setUniform(String name, boolean param) {
			if(!uniformLocations.containsKey(name)) {
				int address = glGetUniformLocation(getID(), name);
				uniformLocations.put(name, address);
			}
			glUniform1i(uniformLocations.get(name), param ? 1 : 0);
		}

		public int getID() {
			return id;
		}

		private int loadShader(String filename, int type) throws FileNotFoundException, IOException {
			int handle = glCreateShader(type);
			File f = new File(filename);
			if (!f.exists()) {
				throw new FileNotFoundException();
			} else {
				try {
					String line;
					StringBuilder sb = new StringBuilder();
					BufferedReader reader = new BufferedReader(new FileReader(f));
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
					glShaderSource(handle, sb.toString());
					glCompileShader(handle);
					int status = glGetShaderi(handle, GL_COMPILE_STATUS);
					String log = GL20.glGetShaderInfoLog(handle, 128000);
					if (status == GL_FALSE) {
						throw new IllegalStateException("Unable to compile Shader \"" + filename + "\", the log is as follows:\n" + log);
					} else {
						Log.debug("Successfully compiled shader \"" + filename + "\", the log is as follows:\n" + log);
					}
					return handle;
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}
}
