package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.vecmath.Vector2i;
import javax.vecmath.Vector3f;
import jdk.nashorn.api.scripting.JSObject;

public class JSBinding {
	public final JSObjectInterface createText;
	public final JSObjectInterface createEntity;
	public final StringInterface getModel;
	public final StringInterface getTexture;
	public final JSObjectInterface createPhysicalEntity;
	public JSBinding() {
		createText = (JSObjectInterface)(obj) -> {
			if(obj.hasMember("text") && obj.hasMember("position")) {
				return Text.createText((String)obj.getMember("text"), (Vector2i)obj.getMember("position"));
			}
			else {
				throw new IllegalArgumentException("At least \"text\" and \"position\" have to be set.");
			}
		};
		getModel = (StringInterface)(filename) -> {
			return Models.getModel(filename);
		};
		getTexture = (StringInterface)(filename) -> {
			return Textures.getTexture(filename);
		};
		createEntity = (JSObjectInterface)(obj) -> {
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
		createPhysicalEntity = (JSObjectInterface)(obj) -> {
			if(obj.hasMember("model") && obj.hasMember("texture")) {
				Model model = (Model)obj.getMember("model");
				Texture texture = (Texture)obj.getMember("texture");
				float mass;
				PhysicalEntity.CollisionType collisionType;
				if(obj.hasMember("mass")) {
					mass = (int)obj.getMember("mass");
				}
				else {
					mass = 0f;
				}
				if(obj.hasMember("collisionType")) {
					collisionType = PhysicalEntity.CollisionType.valueOf(((String)obj.getMember("collisonType")).toUpperCase());
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
	}
	
	@FunctionalInterface
	public static interface StringInterface {
		public Object apply(String filename);
	}
	
	@FunctionalInterface
	public static interface JSObjectInterface {
		public Object apply(JSObject obj);
	}
}
