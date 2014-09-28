package de.cronosx.papiertaschentuch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class Texture {
	private final int textureID;
	private final int width, height;
	
	public Texture(File f) throws IOException {
		textureID = glGenTextures();
		BufferedImage bImg = ImageIO.read(f);
		width = bImg.getWidth();
		height = bImg.getHeight();
		load(bImg);
	}
	
	private void load(BufferedImage bImg) {
		int[] pixels = new int[width * height];
		bImg.getRGB(0, 0, width, height, pixels, 0, width);
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int pixel = pixels[y * width + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); //R
				buffer.put((byte) ((pixel >>  8) & 0xFF)); //G
				buffer.put((byte) ((pixel      ) & 0xFF)); //B
				buffer.put((byte) ((pixel >> 24) & 0xFF)); //A
			}
		}
		buffer.flip();
		bind(buffer);
	}
	
	private void bind(ByteBuffer buffer) {
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getTextureID() {
		return textureID;
	}
	
}
