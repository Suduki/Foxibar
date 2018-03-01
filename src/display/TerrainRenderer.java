package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import constants.Constants;
import gpu.VAO;
import gpu.FBO;
import gpu.GpuE;
import gpu.Program;
import gpu.Shader;
import gpu.GpuUtils;
import gpu.VBO;
import gui.KeyboardState;
import gui.MouseEvent;
import gui.MouseState;
import gui.Region;
import world.World;
import static org.lwjgl.glfw.GLFW.*;

public class TerrainRenderer implements gui.SceneRegionRenderer {
	// Visualisation.
	private Region              mRegion           = null;
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
	private boolean             mSimulateOnRender      = false;
	private int                 mIterationsPerFrame    = 1;
	private int                 mSrcIndex              = 0;
	private int                 mDstIndex              = 1;
	private float               mHeightScale           = 1;
	private float               mRain                  = 1.0f;
	private Texture[]           mHeightTexture         = new Texture[2];
	private Texture[]           mFluxTexture           = new Texture[2];
	private Texture[]           mVelocityTexture       = new Texture[2];
	private FBO                 mSimulationFbo         = null;		
	private Program             mFluxUpdateProgram     = null;
	private Program             mWaterUpdateProgram    = null;
	private Program             mSedimentUpdateProgram = null;
	
	public TerrainRenderer(Window window) {
		System.out.println("WORLD_SIZE_X = " + Constants.WORLD_SIZE_X + ", WORLD_SIZE_Y = " + Constants.WORLD_SIZE_Y);
		
		mCamera = new Camera();
		mCameraController = new FlyCameraController(mCamera);
		
		initVertexArrays();
		initVisualisationShaderPrograms();
		initVisualisationTextures();
		
		mHeightScale = (float)(0.25*Math.sqrt(Constants.WORLD_SIZE));
		initSimulationShaderPrograms();
		initSimulationTextures();
	}
	
	public void setIterationsPerFrame(int pIterations) {
		mIterationsPerFrame = pIterations;
	}
	
	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		if (pEvent == MouseEvent.BUTTON) {
			int button = pMouse.getButtonIndex();
			int action = pMouse.getButtonState(button) ? GLFW_PRESS : GLFW_RELEASE;
			mCameraController.handleMouseEvents(0, button, action, 0);
			
		}
		else if (pEvent == MouseEvent.MOTION) {
			mCameraController.handleMouseMotion(0, pMouse.getPos().x, pMouse.getPos().y);
		}
		
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pKeyboard) {
		System.out.println("Key[" + pKeyboard.getKeyIndex() + "]: " + pKeyboard.getKeyState());
		mCameraController.handleKeyboardEvents(pKeyboard.getKeyState() ? GLFW_PRESS : GLFW_RELEASE, pKeyboard.getKeyIndex());
		// TODO Auto-generated method stub
		return false;
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
		
		mSedimentUpdateProgram = new Program();
		mSedimentUpdateProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simVertex));
		mSedimentUpdateProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simSedimentUpdateFragment));
		mSedimentUpdateProgram.link();		
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
	
	void drawTerrain(Matrix4f translationMatrix) {
		float[] matrixBuffer = new float[16];
		
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
		glUniformMatrix4fv(1, false, new Matrix4f(mCamera.getViewMatrix()).mul(translationMatrix).get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniform1f(3, 1.0f/mHeightScale);
		
		mBufferSet.bind();
		mIndexVbo.bind();
		glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();

		if (mRain > 0) { // TODO: This means "draw water"...
			glDepthFunc(GL_LESS);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			mWaterProgram.bind();
	
			Vector3f e = mCamera.getEyePosition();
			glUniformMatrix4fv(0, false, mCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
			glUniformMatrix4fv(1, false, new Matrix4f(mCamera.getViewMatrix()).mul(translationMatrix).get(matrixBuffer)); GpuUtils.GpuErrorCheck();
			glUniform4f(2, e.x, e.y, e.z, 1.0f); GpuUtils.GpuErrorCheck();
			
			mIndexVbo.bind();
			glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();
			Program.unbind();
		}
		
		Program.unbind();
	}
	
	void swapTextures(Texture[] texture) {
		Texture tmp = texture[0];
		texture[0] = texture[1];
		texture[1] = tmp;
	}
	static final int[] mSimDrawBuffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2};
	
	@Override // SceneRegionRenderer
	public void render(int pViewportWidth, int pViewportHeight) {
		System.out.println("TerrainRenderer.render");
		if (mSimulateOnRender) {
			simulate();
		}
		mCamera.setAspectRatio(pViewportWidth/(float)pViewportHeight);
		mCameraController.update();
		
		
		drawTerrain(new Matrix4f());
		glUseProgram(0);
	}

	public void simulate() {
		glPushAttrib(GL_VIEWPORT_BIT);
		for (int i = 0; i < mIterationsPerFrame; ++i) {
			stepSimulation();
		}
		glPopAttrib();
		glUseProgram(0);
	}
	
	public void toggleSimulateOnRender() {
		mSimulateOnRender = !mSimulateOnRender;
	}
	
	private void stepSimulation() {
		// Calculate new flux.
		mHeightTexture[mSrcIndex].bind(0);
		mFluxTexture[mSrcIndex].bind(1);
		mSimulationFbo.bind();
		glDrawBuffers(mSimDrawBuffers); GpuUtils.GpuErrorCheck();
		
		mFluxUpdateProgram.bind();
		glUniform1f(0, 1.0f/Constants.WORLD_SIZE_X);
		glUniform1f(3, 0.0032f);
		glViewport(0, 0, Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
		glDrawArrays(GL_TRIANGLES, 0, 6); GpuUtils.GpuErrorCheck();
		
		FBO.unbind();
		
		// Calculate new water and erode.
		swapTextures(mHeightTexture);
		swapTextures(mFluxTexture);
		mSimulationFbo.setColorAttachment(0, mHeightTexture[mDstIndex]);
		mSimulationFbo.setColorAttachment(1, mFluxTexture[mDstIndex]);
		mHeightTexture[mSrcIndex].bind(0);
		mFluxTexture[mSrcIndex].bind(1);
		mStrataTexture.bind(3);
		mSimulationFbo.bind();
		glDrawBuffers(mSimDrawBuffers); GpuUtils.GpuErrorCheck();
		
		mWaterUpdateProgram.bind();
		glUniform1f(0, 1.0f/Constants.WORLD_SIZE_X);
		glUniform1f(3, 1.0f/mHeightScale);
		glViewport(0, 0, Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
		glDrawArrays(GL_TRIANGLES, 0, 6); GpuUtils.GpuErrorCheck();
		
		FBO.unbind();
		
		// Calculate sediment transport.
		swapTextures(mHeightTexture);
		swapTextures(mVelocityTexture);
		mHeightTexture[mSrcIndex].bind(0);
		mVelocityTexture[mSrcIndex].bind(1);
		mFluxTexture[mSrcIndex].bind(2);
		mSimulationFbo.setColorAttachment(0, mHeightTexture[mDstIndex]);		
		mSimulationFbo.setColorAttachment(1, mVelocityTexture[mDstIndex]);
		mSimulationFbo.bind();
		glDrawBuffers(mSimDrawBuffers); GpuUtils.GpuErrorCheck();
		
		mSedimentUpdateProgram.bind();
		//glUniform1f(0, 1.0f/Constants.WORLD_SIZE_X); GpuUtils.GpuErrorCheck();
		//glUniform1f(3, 1.0f/mHeightScale); GpuUtils.GpuErrorCheck();
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
			float h = (float)Math.pow(World.terrain.height[i], 1.5);
			h *= mHeightScale;
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

	@Override
	public void setRegion(Region region) {
		mRegion = region;
	}
}
