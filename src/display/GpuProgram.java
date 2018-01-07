package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GpuProgram {
	
	int mProgramId = 0;
	GpuShader mVertexShader = null;
	GpuShader mFragmentShader = null;
	boolean mLinkStatus = false;
	
	GpuProgram() {
		mProgramId = glCreateProgram();
	}
	
	void attachVertexShader(GpuShader shader) {
		mVertexShader = shader;
		glAttachShader(mProgramId,  shader.id());
	}
	
	void attachFragmentShader(GpuShader shader) {
		mFragmentShader = shader;
		glAttachShader(mProgramId,  shader.id());
	}
	
	boolean link() {
		glLinkProgram(mProgramId);
		
		if (glGetProgrami(mProgramId,  GL_LINK_STATUS) != GL_TRUE) {
			String infoLog = glGetProgramInfoLog(mProgramId);
			System.out.println("Program Linking failed:\n" + infoLog);
		}
		else
		{
			mLinkStatus = true;
		}
		
		return isLinked();
	}
	
	boolean isLinked() {
		return mLinkStatus;
	}
}
