package gpu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;


public class GpuUtils {
	public static void GpuErrorCheck() {
		StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
        String where = ste.getClassName() + "." + ste.getMethodName() + ":" + ste.getLineNumber() + " ";
        
		int err = glGetError();
		if (GL_NO_ERROR != err) {
			String name = "\"unknown, weird...\"";
			switch(err) {
			case GL_INVALID_ENUM:
				name = "GL_INVALID_ENUM";
				break;
			case GL_INVALID_VALUE:
				name = "GL_INVALID_VALUE";
				break;
			case GL_INVALID_OPERATION:
				name = "GL_INVALID_OPERATION";
				break;
			case GL_INVALID_FRAMEBUFFER_OPERATION:
				name = "GL_INVALID_FRAMEBUFFER_OPERATION";
				break;
			case GL_OUT_OF_MEMORY:
				name = "GL_OUT_OF_MEMORY";
				break;
			case GL_STACK_OVERFLOW:
				name = "GL_STACK_OVERFLOW";
				break;
			case GL_STACK_UNDERFLOW:
				name = "GL_STACK_UNDERFLOW";
				break;
				default:
					break;
			}
			
			System.err.println(where + " - GlError: " + name);
			for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
				System.out.println(e);				
			}
		}
	}
}
