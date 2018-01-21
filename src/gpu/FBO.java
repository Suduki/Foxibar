package gpu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import display.Texture;
public class FBO {
	private int mFboId;
	
	public FBO() {
		mFboId = glGenFramebuffers(); GpuUtils.GpuErrorCheck();
	}
	
	public void setColorAttachment(int index, Texture texture) {
		glBindFramebuffer(GL_FRAMEBUFFER,  mFboId); GpuUtils.GpuErrorCheck();
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, GL_TEXTURE_2D, texture.id(), 0); GpuUtils.GpuErrorCheck();
		glBindFramebuffer(GL_FRAMEBUFFER,  0); GpuUtils.GpuErrorCheck();
	}
	
	public void bind() {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER,  mFboId); GpuUtils.GpuErrorCheck();
	}
	
	public static void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER,  0); GpuUtils.GpuErrorCheck();
	}
}
