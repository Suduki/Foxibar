package gpu;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class VBO {

	int mBufferId = 0;
	int mComponentType = 0;
	int mBindTarget = 0;
	
	public static VBO createVertexBuffer(float[] vertexData) {
		VBO vbo = new VBO();
		vbo.mComponentType = GL_FLOAT;
		vbo.mBindTarget = GL_ARRAY_BUFFER;
		if (vertexData != null) {
			vbo.load(vertexData);
		}
		return vbo;
	}
	
	public static VBO createIndexBuffer(int[] indexData) {
		VBO vbo = new VBO();
		vbo.mComponentType = GL_INT;
		vbo.mBindTarget = GL_ELEMENT_ARRAY_BUFFER;
		if (indexData != null) {
			vbo.load(indexData);
		}
		return vbo;
	}
	
	public void load(float[] values) {
		bind();
		glBufferData(GL_ARRAY_BUFFER, values, GL_STATIC_DRAW); GpuUtils.GpuErrorCheck();
	}
	
	public void load(int[] indexValues) {
		bind();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexValues, GL_STATIC_DRAW); GpuUtils.GpuErrorCheck();
	}
	
	public int getComponentType() {
		return mComponentType;
	}
	
	public void bind() {
		glBindBuffer(mBindTarget, mBufferId); GpuUtils.GpuErrorCheck();
	}
	
	public void unbindVertex() {
		glBindBuffer(GL_ARRAY_BUFFER, 0); GpuUtils.GpuErrorCheck();
	}
	
	public void unbindElement() {
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0); GpuUtils.GpuErrorCheck();
	}
	
	protected VBO() {		
		mBufferId = glGenBuffers(); GpuUtils.GpuErrorCheck();
	}
}
