package display;

import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

public class Texture {
	public Texture(int pWidth, int pHeight, ByteBuffer pPixels) {
		int[] texId = new int[1];
		
		System.out.println("Creating texture: w = " + pWidth + ", h = " + pHeight);
		if (pPixels == null) {
			mTextureId = 0;
			System.out.println("pixel data is null");
		}
		else {
			GL11.glGenTextures(texId);
			mTextureId = texId[0];
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, pWidth, pHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pPixels);
		}
	}
	
	public static Texture fromFile(String pFilename) {
		IntBuffer width    = IntBuffer.allocate(1);
		IntBuffer height   = IntBuffer.allocate(1);
		IntBuffer channels = IntBuffer.allocate(1);
				
		ByteBuffer imageData = STBImage.stbi_load(pFilename, width, height, channels, 4);
		
		return new Texture(width.get(0), height.get(0), imageData);
	}

	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
	}
	
	public static void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	int mTextureId;
}
