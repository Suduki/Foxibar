package main.java.display;

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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class Util {

	public static String bufferedImageTypeString(BufferedImage image)
	{
		String type;
		
		switch (image.getType())
		{
		case BufferedImage.TYPE_INT_RGB: type = "int-rgb"; break;
		case BufferedImage.TYPE_INT_ARGB: type = "int-argb"; break;
		case BufferedImage.TYPE_INT_ARGB_PRE: type = "int-argb-pre"; break;
		case BufferedImage.TYPE_INT_BGR: type = "int-bgr"; break;
		case BufferedImage.TYPE_3BYTE_BGR: type = "3byte-bgr"; break;
		case BufferedImage.TYPE_4BYTE_ABGR: type = "4byte-abgr"; break;
		case BufferedImage.TYPE_4BYTE_ABGR_PRE: type = "4byte-abgr-pre"; break;
		case BufferedImage.TYPE_BYTE_GRAY: type = "byte-gray"; break;
		case BufferedImage.TYPE_USHORT_GRAY: type = "ushort-gray"; break;
		case BufferedImage.TYPE_BYTE_BINARY: type = "byte-binary"; break;
		case BufferedImage.TYPE_BYTE_INDEXED: type = "byte-indexed"; break;
		case BufferedImage.TYPE_USHORT_565_RGB: type = "ushort-565-rgb"; break;
		case BufferedImage.TYPE_USHORT_555_RGB: type = "ushort-555-rgb"; break;
			default: type = "unknown";
		}
		
		return type;
	}
	
	public static void checkGlError(String message)
	{
		int err = GL11.glGetError();
		if (err != GL11.GL_NO_ERROR)
		{
			String delim = (message != "") ? " -- " : "";
			System.out.println(message + delim + "glGetError: " + glErrorString(err));
		}

	}
	
	public static void checkGlError()
	{
		checkGlError("");
	}
	
	public static String glErrorString(int err)
	{
		switch (err)
		{
		case GL11.GL_INVALID_ENUM:
			return "GL_INVALID_ENUM - An unacceptable value is specified for an enumerated argument. The offending command is ignored and has no other side effect than to set the error flag.";

		case GL11.GL_INVALID_VALUE:
			return "GL_INVALID_VALUE - A numeric argument is out of range. The offending command is ignored and has no other side effect than to set the error flag.";

		case GL11.GL_INVALID_OPERATION:
			return "GL_INVALID_OPERATION - The specified operation is not allowed in the current state. The offending command is ignored and has no other side effect than to set the error flag.";

		case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
			return "GL_INVALID_FRAMEBUFFER_OPERATION - The framebuffer object is not complete. The offending command is ignored and has no other side effect than to set the error flag.";

		case GL11.GL_OUT_OF_MEMORY:
			return "GL_OUT_OF_MEMORY - There is not enough memory left to execute the command. The state of the GL is undefined, except for the state of the error flags, after this error is recorded.";

		case GL11.GL_STACK_UNDERFLOW:
			return "GL_STACK_UNDERFLOW - An attempt has been made to perform an operation that would cause an internal stack to underflow.";

		case GL11.GL_STACK_OVERFLOW:
			return "GL_STACK_OVERFLOW - An attempt has been made to perform an operation that would cause an internal stack to overflow.";
		default:
			return "Unknown error code: " + err;
		}
	}
	
	public static ByteBuffer convertImageData(BufferedImage bufferedImage) {
	    ByteBuffer imageBuffer;
	    WritableRaster raster;
	    BufferedImage texImage;

	    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
	            .getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
	            true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

	    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
	            bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
	    texImage = new BufferedImage(glAlphaColorModel, raster, true,
	            new Hashtable());

	    // copy the source image into the produced image
	    Graphics g = texImage.getGraphics();
	    g.setColor(new Color(0f, 0f, 0f, 0f));
	    g.fillRect(0, 0, 256, 256);
	    g.drawImage(bufferedImage, 0, 0, null);

	    // build a byte buffer from the temporary image
	    // that be used by OpenGL to produce a texture.
	    byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
	            .getData();

	    imageBuffer = ByteBuffer.allocateDirect(data.length);
	    imageBuffer.order(ByteOrder.nativeOrder());
	    imageBuffer.put(data, 0, data.length);
	    imageBuffer.flip();

	    return imageBuffer;
	}
}
