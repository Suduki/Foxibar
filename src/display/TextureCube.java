package display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import gpu.GpuUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;


public class TextureCube
{
	private int mTextureId;
	private int mSize = 0;
	
	public int id() {
		return mTextureId;
	}
	
	public TextureCube()
	{
		int[] texId = new int[1];	
		glGenTextures(texId); GpuUtils.GpuErrorCheck();
		mTextureId = texId[0];
				
		glBindTexture(GL_TEXTURE_CUBE_MAP, mTextureId); GpuUtils.GpuErrorCheck();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR); GpuUtils.GpuErrorCheck();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR); GpuUtils.GpuErrorCheck();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT); GpuUtils.GpuErrorCheck();
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT); GpuUtils.GpuErrorCheck();
	}
	
	
	public void loadFacesFromFile(
			String pRightFilename,
			String pLeftFilename,
			String pTopFilename,
			String pBottomFilename,
			String pBackFilename,
			String pFrontFilename
			) {
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_X, pRightFilename);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, pLeftFilename);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, pTopFilename);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, pBottomFilename);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, pBackFilename);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, pFrontFilename);		
	}
	
	public void loadFaceFromFile(int pFaceGL, String pFilename)
	{
		try
		{
			BufferedImage image = ImageIO.read(new File(pFilename));
				
			System.out.println(pFilename + ": " + image.getWidth() + "x" + image.getHeight() + ", type: " + Util.bufferedImageTypeString(image));
			ByteBuffer imageData = Util.convertImageData(image);
			loadFace(pFaceGL, image.getWidth(), image.getHeight(), imageData);
		}
		catch (IOException e)
		{
			System.out.println("Failed to read file " + pFilename);
		}				
	}	
	
	public void loadFace(int pFaceGL, int pWidth, int pHeight, ByteBuffer pPixels) {
		bind(0);
		if (mSize == 0) {
			glTexStorage2D(GL_TEXTURE_CUBE_MAP, 1, GL_RGBA8, pWidth, pHeight); GpuUtils.GpuErrorCheck();
			mSize = pWidth;
		}
		
		glTexSubImage2D(pFaceGL, 0, 0, 0, pWidth, pHeight, GL_RGBA, GL_UNSIGNED_BYTE, pPixels); GpuUtils.GpuErrorCheck();
		unbind(0);
	}
	
	/*
	public void load(int pWidth, int pHeight, FloatBuffer pPixels) {
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, pWidth, pHeight, 0, GL_RGBA, GL_FLOAT, pPixels); GpuUtils.GpuErrorCheck();
	}
	*/

	
	public void bind(int unit) {
		glActiveTexture(GL_TEXTURE0 + unit); GpuUtils.GpuErrorCheck();
		glBindTexture(GL_TEXTURE_CUBE_MAP, mTextureId); GpuUtils.GpuErrorCheck();
		glActiveTexture(GL_TEXTURE0); GpuUtils.GpuErrorCheck();
	}
	
	public static void unbind(int unit) {
		glActiveTexture(GL_TEXTURE0 + unit); GpuUtils.GpuErrorCheck();
		glBindTexture(GL_TEXTURE_CUBE_MAP, 0); GpuUtils.GpuErrorCheck();
		glActiveTexture(GL_TEXTURE0); GpuUtils.GpuErrorCheck();
	}
}
