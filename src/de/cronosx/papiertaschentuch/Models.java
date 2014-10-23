package de.cronosx.papiertaschentuch;

import java.io.*;
import java.util.*;

public class Models {

	private static Map<String, Model> models = new HashMap<>();

	public static void prefetch() {

	}

	public static Model getModel(String fileName) throws FileNotFoundException {
		if (models.containsKey(fileName)) {
			return models.get(fileName);
		} else {
			File file = new File(fileName);
			if(file.exists()) {
				Model model = new ModelOBJ(file);
				model.init();
				models.put(fileName, model);
				return model;
			}
			else {
				throw new FileNotFoundException("Modelfile \"" + file.getName() + "\" not found!");
			}
		}
	}
}
