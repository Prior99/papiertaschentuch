package de.cronosx.papiertaschentuch;

import java.io.File;
import java.util.*;

public class Models {

	private static Map<String, Model> models = new HashMap<>();

	public static void prefetch() {

	}

	public static Model getModel(String fileName) {
		if (models.containsKey(fileName)) {
			return models.get(fileName);
		} else {
			Model model = new ModelOBJ(new File("models/" + fileName));
			model.init();
			models.put(fileName, model);
			return model;
		}
	}
}
