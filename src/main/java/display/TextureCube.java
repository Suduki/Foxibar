package display;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import gpu.GpuUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;


public class TextureCube implements FrameUpdatable
{
	private int mTextureId;
	private int mSize = 0;
	private boolean mNeedsFrameUpdate = false;
	
	private class FaceData
	{
		public ByteBuffer bytes;
		public int width;
		public int height;
	}
	
	private TreeMap<Integer, FaceData> mFaceData;
	
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
		
		mFaceData = new TreeMap<Integer, FaceData>();
	}
	
	public void loadFacesFromFile(
			String pRightFilename,
			String pLeftFilename,
			String pTopFilename,
			String pBottomFilename,
			String pBackFilename,
			String pFrontFilename
			)
	{
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_X, pRightFilename,  false);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, pLeftFilename,   false);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, pTopFilename,    false);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, pBottomFilename, false);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, pBackFilename,   false);
		loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, pFrontFilename,  false);		
	}
	
	public void loadFacesFromFileAsync(
			String pRightFilename,
			String pLeftFilename,
			String pTopFilename,
			String pBottomFilename,
			String pBackFilename,
			String pFrontFilename
			)
	{	
		mNeedsFrameUpdate = true;
		
		new Thread(() -> {
			loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_X, pRightFilename,  true);
			loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, pLeftFilename,   true);
			loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, pTopFilename,    true);
			loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, pBottomFilename, true);
			loadFaceFromFile(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, pBackFilename,   true);
			loadFaceFromFile(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, pFrontFilename,  true);		
		}).start();
	}
	
	public void loadFaceFromFile(int pFaceGL, String pFilename, boolean async)
	{
		try
		{
			BufferedImage image = ImageIO.read(new File(pFilename));
				
			System.out.println(pFilename + ": " + image.getWidth() + "x" + image.getHeight() + ", type: " + Util.bufferedImageTypeString(image));
			ByteBuffer imageData = Util.convertImageData(image);
			
			if (async)
			{
				setFaceData(imageData, image.getWidth(), image.getHeight(), pFaceGL);
			}
			else
			{
				loadFace(pFaceGL, image.getWidth(), image.getHeight(), imageData);
			}
		}
		catch (IOException e)
		{
			setFaceData(null, 0, 0, pFaceGL);
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

	@Override
	public boolean frameUpdate() {
		if (mFaceData.size() == 6)
		{
			System.out.println("Time to update cube texture!");
			for (Map.Entry<Integer, FaceData> entry : mFaceData.entrySet())
			{
				int faceGL = entry.getKey();
				FaceData fd = entry.getValue();
				
				loadFace(faceGL, fd.width, fd.height, fd.bytes);
			}
			
			mNeedsFrameUpdate = false;
			mFaceData.clear();
			
			return false;
		}
		return true;
	}

	@Override
	public boolean frameUpdateNeeded() {
		return mNeedsFrameUpdate;
	}
	
	private void setFaceData(ByteBuffer buffer, int width, int height, int glFace)
	{
		FaceData fd = new FaceData();
		fd.bytes = buffer;
		fd.width = width;
		fd.height = height;
		
		System.out.println("setFaceData(..., " + width + ", " + height + ", " + glFace + ")");
		mFaceData.put(glFace, fd);
	}
}
