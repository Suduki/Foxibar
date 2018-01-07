package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class GpuShader {
	private int mShaderId = 0;
	private GpuE mShaderType;
	private String mSource;
	private boolean mCompileStatus = false;
	
	GpuShader(GpuE pShaderType, String pSource) {
		mShaderType = pShaderType;
		mSource = pSource;
		
		int shaderType = 0;
		switch (mShaderType)
		{
		case VERTEX_SHADER:
			shaderType = GL_VERTEX_SHADER;
			break;
			
		case FRAGMENT_SHADER:
			shaderType = GL_FRAGMENT_SHADER;
			break;
			
		default:
			System.out.println("Unknown shader type:" + mShaderType.toString());
			break;
		}
		
		if (shaderType != 0) {
			mShaderId = glCreateShader(shaderType);
			
			glShaderSource(mShaderId, mSource);
			glCompileShader(mShaderId);
			
			if (glGetShaderi(mShaderId,  GL_COMPILE_STATUS) != GL_TRUE)
			{
				String infoLog = glGetShaderInfoLog(mShaderId);
				System.out.println("Shader Compilation failed:\n" + infoLog);
			}
			else
			{
				mCompileStatus = true;
			}
		}
	}
	
	int id() {
		return mShaderId;
	}
	
	boolean isCompiled() {
		return mCompileStatus;
	}
}