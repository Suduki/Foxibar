package display;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import main.Main;

import org.joml.Matrix4f;

import constants.Constants;
import gpu.GpuE;
import gpu.GpuUtils;
import gpu.Program;
import gpu.Shader;
import gpu.VAO;
import gpu.VBO;

public class HexTerrainRenderer {
	public HexTerrainRenderer() {
		init();
	}

	// Bonus hex terrain :)
	private Program mHexTerrainProgram = null;
	private VAO     mHexBufferSet      = null;
	private VBO     mHexVertexVbo      = null;
	private VBO     mHexNormalVbo      = null;
	private VBO     mHexTexCoordVbo    = null;
	private VBO     mHexInstanceVbo    = null;
	private VBO     mHexTexVbo         = null;
	private VBO     mHexIndexVbo       = null;
	private int     mHexVertexCount    = 0;
	private int     mHexInstanceCount  = 0;
	private float   mHexFlatness       = 0.5f;

	public void setFlatness(float pFlatness) {
		mHexFlatness = Math.max(0.001f, Math.min(1.0f, 1.0f-pFlatness));
	}

	public void init() {
		// Bonus hex terrain :)
		mHexTerrainProgram = new Program();
		mHexTerrainProgram.attachVertexShader(new Shader(GpuE.VERTEX_SHADER, ShaderSource.hexVertex));
		mHexTerrainProgram.attachFragmentShader(new Shader(GpuE.FRAGMENT_SHADER, ShaderSource.hexFragment));
		mHexTerrainProgram.link("HexTerrainProgram");
		
		buildHexBuffers();
	}
	
	
	void drawHexTerrain(Matrix4f translationMatrix, Texture[] pHeightTexture, int pSrcIndex, Texture pStrataTexture, Texture pColorTexture, Camera pCamera) {
		float[] matrixBuffer = new float[16];
		
		glClearColor(0.0f,0.5f,1.0f,1); GpuUtils.GpuErrorCheck();
		glClear(GL_DEPTH_BUFFER_BIT); GpuUtils.GpuErrorCheck();
		
		glEnable(GL_DEPTH_TEST); GpuUtils.GpuErrorCheck();
		glEnable(GL_CULL_FACE); GpuUtils.GpuErrorCheck();
		
		glDepthFunc(GL_LEQUAL);
		mHexTerrainProgram.bind();
		//glUniform1f(3, 1.0f/mHeightScale); GpuUtils.GpuErrorCheck();
		glUniform1f(4, mHexFlatness); GpuUtils.GpuErrorCheck();
		
		pHeightTexture[pSrcIndex].bind(0);
		pStrataTexture.bind(1);
		//mDetailTexture.bind(2);
		//mTestTexture.bind(2);
		pColorTexture.bind(2);
		
		glUniformMatrix4fv(0, false, pCamera.getProjectionMatrix().get(matrixBuffer)); GpuUtils.GpuErrorCheck();
		glUniformMatrix4fv(1, false, new Matrix4f(pCamera.getViewMatrix()).mul(translationMatrix).get(matrixBuffer)); GpuUtils.GpuErrorCheck();		
		mHexBufferSet.bind();
		glDrawArraysInstanced(GL_TRIANGLES, 0, mHexVertexCount, mHexInstanceCount);
		
		Program.unbind();
	}
	
	// Bonus hex terrain :)
	public void buildHexBuffers() {
		
		class VertexBuilder {
			public float[] mPositionData;
			public float[] mNormalData;
			public float[] mTexCoordData;
			public float[] mInstanceData;
			public float[] mTexData;
			public int mIndex;
			public int mInstanceIndex;
			public int mTexIndex;
			
			VertexBuilder(int pNumVertices, int pNumInstances) {
				mPositionData = new float[pNumVertices*3];
				mNormalData   = new float[pNumVertices*3];
				mTexCoordData = new float[pNumVertices*2];
				mInstanceData = new float[pNumInstances*3];
				mTexData      = new float[pNumInstances*2];
				mIndex = 0;
				mInstanceIndex = 0;
				mTexIndex = 0;
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
			
			public void addInstance(float pPosX, float pPosY, float pPosZ, float pTexU, float pTexV) {
				mInstanceData[mInstanceIndex+0] = pPosX;
				mInstanceData[mInstanceIndex+1] = pPosY;
				mInstanceData[mInstanceIndex+2] = pPosZ;
				mInstanceIndex += 3;
				mTexData[mTexIndex+0] = pTexU;
				mTexData[mTexIndex+1] = pTexV;
				mTexIndex += 2;
			}
			
			public int getNumVertices() {
				return mIndex;
			}
			
			public int getNumInstances() {
				return mInstanceIndex/3;
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
			
			VBO buildTexVBO() {
				return VBO.createVertexBuffer(mTexData);
			}
			
			VBO buildTexCoordVBO() {
				return VBO.createVertexBuffer(mTexCoordData);
			}
		}
		
		VertexBuilder builder = new VertexBuilder(3*3*6, (Main.mSimulation.WORLD_SIZE_X/2)*(Main.mSimulation.WORLD_SIZE_Y/2)+1);//TODO +1
		
		float tc[][] = {
				{0,0},{ 0, 1},{ 1, 1}, 
				{0,0},{-1, 1},{ 0, 1},
				{0,0},{-1,-1},{-1, 1},
				{0,0},{ 0,-1},{-1,-1},
				{0,0},{ 1,-1},{ 0,-1},
				{0,0},{ 1, 1},{ 1,-1}
		};
		
		float us = 1.0f/(Main.mSimulation.WORLD_SIZE_X);
		float vs = 1.0f/(Main.mSimulation.WORLD_SIZE_Y);
		
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
		
		float du = 2.0f/Main.mSimulation.WORLD_SIZE_X;
		float dv = 2.0f/Main.mSimulation.WORLD_SIZE_Y;
		
		float x0 = -Main.mSimulation.WORLD_SIZE_X/2.0f;
		float z0 = -Main.mSimulation.WORLD_SIZE_Y/2.0f;
		
		float xScale = (float)(Math.sqrt(3)*0.5);
		float zScale = 1.5f;
		for (int x = 0; x < Main.mSimulation.WORLD_SIZE_X/2; ++x) {
			for (int z = 0; z < Main.mSimulation.WORLD_SIZE_Y/2; ++z) {
				float xPosOffset = (Math.abs(z)%2 == 1) ? xScale : 0.0f;
				float xTexOffset = (Math.abs(z)%2 == 1) ? du*0.5f : 0.0f;
//				builder.addInstance(
//						x0 + x*2*xScale + xPosOffset,
//						0,
//						z0 + z*zScale,
//						0.5f*du + du*x + xTexOffset,
//						0.5f*dv + dv*z);
				builder.addInstance(
						x0 + x*2*xScale + xPosOffset,
						0,
						z0 + z*zScale,
						0.5f*du + du*x + xTexOffset,
						0.5f*dv + dv*z);
			}
		}
		
		mHexVertexVbo = builder.buildPositionVBO();
		mHexNormalVbo = builder.buildNormalVBO();
		mHexTexCoordVbo = builder.buildTexCoordVBO();
		mHexInstanceVbo = builder.buildInstanceVBO();
		mHexTexVbo = builder.buildTexVBO();
		mHexBufferSet = new VAO();
		mHexBufferSet.setVbo(0, mHexVertexVbo,   3, 0); // TODO: 3 components, get from builder.
		mHexBufferSet.setVbo(1, mHexNormalVbo,   3, 0); // TODO: 3 components, get from builder.
		mHexBufferSet.setVbo(2, mHexTexCoordVbo, 2, 0); // TODO: 2 components, get from builder.
		mHexBufferSet.setVbo(3, mHexInstanceVbo, 3, 1); // TODO: 3 components, get from builder.
		mHexBufferSet.setVbo(4, mHexTexVbo,      2, 1); // TODO: 2 components, get from builder.
		
		mHexVertexCount   = builder.getNumVertices();
		mHexInstanceCount = builder.getNumInstances();		
	}
}