package gpu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Program {
	
	int mProgramId = 0;
	Shader mVertexShader = null;
	Shader mFragmentShader = null;
	boolean mLinkStatus = false;
	
	public Program() {
		mProgramId = glCreateProgram(); GpuUtils.GpuErrorCheck();
	}
	
	public void attachVertexShader(Shader shader) {
		mVertexShader = shader;
		glAttachShader(mProgramId,  shader.id()); GpuUtils.GpuErrorCheck();
	}
	
	public void attachFragmentShader(Shader shader) {
		mFragmentShader = shader;
		glAttachShader(mProgramId,  shader.id()); GpuUtils.GpuErrorCheck();
	}
	
	public boolean link(String pName) {
		glLinkProgram(mProgramId); GpuUtils.GpuErrorCheck();
		
		if (glGetProgrami(mProgramId,  GL_LINK_STATUS) != GL_TRUE) {
			String infoLog = glGetProgramInfoLog(mProgramId); GpuUtils.GpuErrorCheck();
			System.out.println("Program Linking failed:\n" + infoLog);
		}
		else
		{
			if (pName != null) {
				System.out.println("Program " + pName + " linked successfuly.");
			}
			mLinkStatus = true;
		}
		
		return isLinked();
	}
	
	public boolean isLinked() {
		return mLinkStatus;
	}
	
	public void bind() {
		glUseProgram(mProgramId); GpuUtils.GpuErrorCheck();
	}
	
	public static void unbind() {
		glUseProgram(0); GpuUtils.GpuErrorCheck();
	}
}
