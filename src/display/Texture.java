package display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;


public class Texture
{
	private Texture(int pWidth, int pHeight) {
		int[] texId = new int[1];	
		glGenTextures(texId);
		mTextureId = texId[0];

		System.out.println("Creating texture: w = " + pWidth + ", h = " + pHeight + ", id = " + mTextureId);
		
		glBindTexture(GL_TEXTURE_2D, mTextureId);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	}
	
	public Texture(int pWidth, int pHeight, ByteBuffer pPixels)
	{
		this(pWidth, pHeight);
		
		if (pPixels != null)
		{			
			load(pWidth, pHeight, pPixels);
		}
	}
	
	public Texture(int pWidth, int pHeight, FloatBuffer pPixels)
	{
		this(pWidth, pHeight);
		
		if (pPixels != null)
		{			
			load(pWidth, pHeight, pPixels);
		}
	}
	
	public static Texture fromFile(String pFilename)
	{
		Texture texture = null;
		
		try
		{
			BufferedImage image = ImageIO.read(new File(pFilename));
				
			System.out.println(pFilename + ": " + image.getWidth() + "x" + image.getHeight() + ", type: " + Util.bufferedImageTypeString(image));
			ByteBuffer imageData = Util.convertImageData(image);
			texture = new Texture(image.getWidth(), image.getHeight(), imageData);
		}
		catch (IOException e)
		{
			System.out.println("Failed to read file " + pFilename);
		}
		
		return texture;				
	}
	
	public void load(int pWidth, int pHeight, ByteBuffer pPixels) {
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, pWidth, pHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, pPixels);
	}
	
	public void load(int pWidth, int pHeight, FloatBuffer pPixels) {
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, pWidth, pHeight, 0, GL_RGBA, GL_FLOAT, pPixels);
	}

	
	public void bind(int unit) {
		glActiveTexture(GL_TEXTURE0 + unit);
		glBindTexture(GL_TEXTURE_2D, mTextureId);
		glActiveTexture(GL_TEXTURE0);
	}
	
	public static void unbind(int unit) {
		glActiveTexture(GL_TEXTURE0 + unit);
		glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE0);
	}
	
	int mTextureId;
}
