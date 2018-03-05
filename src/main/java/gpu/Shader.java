package gpu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
	private int mShaderId = 0;
	private GpuE mShaderType;
	private String mSource;
	private boolean mCompileStatus = false;
	
	public Shader(GpuE pShaderType, String pSource) {
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
			mShaderId = glCreateShader(shaderType); GpuUtils.GpuErrorCheck();
			
			glShaderSource(mShaderId, mSource); GpuUtils.GpuErrorCheck();
			glCompileShader(mShaderId); GpuUtils.GpuErrorCheck();
			
			if (glGetShaderi(mShaderId,  GL_COMPILE_STATUS) != GL_TRUE)
			{
				String infoLog = glGetShaderInfoLog(mShaderId); GpuUtils.GpuErrorCheck();
				System.out.println("Shader Compilation failed:\n" + infoLog);
			}
			else
			{
				mCompileStatus = true;
			}
		}
	}
	
	public int id() {
		return mShaderId;
	}
	
	public boolean isCompiled() {
		return mCompileStatus;
	}
}