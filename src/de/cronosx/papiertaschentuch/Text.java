package de.cronosx.papiertaschentuch;

import de.cronosx.papiertaschentuch.vecmath.Vector2i;

public class Text {
	private String text;
	private Vector2i position;
	
	public static Text createText(String text, Vector2i position) {
		Text t = new Text(text, position);
		Papiertaschentuch.getInstance().getGraphics().getGUI().addText(t);
		return t;
	}

	public Text(String text, Vector2i position) {
		this.text = text;
		this.position = position;
	}

	public Vector2i getPosition() {
		return position;
	}
	
	public Text setPosition(Vector2i position) {
		this.position = position;
		return this;
	}
	
	public Text move(Vector2i delta) {
		this.position.add(delta);
		return this;
	}

	public String getText() {
		return text;
	}

	public Text setText(String text) {
		this.text = text;
		return this;
	}
}