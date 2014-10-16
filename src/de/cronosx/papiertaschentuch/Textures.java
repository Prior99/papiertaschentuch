package de.cronosx.papiertaschentuch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Textures {

	private static Map<String, Texture> textures = new HashMap<>();

	public static Texture getTexture(String fileName) {
		if (textures.containsKey(fileName)) {
			return textures.get(fileName);
		} else {
			try {
				Texture tex = new Texture(new File("textures/" + fileName));
				textures.put(fileName, tex);
				return tex;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
