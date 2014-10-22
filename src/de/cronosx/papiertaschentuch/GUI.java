package de.cronosx.papiertaschentuch;

import com.bulletphysics.collision.shapes.CollisionShape;
import de.cronosx.papiertaschentuch.Shaders.Shader;
import de.cronosx.papiertaschentuch.vecmath.*;
import java.io.*;
import java.util.*;
import javax.vecmath.Vector2f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class GUI {
	private float pixelWidth, pixelHeight;
	private int width, height;
	private Model letter;
	private List<Text> texts;
	private Shader letterShader;
	private Texture defaultFont;
	private Matrix4f translationMatrix;
	private FontMap defaultFontMap;
	
	public GUI(int width, int height) {
		this.width = width;
		this.height = height;
		texts = new ArrayList<>();
		defaultFontMap = new FontMap("fonts/default.map");
		translationMatrix = new Matrix4f();
		pixelWidth = 2.f / width;
		pixelHeight = 2.f / height;
		setupLetterModel();
	}
	
	public void addText(Text text) {
		texts.add(text);
	}
	
	public void removeText(Text text) {
		texts.remove(text);
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
		Vector2i position = text.getPosition();
		for(int i = 0; i < text.getText().length(); i++) {
			char c = text.getText().charAt(i);
			translationMatrix.setIdentity();
			translationMatrix.translate(new Vector2f(position.x * pixelWidth - 1.f + 9 * pixelWidth * col, 
					1.f - 16 * pixelHeight * row - position.y * pixelHeight));
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
				0, 0, 0,
				8 * pixelWidth,  0, 0,
				0, -16 * pixelHeight, 0,
				8 * pixelWidth, -16 * pixelHeight, 0,
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
