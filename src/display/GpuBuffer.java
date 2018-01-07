package display;


import static org.lwjgl.opengl.GL15.*;

public class GpuBuffer {

	int mBufferId = 0;
	
	public static GpuBuffer createVertexBuffer() {
		GpuBuffer vbo = new GpuBuffer();
		return vbo;
	}
	
	public static GpuBuffer createIndexBuffer() {
		GpuBuffer vbo = new GpuBuffer();
		return vbo;
	}
	
	protected GpuBuffer() {
		mBufferId = glGenBuffers();
	}
}
