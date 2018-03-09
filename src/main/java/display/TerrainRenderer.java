package display;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.FloatBuffer;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import agents.Animal;
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
	private Texture             mTestTexture      = null;
	private Texture             mColorTexture     = null;
	private TextureCube         mSkyboxTexture    = null;
	private VBO                 mPositionVbo      = null;
	private VBO                 mTexCoordVbo      = null;
	private VBO                 mIndexVbo         = null;
	private VAO                 mBufferSet        = null;	
	private int                 mNumIndices       = 0;
	private Program             mTerrainProgram   = null;
	private Program             mWaterProgram     = null;
	
	// Bonus hex terrain :)
	private Program mHexTerrainProgram = null;
	private VAO     mHexBufferSet      = null;
	private VBO     mHexVertexVbo      = null;
	private VBO     mHexNormalVbo      = null;
	private VBO     mHexTexCoordVbo    = null;
	private VBO     mHexInstanceVbo    = null;
	private VBO     mHexIndexVbo       = null;
	private int     mHexVertexCount    = 0;
	private int     mHexInstanceCount  = 0;
	private float   mHexFlatness       = 0.5f;
	
	
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
		
		mHeightScale = 0.25f*(float)(0.25*Math.sqrt(Constants.WORLD_SIZE));
		initSimulationShaderPrograms();
		initSimulationTextures();
	}
	
	public void setIterationsPerFrame(int pIterations) {
		mIterationsPerFrame = pIterations;
	}
	
	public void setFlatness(float pFlatness) {
		mHexFlatness = Math.max(0.001f, Math.min(1.0f, 1.0f-pFlatness));
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
		
		// Bonus hex terrain :)
		mHexTerrainProgram = new Program();
		mHexTerrainProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.hexVertex));
		mHexTerrainProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.hexFragment));
		mHexTerrainProgram.link("HexTerrainProgram");
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
		mSkyboxTexture.loadFacesFromFile(
				"pics/skybox/right.png",
				"pics/skybox/left.png",
				"pics/skybox/top.png",
				"pics/skybox/bottom.png",
				"pics/skybox/front.png",
				"pics/skybox/back.png");
	}
	
	void updateColorTexture() {
		FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(Constants.WORLD_SIZE*4);
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			World.updateColor(LegacyRenderer.terrainColor, i);
			
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
				colorBuffer.put(i*4+0, LegacyRenderer.terrainColor[i][0]);
				colorBuffer.put(i*4+1, LegacyRenderer.terrainColor[i][1]);
				colorBuffer.put(i*4+2, LegacyRenderer.terrainColor[i][2]);
			}
		}
		
		if (mColorTexture == null) {
			mColorTexture = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, colorBuffer);
			mColorTexture.filterNearest();
		}
		else {
			mColorTexture.load(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, colorBuffer);
		}
	}	
	
	void drawTerrain(Matrix4f translationMatrix) {
		float[] matrixBuffer = new float[16];
		
		glClearColor(0.25f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
	//	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
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
		//glDrawElements(GL_TRIANGLES, mNumIndices, GL_UNSIGNED_INT, 0); GpuUtils.GpuErrorCheck();

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

	void drawGrass() {
		int i = 0;
		float x0 = -Constants.WORLD_SIZE_X/2.0f;
		float z0 = -Constants.WORLD_SIZE_Y/2.0f;
		
		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;
		
		glLineWidth(10);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glBegin(GL_LINES);
		glColor3f(0,0,0);
		for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
			for (int x = 0; x < Constants.WORLD_SIZE_X; x+=1) {
				float height  = World.grass.height[i];
				if (height > 0.3f) {
					float xScale = (float)(Math.sqrt(3)*0.5);
					float zScale = 1.5f;

					int hexX = x/2;
					int hexZ = z/2; 
					
					float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;
					
					float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
					float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);
					
					renderGrassAt(height*2, xpos, zpos, i);
				}
				++i;
			}
		}
		glEnd();
		glLineWidth(1);
	}
	
	
	private void renderGrassAt(float height, float x, float z, int pos) {
		float[] c = Constants.Colors.GRASS_STRAW;
		float y = (float)Math.pow(World.terrain.height[pos], 1.5);
		y *= mHeightScale;
		glColor4f(c[0],c[1],c[2], 0.9f);
		float xWind = 1f-2*World.terrain.getWindX(pos);
		float zWind = 1f-2*World.terrain.getWindZ(pos);
		
		int numSplits = 4;
		float X = 0;
		float Y = 0;
		float Z = 0;
		for (int i = 0; i < numSplits; ++i) {
			glVertex3f(x + X,y + Y,z + Z);
			float dX = World.terrain.getWindDeltaAtY(xWind, Y);
			float dZ = World.terrain.getWindDeltaAtY(zWind, Y);
			if (dX * dX + dZ * dZ > height/numSplits*height/numSplits) {
				// Completely horizontal line
				Y += 0;
				float factor = (float) Math.sqrt(dX*dX + dZ*dZ) * numSplits / height;
				X += dX/factor;
				Z += dZ/factor;
			}
			else {
				X += dX;
				Z += dZ;
				Y += (float) Math.sqrt((height/numSplits)*(height/numSplits) - dX*dX - dZ*dZ);
			}
			glVertex3f(x + X,y + Y,z + Z);
		}
		
	}
	

	void drawAnimals() {
		int i = 0;
		float x0 = -Constants.WORLD_SIZE_X/2.0f;
		float z0 = -Constants.WORLD_SIZE_Y/2.0f;
		
		float xNudge = (float)(Math.sqrt(3.0f)*0.2f);
		float zNudge = 3.0f/9.0f;
		
		glLineWidth(10);
		glBegin(GL_LINES);
		glColor3f(0,0,0);
		for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
			for (int x = 0; x < Constants.WORLD_SIZE_X; x+=1) {
				// RENDER ANIMAL
				int id  = Animal.containsAnimals[i];
				if (id != -1) {
					float xScale = (float)(Math.sqrt(3)*0.5);
					float zScale = 1.5f;
										
					int hexX = x/2;
					int hexZ = z/2; 
					
					float xPosOffset = (hexZ%2 == 1) ? xScale : 0.0f;
					
					float xpos = x0 + hexX*2*xScale + xPosOffset + ((x%2 == 0) ? -xNudge : xNudge);
					float zpos = z0 + hexZ*zScale + ((z%2 == 0) ? -zNudge : zNudge);
					
					renderAnimalAt(id, xpos, zpos);
				}
				
				++i;
			}
		}
		glEnd();
		glLineWidth(1);
	}
	
	void renderAnimalAt(int id, float x, float z) {
		Animal animal = Animal.pool[id];
		float[] c = animal.secondaryColor;
		float h = (float)Math.pow(World.terrain.height[animal.pos], 1.5);
		h *= mHeightScale;
		glColor3f(c[0],c[1],c[2]);
		glVertex3f(x,h,z);
		glVertex3f(x,h+1,z);
	}
	
	void drawHexTerrain(Matrix4f translationMatrix) {
		float[] matrixBuffer = new float[16];
		
		updateColorTexture();
		
		glClearColor(0.0f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
		glEnable(GL_DEPTH_TEST); GpuUtils.GpuErrorCheck();
		glEnable(GL_CULL_FACE); GpuUtils.GpuErrorCheck();
		
		glDepthFunc(GL_LEQUAL);
		mHexTerrainProgram.bind();
		//glUniform1f(3, 1.0f/mHeightScale); GpuUtils.GpuErrorCheck();
		glUniform1f(4, mHexFlatness); GpuUtils.GpuErrorCheck();
		
		mHeightTexture[mSrcIndex].bind(0);
		mStrataTexture.bind(1);
		//mDetailTexture.bind(2);
		//mTestTexture.bind(2);
		mColorTexture.bind(2);
		
		glUniformMatrix4fv(0, false, mCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniformMatrix4fv(1, false, new Matrix4f(mCamera.getViewMatrix()).mul(translationMatrix).get(matrixBuffer)); GpuUtils.GpuErrorCheck();		
		mHexBufferSet.bind();
		glDrawArraysInstanced(GL_TRIANGLES, 0, mHexVertexCount, mHexInstanceCount);
		
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
		if (mSimulateOnRender) {
			simulate();
		}
		mCamera.setAspectRatio(pViewportWidth/(float)pViewportHeight);
		mCameraController.update();

		Matrix4f m;
		m = new Matrix4f();
		drawHexTerrain(m.translate(17, 0, 33));

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
		
		drawAnimals();
		drawGrass();
		
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
		mBufferSet.setVbo(0, mPositionVbo, 3, 0);
		mBufferSet.setVbo(1, mTexCoordVbo, 2, 0);		
		
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
		
		buildHexBuffers();
	}
	
	// Bonus hex terrain :)
	public void buildHexBuffers() {
		
		class VertexBuilder {
			public float[] mPositionData;
			public float[] mNormalData;
			public float[] mTexCoordData;
			public float[] mInstanceData;
			public int mIndex;
			public int mInstanceIndex;
			
			VertexBuilder(int pNumVertices, int pNumInstances) {
				mPositionData = new float[pNumVertices*3];
				mNormalData   = new float[pNumVertices*3];
				mTexCoordData = new float[pNumVertices*2];
				mInstanceData = new float[pNumInstances*4];
				mIndex = 0;
				mInstanceIndex = 0;
			}
			
			public void addVertex(float px, float py, float pz, float nx, float ny, float nz, float u, float v) {
				mPositionData[3*mIndex+0] = px;
				mPositionData[3*mIndex+1] = py;
				mPositionData[3*mIndex+2] = pz;
				
				mNormalData[3*mIndex+0] = nx;
				mNormalData[3*mIndex+1] = ny;
				mNormalData[3*mIndex+2] = nz;
				
				mTexCoordData[2*mIndex+0] = u;
				mTexCoordData[2*mIndex+1] = v;
				++mIndex;
			}
			
			public void addInstance(float pPosX, float pPosZ, float pTexU, float pTexV) {
				mInstanceData[mInstanceIndex+0] = pPosX;
				mInstanceData[mInstanceIndex+1] = pPosZ;
				mInstanceData[mInstanceIndex+2] = pTexU;
				mInstanceData[mInstanceIndex+3] = pTexV;
				mInstanceIndex += 4;
			}
			
			public int getNumVertices() {
				return mIndex;
			}
			
			public int getNumInstances() {
				return mInstanceIndex/4;
			}
			
			VBO buildPositionVBO() {
				return VBO.createVertexBuffer(mPositionData);
			}
			
			VBO buildNormalVBO() {
				return VBO.createVertexBuffer(mNormalData);
			}
			
			VBO buildInstanceVBO() {
				return VBO.createVertexBuffer(mInstanceData);
			}
			
			VBO buildTexCoordVBO() {
				return VBO.createVertexBuffer(mTexCoordData);
			}
		}
		
		VertexBuilder builder = new VertexBuilder(3*3*6, (Constants.WORLD_SIZE_X/2)*(Constants.WORLD_SIZE_Y/2));
		
		float tc[][] = {
				{0,0},{ 0, 1},{ 1, 1}, 
				{0,0},{-1, 1},{ 0, 1},
				{0,0},{-1,-1},{-1, 1},
				{0,0},{ 0,-1},{-1,-1},
				{0,0},{ 1,-1},{ 0,-1},
				{0,0},{ 1, 1},{ 1,-1}
		};
		
		float us = 1.0f/(Constants.WORLD_SIZE_X);
		float vs = 1.0f/(Constants.WORLD_SIZE_Y);
		
		float da = (float)(Math.PI*2.0/6.0);
		for (int i = 0; i < 6; ++i) {
			float a0 = da*(i+0.5f);
			float a1 = da*(i+1.5f);
			float an = da*(i+1);
			
			float x0 = (float)Math.cos(a0);
			float x1 = (float)Math.cos(a1);
			float z0 = (float)Math.sin(a0);
			float z1 = (float)Math.sin(a1);
			float xn = (float)Math.cos(an);
			float zn = (float)Math.sin(an);
		
			builder.addVertex( 0, 1,  0, 0, 1, 0, us*tc[i*3+0][0], vs*tc[i*3+0][1]);
			builder.addVertex(x1, 1, z1, 0, 1, 0, us*tc[i*3+1][0], vs*tc[i*3+1][1]);
			builder.addVertex(x0, 1, z0, 0, 1, 0, us*tc[i*3+2][0], vs*tc[i*3+2][1]);
			
			builder.addVertex(x1, 1, z1, xn, 0, zn, us*tc[i*3+1][0], vs*tc[i*3+1][1]);
			builder.addVertex(x0, 0, z0, xn, 0, zn, us*tc[i*3+2][0], vs*tc[i*3+2][1]);
			builder.addVertex(x0, 1, z0, xn, 0, zn, us*tc[i*3+2][0], vs*tc[i*3+2][1]);
			
			builder.addVertex(x0, 0, z0, xn, 0, zn, us*tc[i*3+2][0], vs*tc[i*3+2][1]);
			builder.addVertex(x1, 1, z1, xn, 0, zn, us*tc[i*3+1][0], vs*tc[i*3+1][1]);
			builder.addVertex(x1, 0, z1, xn, 0, zn, us*tc[i*3+1][0], vs*tc[i*3+1][1]);
		}
		
		float du = 2.0f/Constants.WORLD_SIZE_X;
		float dv = 2.0f/Constants.WORLD_SIZE_Y;
		
		float x0 = -Constants.WORLD_SIZE_X/2.0f;
		float z0 = -Constants.WORLD_SIZE_Y/2.0f;
		
		float xScale = (float)(Math.sqrt(3)*0.5);
		float zScale = 1.5f;
		for (int x = 0; x < Constants.WORLD_SIZE_X/2; ++x) {
			for (int z = 0; z < Constants.WORLD_SIZE_Y/2; ++z) {
				float xPosOffset = (Math.abs(z)%2 == 1) ? xScale : 0.0f;
				float xTexOffset = (Math.abs(z)%2 == 1) ? du*0.5f : 0.0f;
				builder.addInstance(
						x0 + x*2*xScale + xPosOffset,
						z0 + z*zScale,
						0.5f*du + du*x + xTexOffset,
						0.5f*dv + dv*z);
			}
		}
		
		mHexVertexVbo = builder.buildPositionVBO();
		mHexNormalVbo = builder.buildNormalVBO();
		mHexTexCoordVbo = builder.buildTexCoordVBO();
		mHexInstanceVbo = builder.buildInstanceVBO();
		mHexBufferSet = new VAO();
		mHexBufferSet.setVbo(0, mHexVertexVbo,   3, 0); // TODO: 3 components, get from builder.
		mHexBufferSet.setVbo(1, mHexNormalVbo,   3, 0); // TODO: 3 components, get from builder.
		mHexBufferSet.setVbo(2, mHexTexCoordVbo, 2, 0); // TODO: 2 components, get from builder.
		mHexBufferSet.setVbo(3, mHexInstanceVbo, 4, 1); // TODO: 4 components, get from builder.
		
		mHexVertexCount   = builder.getNumVertices();
		mHexInstanceCount = builder.getNumInstances();		
	}
	
	private void initSimulationTextures() {
		FloatBuffer heightBuffer = BufferUtils.createFloatBuffer(Constants.WORLD_SIZE*4);
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