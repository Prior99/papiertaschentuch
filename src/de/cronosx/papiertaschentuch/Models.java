package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.models.*;
import java.io.File;
import java.util.Map;

public class Models {

    private static Cube cube;
    private static Ground ground;
    private static Map<String, Model> models;

    public static Model getModel(String fileName) {
	if (models.containsKey(fileName)) {
	    return models.get(fileName);
	} else {
	    Model model = new ModelOBJ(new File(fileName));
	    models.put(fileName, model);
	    return model;
	}
    }

    public static Model getCube() {
	if (cube == null) {
	    cube = new Cube();
	}
	return cube;
    }

    public static Model getGround() {
	if (ground == null) {
	    ground = new Ground();
	}
	return ground;
    }
}
