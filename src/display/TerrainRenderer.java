package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import constants.Constants;
import gpu.VAO;
import gpu.GpuE;
import gpu.Program;
import gpu.RenderNode;
import gpu.Renderer;
import gpu.Shader;
import gpu.GpuUtils;
import gpu.VBO;
import gpu.nodes.SetViewport;
import gpu.nodes.UseProgram;
import world.World;
import static org.lwjgl.glfw.GLFW.*;

public class TerrainRenderer {
	private Texture             mStrataTexture = null;
	private Texture             mDetailTexture = null;
	private Texture             mHeightTexture = null;
			
	private VBO                 mPositionVbo = null;
	private VBO                 mTexCoordVbo = null;
	private VBO                 mNormalVbo = null;
	private VBO                 mIndexVbo = null;
	private VAO                 mBufferSet = null;	
	private Program             mTerrainProgram = null;	
	private int                 mNumIndices = 0;
	private Window              mWindow = null;
	private Camera              mCamera = null;
	private FlyCameraController mCameraController = null;
	private float               mWrinkleParam = 1.0f;
	
	public TerrainRenderer(Window window) {
		mWindow = window;

		mPositionVbo = VBO.createVertexBuffer(null);
		mTexCoordVbo = VBO.createVertexBuffer(null);
		mNormalVbo   = VBO.createVertexBuffer(null);
		mIndexVbo    = VBO.createIndexBuffer(null);
		mBufferSet   = new VAO();
		mBufferSet.setVbo(0, mPositionVbo, 3);
		mBufferSet.setVbo(1, mTexCoordVbo, 2);
		mBufferSet.setVbo(2, mNormalVbo,   3);		
		updateArrays();
				
		mTerrainProgram = new Program();
		mTerrainProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simpleVertex));
		mTerrainProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simpleFragment));
		mTerrainProgram.link();
		
		mStrataTexture = Texture.fromFile("pics/strata.png");
		mDetailTexture = Texture.fromFile("pics/detail.png");
		buildHeightTexture();
			
		mCamera = new Camera();
		mCamera.setAspectRatio(mWindow.getAspectRatio());
		mCameraController = new FlyCameraController(mCamera);
		
		ProxyInputHandler inputProxy = new ProxyInputHandler();
		inputProxy.add(mCameraController);
		inputProxy.add(new BaseInputHandler() {
			public void handleKeyboardEvents(int action, int key) { if (key == GLFW_KEY_ESCAPE) { mWindow.requestClose(); } }
			public void handleScrollWheel(long window, double xoffset, double yoffset) { mWrinkleParam += yoffset * 0.01f; if (mWrinkleParam < 0) {mWrinkleParam = 0;} if (mWrinkleParam > 1) { mWrinkleParam = 1; } }
		});
		
		mWindow.setInputHandler(inputProxy);
	}
	
	void drawArrays() {
		float[] matrixBuffer = new float[16];
		mTerrainProgram.bind();
		
		glUniformMatrix4fv(0, false, mCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniformMatrix4fv(1, false, mCamera.getViewMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniform1f(2, mWrinkleParam);
		glUniform1f(3, 1.0f/(float)(0.25*Math.sqrt(Constants.WORLD_SIZE)));
		
		mBufferSet.bind();
		mIndexVbo.bind();
		glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();
		Program.unbind();
	}
	
	public void render() {
		mCameraController.update();
		mCamera.update();
		
		glViewport(0, 0, mWindow.getWidth(), mWindow.getHeight()); GpuUtils.GpuErrorCheck();
		glClearColor(0.25f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
		glEnable(GL_DEPTH_TEST); GpuUtils.GpuErrorCheck();
		glEnable(GL_CULL_FACE); GpuUtils.GpuErrorCheck();
		mHeightTexture.bind(0);
		mStrataTexture.bind(1);
		mDetailTexture.bind(2);
		
		drawArrays();
	}
	
	private void updateArrays() {		
		float[] vertexData   = new float[Constants.WORLD_SIZE*3];
		float[] texCoordData = new float[Constants.WORLD_SIZE*2];
		int[]   indexData    = new int  [Constants.WORLD_SIZE*6];
		
		int index = 0;
		float xScale = 1.0f/Constants.WORLD_SIZE_X;
		float zScale = 1.0f/Constants.WORLD_SIZE_Y;
		float yScale = (float)(0.25*Math.sqrt(Constants.WORLD_SIZE));//25.0f;
		float xOffset = -Constants.WORLD_SIZE_X/2.0f;
		float zOffset = -Constants.WORLD_SIZE_Y/2.0f;
		
		for (int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
				vertexData[3*index+0] = x + xOffset;
				vertexData[3*index+1] = 0;//World.terrain.height[index]*yScale;
				vertexData[3*index+2] = z + zOffset;
				
				texCoordData[2*index+0] = z*zScale;
				texCoordData[2*index+1] = x*xScale;
				
				++index;
			}
		}
				
		mNumIndices = 0;
		for (int x = 0; x < Constants.WORLD_SIZE_X-1; ++x) {
			for (int z = 0; z < Constants.WORLD_SIZE_Y-1; ++z) {
				indexData[mNumIndices+0] = Constants.WORLD_SIZE_X*z     + x;
				indexData[mNumIndices+1] = Constants.WORLD_SIZE_X*z     + x + 1;
				indexData[mNumIndices+2] = Constants.WORLD_SIZE_X*(z+1) + x;
				
				indexData[mNumIndices+3] = Constants.WORLD_SIZE_X*z     + x + 1;
				indexData[mNumIndices+4] = Constants.WORLD_SIZE_X*(z+1) + x + 1;
				indexData[mNumIndices+5] = Constants.WORLD_SIZE_X*(z+1) + x;
				
				mNumIndices+=6;
			}
		}

		Vector3f[] normals = new Vector3f[Constants.WORLD_SIZE];
		for (int i = 0; i < normals.length; ++i) {
			normals[i] = new Vector3f();
		}		
		
		for (int triIndex = 0; triIndex < indexData.length/3; ++triIndex) {
			int i0 = 3*indexData[3*triIndex+0];
			int i1 = 3*indexData[3*triIndex+1];
			int i2 = 3*indexData[3*triIndex+2];
			Vector3f p0 = new Vector3f(vertexData[i0+0], vertexData[i0+1], vertexData[i0+2]);
			Vector3f p1 = new Vector3f(vertexData[i1+0], vertexData[i1+1], vertexData[i1+2]);
			Vector3f p2 = new Vector3f(vertexData[i2+0], vertexData[i2+1], vertexData[i2+2]);
			
			p1.sub(p0);
			p2.sub(p0);
			p1.cross(p2, p0);					
			
			normals[i0/3].add(p0);
			normals[i1/3].add(p0);
			normals[i2/3].add(p0);
		}
		
		float[] normalData = new float[vertexData.length];
		for (int i = 0; i < normals.length; ++i) {
			normals[i].normalize();
			normalData[3*i + 0] = normals[i].x;
			normalData[3*i + 1] = normals[i].y;
			normalData[3*i + 2] = normals[i].z;
		}
		
		mPositionVbo.load(vertexData);
		mTexCoordVbo.load(texCoordData);
		mNormalVbo.load(normalData);
		mIndexVbo.load(indexData);
	}
	
	private void buildHeightTexture() {
		FloatBuffer heightBuffer = BufferUtils.createFloatBuffer(Constants.WORLD_SIZE*4);;
		float yScale = (float)(0.25*Math.sqrt(Constants.WORLD_SIZE));
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			float h = World.terrain.height[i]*yScale;
			heightBuffer.put(i*4+0, h);
			heightBuffer.put(i*4+1, 0.0f);
			heightBuffer.put(i*4+2, 0.0f);
		}
		
		mHeightTexture = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
	}
}
