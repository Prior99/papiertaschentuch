package de.cronosx.papiertaschentuch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import org.lwjgl.opengl.GL20;
import static org.lwjgl.opengl.GL20.*;

public class Shaders {
    private static final Map<String, Shader> shaders = new HashMap<>();
    
    public static Shader getShader(String filename) {
	if(shaders.containsKey(filename)) {
	    return shaders.get(filename);
	}
	else {
	    Shader shader = new Shader(filename);
	    shaders.put(filename, shader);
	    return shader;
	}
    }
    
    public static class Shader {
	private int id;
	public Shader(String filename) {
	    id = glCreateProgram();
	    try {
		int vertID = loadShader(filename + ".vert", GL_VERTEX_SHADER);
		int fragID = loadShader(filename + ".frag", GL_FRAGMENT_SHADER);
		glAttachShader(id, vertID);
		glAttachShader(id, fragID);
		glLinkProgram(id);
		String log = glGetProgramInfoLog(id, 128000);
		if(glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
		    Log.fatal("Error linking shaders of " + filename + " together, please see the following log:\n" + log);
		}
		if(glGetProgrami(id, GL_VALIDATE_STATUS) == GL_FALSE) {
		    Log.fatal("Error validating shader " + filename + ", please see the following log:\n" + log);
		}
	    }
	    catch(FileNotFoundException e) {
		Log.fatal("Could not open file for shader " + filename + ": " + e.getMessage());
	    }
	    catch(IOException e) {
		Log.fatal("Unable to read file for shader " + filename + ": " + e.getMessage());
	    }
	    catch(IllegalStateException e) {
		Log.fatal("Could not compile shader " + filename + ":" + e.getMessage());
	    }
	}
	
	public int getID() {
	    return id;
	}
	
	private int loadShader(String filename, int type) throws FileNotFoundException, IOException {
	    int handle = glCreateShader(type);
	    File f = new File(filename);
	    if(!f.exists()) {
		throw new FileNotFoundException();
	    }
	    else {
		try {
		    String line;
		    StringBuilder sb = new StringBuilder();
		    BufferedReader reader = new BufferedReader(new FileReader(f));
		    while((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		    }
		    glShaderSource(handle, sb.toString());
		    glCompileShader(handle);
		    int status = glGetShaderi(handle, GL_COMPILE_STATUS);
		    String log = GL20.glGetShaderInfoLog(handle, 128000);
		    if(status == GL_FALSE) {
			throw new IllegalStateException("Unable to compile Shader " + filename + ", the log is as follows:\n" + log);
		    }
		    else {
			Log.debug("Successfully compiled shader " + filename + ", the log is as follows:\n" + log);
		    }
		    return handle;
		}
		catch(IOException e) {
		    throw e;
		}
	    }
	}
    }
}
