package display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;

public class Texture
{
	public Texture(int pWidth, int pHeight, ByteBuffer pPixels)
	{
		if (pPixels == null)
		{
			mTextureId = 0;
			System.out.println("pixel data is null");
		}
		else
		{
			int[] texId = new int[1];	
			GL11.glGenTextures(texId);
			mTextureId = texId[0];

			System.out.println("Creating texture: w = " + pWidth + ", h = " + pHeight + ", id = " + mTextureId);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
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
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, pWidth, pHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pPixels);
	}

	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mTextureId);
	}
	
	public static void unbind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
	
	int mTextureId;
}
