package de.cronosx.papiertaschentuch;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Textures {

	private static Map<String, Texture> textures = new HashMap<>();

	public static Texture getTexture(String fileName) throws IOException {
		if (textures.containsKey(fileName)) {
			return textures.get(fileName);
		} else {
			File f = new File(fileName);
			if(f.exists()) {
				Texture tex = new Texture(new File(fileName));
				textures.put(fileName, tex);
				return tex;
			}
			else {
				throw new FileNotFoundException("Texturefile \"" + f.getName() + "\" not found!");
			}
		}
	}
}
