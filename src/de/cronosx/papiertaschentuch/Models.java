package de.cronosx.papiertaschentuch;

import java.io.File;
import java.util.Map;

public class Models {
    private static Map<String, Model> models;

    public static Model getModel(String fileName) {
	if (models.containsKey(fileName)) {
	    return models.get(fileName);
	} else {
	    Model model = new ModelOBJ(new File("models/" + fileName));
	    models.put(fileName, model);
	    return model;
	}
    }
}
