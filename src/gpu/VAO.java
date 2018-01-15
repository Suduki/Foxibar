package gpu;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class VAO {
	int mArrayId = 0;
	
	public VAO() {
		mArrayId = glGenVertexArrays(); GpuUtils.GpuErrorCheck();
	}
	
	public void bind() {
		glBindVertexArray(mArrayId); GpuUtils.GpuErrorCheck();
	}
	
	public static void unbind() {
		glBindVertexArray(0); GpuUtils.GpuErrorCheck();
	}
	
	public void setVbo(int index, VBO buffer, int numComponents) {
		bind();
		glEnableVertexAttribArray(index); GpuUtils.GpuErrorCheck();
		buffer.bind();
		glVertexAttribPointer(index, numComponents, buffer.getComponentType(), false, 0, 0); GpuUtils.GpuErrorCheck();
	}
}
