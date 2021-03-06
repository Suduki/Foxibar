package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import agents.Animal;
import agents.Brainler;
import constants.Constants;
import display.hex.AgentRenderer;
import display.hex.GrassRenderer;
import display.hex.HexTerrainRenderer;
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
import gui.RegionI;
import main.Main;
import world.World;
import static org.lwjgl.glfw.GLFW.*;

public class TerrainRenderer implements gui.SceneRegionRenderer {
	// Visualisation.
	private RegionI              mRegion          = null;
	private Camera              mCamera           = null;
	private FlyCameraController mCameraController = null;
	private Texture             mStrataTexture    = null;
	private Texture             mDetailTexture    = null;
	private Texture             mWaterTexture     = null;
	private Texture             mTestTexture      = null;
	private Texture             mColorTexture     = null;
	private TextureCube         mSkyboxTexture    = null;
	private VBO                 mPositionVbo      = null;
	private VBO                 mTexCoordVbo      = null;
	private VBO                 mIndexVbo         = null;
	private VBO                 mSkyboxVbo        = null;
	private VAO                 mBufferSet        = null;
	private int                 mNumIndices       = 0;
	private Program             mTerrainProgram   = null;
	private Program             mWaterProgram     = null;
	private Program             mSkyboxProgram    = null;
	private GrassRenderer		mGrassRenderer    = null;
	private AgentRenderer		mAgentRenderer    = null;
	private Set<FrameUpdatable> mUpdatables       = null;
	
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
	HexTerrainRenderer			mHexTerrainRenderer    = null;
	
	public TerrainRenderer(Window window) {
		System.out.println("WORLD_SIZE_X = " + Main.mSimulation.WORLD_SIZE_X + ", WORLD_SIZE_Y = " + Main.mSimulation.WORLD_SIZE_Y);
		
		mCamera = new Camera();
		mCameraController = new FlyCameraController(mCamera);
		
		mHexTerrainRenderer = new HexTerrainRenderer();
		mGrassRenderer = new GrassRenderer();
		mAgentRenderer = new AgentRenderer();
		mUpdatables = new HashSet<FrameUpdatable>();
		
		initVertexArrays();
		initVisualisationShaderPrograms();
		initVisualisationTextures();
		
		mHeightScale = 0.5f*(float)(0.25*Math.sqrt(Main.mSimulation.WORLD_SIZE));
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
		mTerrainProgram.link("TerrainProgram");
		
		mWaterProgram = new Program();
		mWaterProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.waterVertex));
		mWaterProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.waterFragment));
		mWaterProgram.link("WaterProgram");
		
		mSkyboxProgram= new Program();
		mSkyboxProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.skyboxVertex));
		mSkyboxProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.skyboxFragment));
		mSkyboxProgram.link("SkyboxProgram");
	}
	
	private void initSimulationShaderPrograms() {
		mFluxUpdateProgram = new Program();
		mFluxUpdateProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simVertex));
		mFluxUpdateProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simFluxUpdateFragment));
		mFluxUpdateProgram.link("FluxUpdateProgram");
		
		mWaterUpdateProgram = new Program();
		mWaterUpdateProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simVertex));
		mWaterUpdateProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simWaterUpdateFragment));
		mWaterUpdateProgram.link("WaterUpdateProgram");
		
		mSedimentUpdateProgram = new Program();
		mSedimentUpdateProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.simVertex));
		mSedimentUpdateProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.simSedimentUpdateFragment));
		mSedimentUpdateProgram.link("SedimantUpdateProgram");		
	}
	
	private void initVisualisationTextures() {
		mStrataTexture = Texture.fromFile("pics/strata.png");
		mDetailTexture = Texture.fromFile("pics/detail.png");
		//mDetailTexture.filterNearest();
		mWaterTexture  = Texture.fromFile("pics/water.png");
		//mTestTexture   = Texture.fromFile("pics/checkers256.png");
		mTestTexture   = Texture.fromFile("pics/GuiDefault.png");
		mTestTexture.filterNearest();
		mSkyboxTexture = new TextureCube();
		
		mUpdatables.add(mSkyboxTexture);
		
		String skyboxPath = "pics/skybox_cool/";
		String fileType = ".jpg";
		mSkyboxTexture.loadFacesFromFileAsync(
				skyboxPath + "right" + fileType,
				skyboxPath + "left" + fileType,
				skyboxPath + "top" + fileType,
				skyboxPath + "bottom" + fileType,
				skyboxPath + "front" + fileType,
				skyboxPath + "back" + fileType);
	}
	
	FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(Main.mSimulation.WORLD_SIZE*4);
	public void updateColorTexture() {
		int i = 0;
		Main.mSimulation.mWorld.updateColor(LegacyRenderer.terrainColor);
		for (int x = 0; x < Main.mSimulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Main.mSimulation.WORLD_SIZE_Y; ++y, ++i) {


				if (i == 0) {
					colorBuffer.put(i*4+0, 0);
					colorBuffer.put(i*4+1, 0);
					colorBuffer.put(i*4+2, 0);
				}
				else if (i == 1) {
					colorBuffer.put(i*4+0, 1);
					colorBuffer.put(i*4+1, 0);
					colorBuffer.put(i*4+2, 0);
				}
				else {
					colorBuffer.put(i*4+0, LegacyRenderer.terrainColor[y][x][0]);
					colorBuffer.put(i*4+1, LegacyRenderer.terrainColor[y][x][1]);
					colorBuffer.put(i*4+2, LegacyRenderer.terrainColor[y][x][2]);
				}
			}
		}
		
		if (mColorTexture == null) {
			mColorTexture = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, colorBuffer);
			mColorTexture.filterNearest();
		}
		else {
			mColorTexture.load(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, colorBuffer);
		}
	}
	
	void drawTerrain(Matrix4f translationMatrix) {
		float[] matrixBuffer = new float[16];
		
		glClearColor(0.25f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
//		glClear(GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
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
//		glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();

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
			glDisable(GL_BLEND);
		}
		
		Program.unbind();
	}
	
	void drawSkybox() {
		float[] matrixBuffer = new float[16];
		
		//glClearColor(0.25f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
	//	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
		glDisable(GL_DEPTH_TEST); GpuUtils.GpuErrorCheck();
		glDisable(GL_CULL_FACE); GpuUtils.GpuErrorCheck();
		
		mSkyboxTexture.bind(0);
		
		Matrix3f rot = new Matrix3f();
		mCamera.getViewMatrix().get3x3(rot);
		
		Matrix4f mat = new Matrix4f(rot);
		
		//glDepthFunc(GL_LEQUAL);
		mSkyboxProgram.bind();		
		glUniformMatrix4fv(0, false, mCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniformMatrix4fv(1, false, mat.get(matrixBuffer)); GpuUtils.GpuErrorCheck();		
		mBufferSet.bind();
		
		glDrawArrays(GL_TRIANGLES, 0, 36);
		//glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();

		
		Program.unbind();
		
		glEnable(GL_CULL_FACE); GpuUtils.GpuErrorCheck();
		glEnable(GL_DEPTH_TEST); GpuUtils.GpuErrorCheck();
	}

	
	
	void swapTextures(Texture[] texture) {
		Texture tmp = texture[0];
		texture[0] = texture[1];
		texture[1] = tmp;
	}
	static final int[] mSimDrawBuffers = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2};
	
	@Override // SceneRegionRenderer
	public void render(int pViewportWidth, int pViewportHeight) {
		if (mSimulateOnRender)
		{
			simulate();
		}
		
		if (mUpdatables.size() > 0)
		{
			Set<FrameUpdatable> nextFrame = new HashSet<FrameUpdatable>();
			for (FrameUpdatable updatable : mUpdatables)
			{
				if (updatable.frameUpdate())
				{
					nextFrame.add(updatable);
				}
			}
			
			mUpdatables = nextFrame;
		}
		
		mCamera.setAspectRatio(pViewportWidth/(float)pViewportHeight);
		mCameraController.update();
		
		updateColorTexture();
		
		Matrix4f m;
		m = new Matrix4f();
		drawSkybox();
		mHexTerrainRenderer.drawHexTerrain(m.translate(17, 0, 33), mHeightTexture, mSrcIndex, mStrataTexture, mColorTexture, mCamera);
		
		m = new Matrix4f();
		float q = 0.75f;
		float p = (float)(Math.sqrt(3)/2.0);
		drawTerrain(m.scale(p, 1, q));
		glUseProgram(0);

		float[] matrixBuffer = new float[16];
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadMatrixf(mCamera.getProjectionMatrix().get(matrixBuffer));
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		m = new Matrix4f();
		glLoadMatrixf(new Matrix4f(mCamera.getViewMatrix()).mul(m.translate(17, 0, 33)).get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_QUADS);
		mAgentRenderer.drawAgents(mHeightScale);
		mGrassRenderer.drawGrass(mHeightScale);
		glEnd();
		
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		glPopMatrix();
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
		glUniform1f(0, 1.0f/Main.mSimulation.WORLD_SIZE_X);
		glUniform1f(3, 0.0032f);
		glViewport(0, 0, Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
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
		glUniform1f(0, 1.0f/Main.mSimulation.WORLD_SIZE_X);
		glUniform1f(3, 1.0f/mHeightScale);
		glViewport(0, 0, Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
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
		//glUniform1f(0, 1.0f/Simulation.WORLD_SIZE_X); GpuUtils.GpuErrorCheck();
		//glUniform1f(3, 1.0f/mHeightScale); GpuUtils.GpuErrorCheck();
		glViewport(0, 0, Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y); GpuUtils.GpuErrorCheck();
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
		mSkyboxVbo   = VBO.createIndexBuffer(null);
		mBufferSet   = new VAO();
		mBufferSet.setVbo(0, mPositionVbo, 3, 0);
		mBufferSet.setVbo(1, mTexCoordVbo, 2, 0);
				
		final int W = 4*Main.mSimulation.WORLD_SIZE_X;
		final int H = 4*Main.mSimulation.WORLD_SIZE_Y;
		final float du = 0.5f/Main.mSimulation.WORLD_SIZE_X;
		final float dv = 0.5f/Main.mSimulation.WORLD_SIZE_Y;
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
		FloatBuffer heightBuffer = BufferUtils.createFloatBuffer(Main.mSimulation.WORLD_SIZE*4);
		int i = 0;
		for (int x = 0; x < Main.mSimulation.WORLD_SIZE_X; ++x) {
			for (int y = 0; y < Main.mSimulation.WORLD_SIZE_Y; ++y,++i) {
				float h = (float)Math.pow(Main.mSimulation.mWorld.terrain.height[y][x], 1.5);
				h *= mHeightScale;
				heightBuffer.put(i*4+0, h);
				heightBuffer.put(i*4+1, 0);
				heightBuffer.put(i*4+2, 0);
			}
		}

		mHeightTexture[0] = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, heightBuffer);
		mHeightTexture[1] = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, heightBuffer);
		
		for (i = 0; i < Main.mSimulation.WORLD_SIZE; ++i) {
			heightBuffer.put(i*4+0, 0);
			heightBuffer.put(i*4+1, 0);
			heightBuffer.put(i*4+2, 0);
			heightBuffer.put(i*4+3, 0);
		}
		
		mFluxTexture[0]     = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, heightBuffer);
		mFluxTexture[1]     = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, heightBuffer);
		mVelocityTexture[0] = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, heightBuffer);
		mVelocityTexture[1] = new Texture(Main.mSimulation.WORLD_SIZE_X, Main.mSimulation.WORLD_SIZE_Y, heightBuffer);
		
		mSimulationFbo = new FBO();
		mSimulationFbo.setColorAttachment(0, mHeightTexture[mDstIndex]);
		mSimulationFbo.setColorAttachment(1, mFluxTexture[mDstIndex]);
		
	}

	@Override
	public void setRegion(RegionI region) {
		mRegion = region;
	}

	public void setDrawGrass() {
		mGrassRenderer.setDrawGrass();
	}

	public void resetGrass() {
		Main.mSimulation.resetWorld(false);
	}
}
