package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import constants.Constants;
import gpu.VAO;
import gpu.FBO;
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
	// Visualisation.
	private Window              mWindow           = null;
	private Camera              mCamera           = null;
	private FlyCameraController mCameraController = null;
	private Texture             mStrataTexture    = null;
	private Texture             mDetailTexture    = null;
	private Texture             mWaterTexture     = null;
	private TextureCube         mSkyboxTexture    = null;
	private VBO                 mPositionVbo      = null;
	private VBO                 mTexCoordVbo      = null;
	private VBO                 mIndexVbo         = null;
	private VAO                 mBufferSet        = null;	
	private int                 mNumIndices       = 0;
	private Program             mTerrainProgram   = null;
	private Program             mWaterProgram     = null;
	
	// Simulation.
	private int                 mSrcIndex   = 0;
	private int                 mDstIndex   = 1;
	private float               mHeightScale = 1;     
	private Texture[]           mHeightTexture      = new Texture[2];
	private Texture[]           mFluxTexture        = new Texture[2];
	private Texture[]           mVelocityTexture    = new Texture[2];
	private FBO                 mSimulationFbo      = null;		
	private Program             mFluxUpdateProgram  = null;
	private Program             mWaterUpdateProgram = null;
	
	public TerrainRenderer(Window window) {
		System.out.println("WORLD_SIZE_X = " + Constants.WORLD_SIZE_X + ", WORLD_SIZE_Y = " + Constants.WORLD_SIZE_Y);
		mWindow = window;
		
		mCamera = new Camera();
		mCamera.setAspectRatio(mWindow.getAspectRatio());
		mCameraController = new FlyCameraController(mCamera);
		
		ProxyInputHandler inputProxy = new ProxyInputHandler();
		inputProxy.add(mCameraController);
		inputProxy.add(new BaseInputHandler() {
			public void handleKeyboardEvents(int action, int key) {
				if (key == GLFW_KEY_ESCAPE) {
					mWindow.requestClose();
					}
				}
		});
		
		mWindow.setInputHandler(inputProxy);
		
		initVertexArrays();
		initVisualisationShaderPrograms();
		initVisualisationTextures();
		
		mHeightScale = (float)(0.25*Math.sqrt(Constants.WORLD_SIZE));
		initSimulationShaderPrograms();
		initSimulationTextures();
	}
	
	private void initVisualisationShaderPrograms() {
		mTerrainProgram = new Program();
		mTerrainProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.solidGroundVertex));
		mTerrainProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.solidGroundFragment));
		mTerrainProgram.link();
		
		mWaterProgram = new Program();
		mWaterProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.waterVertex));
		mWaterProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.waterFragment));
		mWaterProgram.link();
	}
	
	private void initSimulationShaderPrograms() {
		mFluxUpdateProgram = new Program();
		mFluxUpdateProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simVertex));
		mFluxUpdateProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simFluxUpdateFragment));
		mFluxUpdateProgram.link();
		
		mWaterUpdateProgram = new Program();
		mWaterUpdateProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simVertex));
		mWaterUpdateProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simWaterUpdateFragment));
		mWaterUpdateProgram.link();
	}
	
	private void initVisualisationTextures() {
		mStrataTexture = Texture.fromFile("pics/strata.png");
		mDetailTexture = Texture.fromFile("pics/detail.png");
		mWaterTexture  = Texture.fromFile("pics/water.png");
		mSkyboxTexture = new TextureCube();
		mSkyboxTexture.loadFacesFromFile(
				"pics/skybox/right.png",
				"pics/skybox/left.png",
				"pics/skybox/top.png",
				"pics/skybox/bottom.png",
				"pics/skybox/front.png",
				"pics/skybox/back.png");
	}
	
	
	
	void drawArrays() {
		float[] matrixBuffer = new float[16];
		
		glViewport(0, 0, mWindow.getWidth(), mWindow.getHeight()); GpuUtils.GpuErrorCheck();
		glClearColor(0.25f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
		glEnable(GL_DEPTH_TEST); GpuUtils.GpuErrorCheck();
		glEnable(GL_CULL_FACE); GpuUtils.GpuErrorCheck();
		mHeightTexture[mSrcIndex].bind(0);
		mStrataTexture.bind(1);
		mDetailTexture.bind(2);
		mWaterTexture.bind(3);
		mSkyboxTexture.bind(4);
		mVelocityTexture[mSrcIndex].bind(5);
		
		glDepthFunc(GL_LEQUAL);
		mTerrainProgram.bind();
		
		glUniformMatrix4fv(0, false, mCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniformMatrix4fv(1, false, mCamera.getViewMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniform1f(3, 1.0f/(float)(0.25*Math.sqrt(Constants.WORLD_SIZE)));
		
		mBufferSet.bind();
		mIndexVbo.bind();
		glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();
		Program.unbind();
		
		glDepthFunc(GL_LESS);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		mWaterProgram.bind();

		Vector3f e = mCamera.getEyePosition();
		glUniformMatrix4fv(0, false, mCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniformMatrix4fv(1, false, mCamera.getViewMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniform4f(2, e.x, e.y, e.z, 1.0f);
		glUniform1f(3, 1.0f/(float)(0.25*Math.sqrt(Constants.WORLD_SIZE)));
		
		mBufferSet.bind();
		mIndexVbo.bind();
		glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();
		Program.unbind();
		glDisable(GL_BLEND);
	}
	
	void swapTextures(Texture[] texture) {
		Texture tmp = texture[0];
		texture[0] = texture[1];
		texture[1] = tmp;
	}
	static final int[] mSimDrawBuffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2};
	
	public void render() {
		mCameraController.update();
		drawArrays();	
		stepSimulation();
	}
	
	private void stepSimulation() {
		// Step simulation.
		mSimulationFbo.bind();
		glDrawBuffers(mSimDrawBuffers); GpuUtils.GpuErrorCheck();
		
		// Calculate new flux.
		mFluxUpdateProgram.bind();
		glUniform1f(0, 1.0f/Constants.WORLD_SIZE_X);
		mHeightTexture[mSrcIndex].bind(0);
		mFluxTexture[mSrcIndex].bind(1);
		glViewport(0, 0, Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
		glDrawArrays(GL_TRIANGLES, 0, 6); GpuUtils.GpuErrorCheck();
		
		FBO.unbind();
		
		swapTextures(mHeightTexture);
		swapTextures(mFluxTexture);
		mSimulationFbo.setColorAttachment(0, mHeightTexture[mDstIndex]);
		mSimulationFbo.setColorAttachment(1, mFluxTexture[mDstIndex]);
		mHeightTexture[mSrcIndex].bind(0);
		mFluxTexture[mSrcIndex].bind(1);
		mStrataTexture.bind(3);
		
		mSimulationFbo.bind();
		glDrawBuffers(mSimDrawBuffers); GpuUtils.GpuErrorCheck();
		
		// Calculate new water and erode.
		mWaterUpdateProgram.bind();
		glUniform1f(0, 1.0f/Constants.WORLD_SIZE_X);
		glUniform1f(3, 1.0f/(float)(0.25*Math.sqrt(Constants.WORLD_SIZE)));
		glViewport(0, 0, Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
		glDrawArrays(GL_TRIANGLES, 0, 6); GpuUtils.GpuErrorCheck();
		FBO.unbind();
		
		swapTextures(mFluxTexture);
		swapTextures(mHeightTexture);
		swapTextures(mVelocityTexture);
		mSimulationFbo.setColorAttachment(0, mHeightTexture[mDstIndex]);
		mSimulationFbo.setColorAttachment(1, mFluxTexture[mDstIndex]);
		mSimulationFbo.setColorAttachment(2, mVelocityTexture[mDstIndex]);
	}
	
	private void initVertexArrays() {	
		mPositionVbo = VBO.createVertexBuffer(null);
		mTexCoordVbo = VBO.createVertexBuffer(null);
		mIndexVbo    = VBO.createIndexBuffer(null);
		mBufferSet   = new VAO();
		mBufferSet.setVbo(0, mPositionVbo, 3);
		mBufferSet.setVbo(1, mTexCoordVbo, 2);		
		
		final int W = 4*Constants.WORLD_SIZE_X;
		final int H = 4*Constants.WORLD_SIZE_Y;
		final float du = 0.5f/Constants.WORLD_SIZE_X;
		final float dv = 0.5f/Constants.WORLD_SIZE_Y;
		final int S = W*H;
		float[] vertexData   = new float[S*3];
		float[] texCoordData = new float[S*2];
		int[]   indexData    = new int  [S*6];
		
		float xScale = 1.0f/W;
		float zScale = 1.0f/H;
		float yScale = (float)(0.25*Math.sqrt(S));
		float xOffset = -W/2.0f;
		float zOffset = -H/2.0f;
		
		int index = 0;
		for (int x = 0; x < W; ++x) {
			for (int z = 0; z < H; ++z) {
				vertexData[3*index+0] = (x + xOffset)*0.25f;
				vertexData[3*index+1] = 0;
				vertexData[3*index+2] = (z + zOffset)*0.25f;
				
				texCoordData[2*index+0] = (z*zScale + du);
				texCoordData[2*index+1] = (x*xScale + dv);
				
				++index;
			}
		}
				
		mNumIndices = 0;
		for (int x = 0; x < W-1; ++x) {
			for (int z = 0; z < H-1; ++z) {
				indexData[mNumIndices+0] = W*z     + x;
				indexData[mNumIndices+1] = W*z     + x + 1;
				indexData[mNumIndices+2] = W*(z+1) + x;
				
				indexData[mNumIndices+3] = W*z     + x + 1;
				indexData[mNumIndices+4] = W*(z+1) + x + 1;
				indexData[mNumIndices+5] = W*(z+1) + x;
				
				mNumIndices+=6;
			}
		}
		
		mPositionVbo.load(vertexData);
		mTexCoordVbo.load(texCoordData);
		mIndexVbo.load(indexData);
	}
	
	private void initSimulationTextures() {
		FloatBuffer heightBuffer = BufferUtils.createFloatBuffer(Constants.WORLD_SIZE*4);;
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			float h = World.terrain.height[i];
			float w = h < 0.5f ? 0.5f-h : 0.0f;
			h *= mHeightScale;
			w *= mHeightScale;
			heightBuffer.put(i*4+0, h);
			heightBuffer.put(i*4+1, 0);
			heightBuffer.put(i*4+2, 0);
		}
		
		mHeightTexture[0] = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
		mHeightTexture[1] = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
		
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			heightBuffer.put(i*4+0, 0);
			heightBuffer.put(i*4+1, 0);
			heightBuffer.put(i*4+2, 0);
			heightBuffer.put(i*4+3, 0);
		}
		
		mFluxTexture[0]     = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
		mFluxTexture[1]     = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
		mVelocityTexture[0] = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
		mVelocityTexture[1] = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, heightBuffer);
		
		mSimulationFbo = new FBO();
		mSimulationFbo.setColorAttachment(0, mHeightTexture[mDstIndex]);
		mSimulationFbo.setColorAttachment(1, mFluxTexture[mDstIndex]);
		
	}
}
