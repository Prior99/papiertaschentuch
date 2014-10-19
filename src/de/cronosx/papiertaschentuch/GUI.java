package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.CollisionShape;
import de.cronosx.papiertaschentuch.Shaders.Shader;
import de.cronosx.papiertaschentuch.vecmath.Matrix4f;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector2f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class GUI {
	private static final float fontSize = .06f;
	private int width, height;
	private float aspectRatio;
	private Model letter;
	private List<Text> texts;
	private Shader letterShader;
	private Texture defaultFont;
	private Matrix4f translationMatrix;
	private FontMap defaultFontMap;
	
	public GUI(int width, int height) {
		this.width = width;
		this.height = height;
		aspectRatio = width / (float) height;
		setupLetterModel();
		texts = new ArrayList<>();
		defaultFontMap = new FontMap("fonts/default.map");
		translationMatrix = new Matrix4f();
	}
	
	public void addText(String text, int x, int y) {
		texts.add(new Text(text, x, y));
	}
	
	private void drawText(Text text) {
		glUseProgram(letterShader.getID());
		letterShader.setAttribute("aVertexPosition", letter.getVertexBufferID(), 3, GL_FLOAT);
		letterShader.setAttribute("aTextureCoord", letter.getTextureBufferID(), 2, GL_FLOAT);
		letterShader.setAttribute("aNormals", letter.getNormalBufferID(), 3, GL_FLOAT);
		letterShader.setUniform("uSampler", 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, defaultFont.retrieveTextureID());
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, letter.getIndexBufferID());
		int col = 0, row = 0;
		for(int i = 0; i < text.getText().length(); i++) {
			char c = text.getText().charAt(i);
			translationMatrix.setIdentity();
			translationMatrix.translate(new Vector2f((fontSize / 5) * 3 * col, 1.f -(fontSize / 5) * 9 * row));
			if(c == '\n') {
				col = 0;
				row++;
			}
			else {
				drawLetter(defaultFontMap.getIndexOfChar(c));
				col++;
			}
		}
	}
	
	private void drawLetter(int index) {
		letterShader.setUniform("uIndex", index);
		letterShader.setUniform("uTranslationMatrix", translationMatrix);
		glDrawElements(GL_TRIANGLES, letter.getIndexCount(), GL_UNSIGNED_INT, 0);
	}
	
	public void draw() {
		checkInit();
		translationMatrix.setIdentity();
		glDisable(GL_DEPTH_TEST);
		texts.stream().forEach((t) -> {
			drawText(t);
		});
		glEnable(GL_DEPTH_TEST);
	}
	
	private void checkInit() {
		if(defaultFont == null) {
			defaultFont = Textures.getTexture("fonts/default.png");
		}
		if(letterShader == null) {
			letterShader = Shaders.getShader("shader/letter");
		}
	}
	
	private void setupLetterModel() {
		letter = new ModelLetter();
		letter.init();
	}
	
	public static class Text {
		private String text;
		private int x, y;
		public Text(String text, int x, int y) {
			this.text = text;
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
	}
	
	private class FontMap {
		private final Map<Integer, Integer> map;
		public FontMap(String filename) {
			map = new HashMap<>();
			parse(new File(filename));
		}
		
		public int getIndexOfCharCode(int i) {
			if(map.containsKey(i)) {
				return map.get(i);
			}
			else {
				return -1;
			}
		}
		
		public int getIndexOfChar(char c) {
			return getIndexOfCharCode((int) c);
		}
		
		private void parse(File file) {
			try(BufferedReader rd = new BufferedReader(new FileReader(file))) {
				StringBuilder sb = new StringBuilder();
				String line;
				while((line = rd.readLine()) != null) {
					line = line.trim();
					if(!line.startsWith("#")) {
						sb.append(line).append(" ");
					}
				}
				String[] numberStrings = sb.toString().split("\\s+");
				int j = 0;
				for(String ns : numberStrings) {
					if(ns.length() > 0) {
						try {
							int i = Integer.parseInt(ns);
							map.put(i, j);
						}
						catch(NumberFormatException e) {
							Log.error("Broken Fontmap file \"" + file.getName() + "\". This is not a number: \"" + ns + "\"");
						}
						j++;
					}
				}
			}
			catch(FileNotFoundException ex) {
				Log.error("Fontmap file \"" + file.getName() + "\" could not be found.");
			}
			catch(IOException ex) {
				Log.error("Fontmap file \"" + file.getName() + "\" could not be read.");
			}
		}
	}
	
	private class ModelLetter extends Model {
		private float[] vertices, textureMap, normals;
		private int[] faces;

		@Override
		protected void load() {
			vertices = new float[] {
				0,        fontSize * aspectRatio, 0,
				fontSize / 2, fontSize * aspectRatio, 0,
				0,        0,                  0,
				fontSize / 2, 0,                  0
			};
			normals = new float[] {
				0, 0, -1,
				0, 0, -1,
				0, 0, -1,
				0, 0, -1,
			};
			faces = new int[] {
				0, 1, 2,
				1, 2, 3
			};
			textureMap = new float[] {
				0,       0,
				0.0625f, 0,
				0,       0.0625f,
				0.0625f, 0.0625f
			};
		}

		@Override
		protected float[] getVertices() {
			return vertices;
		}

		@Override
		protected float[] getNormals() {
			return normals;
		}

		@Override
		protected float[] getTextureMap() {
			return textureMap;
		}

		@Override
		protected int[] getFaces() {
			return faces;
		}

		@Override
		public CollisionShape getConvexCollisionShape() { throw new UnsupportedOperationException("A letter does not have a collision Shape."); }

		@Override
		public CollisionShape getConcaveCollisionShape() { throw new UnsupportedOperationException("A letter does not have a collision Shape."); }
	}
}
