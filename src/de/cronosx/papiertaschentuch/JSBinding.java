package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.vecmath.Vector2i;
import java.io.*;
import javax.vecmath.Vector3f;
import jdk.nashorn.api.scripting.*;

public class JSBinding {
	public final ScriptObjectMirrorInterface createText;
	public final ScriptObjectMirrorInterface createEntity;
	public final StringInterface getModel;
	public final StringInterface getTexture;
	public final ScriptObjectMirrorInterface createPhysicalEntity;
	public final CallbackInterface on;
	public final ScriptObjectMirrorInterface createPointLight;
	public final ObjectGetterInterface getSun;
	private final EventEmitter emitter;
	
	public JSBinding() {
		emitter = new EventEmitter();
		Papiertaschentuch.getInstance().getEmitter().on("ready", () -> {
			Papiertaschentuch.getInstance().getEmitter().addChild(emitter);
			Papiertaschentuch.getInstance().getGraphics().getEmitter().addChild(emitter);
			Papiertaschentuch.getInstance().getGame().getEmitter().addChild(emitter);
			emitter.emit("ready");
		});
		createText = (ScriptObjectMirrorInterface)(obj) -> {
			if(obj.hasMember("text") && obj.hasMember("position")) {
				return Text.createText((String)obj.getMember("text"), (Vector2i)obj.getMember("position"));
			}
			else {
				throw new IllegalArgumentException("At least \"text\" and \"position\" have to be set.");
			}
		};
		getModel = (StringInterface)(filename) -> {
			try {
				return Models.getModel(filename);
			}
			catch(IOException e) {
				Log.error("Script requested modefile \"" + filename + "\", which does not exist.");
				return null;
			}
		};
		getTexture = (StringInterface)(filename) -> {
			try {
				return Textures.getTexture(filename);
			}
			catch(IOException e) {
				Log.error("Script requested texturefile \"" + filename + "\", which does not exist.");
				return null;
			}
		};
		getSun = (ObjectGetterInterface)() -> {
			return Lights.getSun();
		};
		createEntity = (ScriptObjectMirrorInterface)(obj) -> {
			if(!obj.hasMember("model") && !obj.hasMember("texture")) {
				return Entities.createEntity();
			}
			else if(obj.hasMember("model") && obj.hasMember("texture")) {
				return Entities.createEntity((Model)obj.getMember("model"), (Texture)obj.getMember("texture"));
			}
			else {
				throw new IllegalArgumentException("Either both model and texture or neither of them have to be supplied when calling \"createEntity\"");
			}
		};
		createPointLight = (ScriptObjectMirrorInterface)(obj) -> {
			Vector3f position, color;
			float strength;
			if(obj.hasMember("position")) {
				position = (Vector3f)obj.getMember("position");
			}
			else {
				position = new Vector3f(0, 0, 0);
			}
			if(obj.hasMember("color")) {
				color = (Vector3f)obj.getMember("color");
			}
			else {
				color = new Vector3f(1, 1, 1);
			}
			if(obj.hasMember("strength")) {
				strength = ((Number)obj.getMember("strength")).floatValue();
			}
			else {
				strength = 10f;
			}
			try {
				return Lights.createPointLight(position, color, strength);
			}
			catch(UnsupportedOperationException e) {
				Log.error("Script tried to create more than 64 lightsources.");
				return null;
			}
		
		};
		createPhysicalEntity = (ScriptObjectMirrorInterface)(obj) -> {
			if(obj.hasMember("model") && obj.hasMember("texture")) {
				Model model = (Model)obj.getMember("model");
				Texture texture = (Texture)obj.getMember("texture");
				float mass;
				PhysicalEntity.CollisionType collisionType;
				if(obj.hasMember("mass")) {
					mass = ((Number)obj.getMember("mass")).floatValue();
				}
				else {
					mass = 0f;
				}
				if(obj.hasMember("collisionType")) {
					collisionType = PhysicalEntity.CollisionType.valueOf(((String)obj.getMember("collisionType")).toUpperCase());
				}
				else {
					collisionType = PhysicalEntity.CollisionType.CONVEX;
				}
				if(collisionType == PhysicalEntity.CollisionType.BOX) {
					if(obj.hasMember("collisionBox")) {
						return Entities.createPhysicalEntity(model, texture, mass, collisionType, (Vector3f)obj.getMember("collisionBox"));
					}
					else {
						throw new IllegalArgumentException("If \"collisionType\" is \"BOX\", \"collisionBox\" must be set.");
					}
				}
				else {
					return Entities.createPhysicalEntity(model, texture, mass, collisionType);
				}
			}
			else {
				throw new IllegalArgumentException("A physical entity needs at least \"model\" and \"texture\".");
			}
		};
		on = (CallbackInterface)(key, obj) -> {
			emitter.on(key, () -> {
				try {
					obj.call(obj);
				}
				catch(NashornException e) {
					Log.fatal("An exception occured when executing callback \"" + key + "\" on scripts. \n" + 
							  "Linenumber: " + e.getLineNumber() + "\n" + 
							  "File: " + e.getFileName() + "\n" + 
							  "Error: " + e.getEcmaError());
				}
			});
		};
	}
	
	@FunctionalInterface
	public static interface StringInterface {
		public Object apply(String filename);
	}
	
	@FunctionalInterface
	public static interface ScriptObjectMirrorInterface {
		public Object apply(ScriptObjectMirror obj);
	}
	
	@FunctionalInterface
	public static interface CallbackInterface {
		public void apply(String key, ScriptObjectMirror obj);
	}
	
	@FunctionalInterface
	public static interface ObjectGetterInterface {
		public Object apply();
	}
}
