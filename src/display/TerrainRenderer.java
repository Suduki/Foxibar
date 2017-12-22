package display;

import static org.lwjgl.opengl.GL11.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import constants.Constants;
import world.World;

public class TerrainRenderer {
	
	private Texture mGrassTexture = null;
	private ByteBuffer mGrassPixelBuffer = null;
	private Vector3f[] mVertices = null;
	private int[] mIndices = null;
	private Vector2f[] mTexCoords = null;
	
	public TerrainRenderer() {
		mGrassPixelBuffer = BufferUtils.createByteBuffer(Constants.WORLD_SIZE*4);

		updateGrassTexture();
		buildArrays();
		
	}
	
	void buildArrays() {
		mVertices  = new Vector3f[Constants.WORLD_SIZE];
		mTexCoords = new Vector2f[Constants.WORLD_SIZE];
		mIndices   = new int[Constants.WORLD_SIZE*6];
		
		int index = 0;
		float xScale = 1.0f/Constants.WORLD_SIZE_X;
		float zScale = 1.0f/Constants.WORLD_SIZE_Y;
		float yScale = 0.2f;
		
		for (int x = 0; x < Constants.WORLD_SIZE_X; ++x) {
			for (int z = 0; z < Constants.WORLD_SIZE_Y; ++z) {
				mVertices[index] = new Vector3f(x*xScale - 0.5f, World.terrain.height[index]*yScale, z*zScale - 0.5f);
				mTexCoords[index] = new Vector2f(z*zScale, x*xScale);
				++index;
			}
		}
		
		int i = 0;
		for (int x = 0; x < Constants.WORLD_SIZE_X-1; ++x) {
			for (int z = 0; z < Constants.WORLD_SIZE_Y-1; ++z) {
				mIndices[i+0] = Constants.WORLD_SIZE_X*z + x;
				mIndices[i+1] = Constants.WORLD_SIZE_X*z + x+1;
				mIndices[i+2] = Constants.WORLD_SIZE_X*(z+1) + x;				
				mIndices[i+3] = Constants.WORLD_SIZE_X*z + x+1;
				mIndices[i+4] = Constants.WORLD_SIZE_X*(z+1) + x+1;
				mIndices[i+5] = Constants.WORLD_SIZE_X*(z+1) + x;
				i+=6;
			}
		}
	}
	
	void drawArrays() {
		FloatBuffer vertex   = BufferUtils.createFloatBuffer(3);
		FloatBuffer texCoord = BufferUtils.createFloatBuffer(2);
		
		glColor3f(1,1,1);		
		glBegin(GL_TRIANGLES);
		for (int i = 0; i < mIndices.length; ++i) {
			int j = mIndices[i];
			mVertices[j].get(vertex);
			mTexCoords[j].get(texCoord);
			glTexCoord2fv(texCoord);
			glVertex3fv(vertex);
		}
		glEnd();
	}
	
	private void updateGrassTexture() {
		float grassness, dirtness;
		float[] tempColor = new float[3];
		for (int i = 0; i < Constants.WORLD_SIZE; ++i) {
			float r = 0;
			float g = 0;
			float b = 0;

			grassness = World.grass.height[i];
			dirtness = 1 - grassness;
			
			r += grassness*World.grass.color[0];
			g += grassness*World.grass.color[1];
			b += grassness*World.grass.color[2];

			World.terrain.getColor(i, tempColor);
			r += dirtness*tempColor[0];
			g += dirtness*tempColor[1];
			b += dirtness*tempColor[2];

			if (RenderState.RENDER_BLOOD) {
				World.blood.getColor(i, tempColor);
				r += tempColor[0];
				g += tempColor[1];
				b += tempColor[2];
			}
			
			r*=255;
			g*=255;
			b*=255;
			mGrassPixelBuffer.put(i*4+0, (byte)r);
			mGrassPixelBuffer.put(i*4+1, (byte)g);
			mGrassPixelBuffer.put(i*4+2, (byte)b);
		}
		
		if (mGrassTexture == null) {
			mGrassTexture = new Texture(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, mGrassPixelBuffer);
		}
		else {
			mGrassTexture.load(Constants.WORLD_SIZE_X, Constants.WORLD_SIZE_Y, mGrassPixelBuffer);
		}
	}
	
	public void render(Camera camera, Window window) {
		
		updateGrassTexture();
		
		FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
		glViewport(0, 0, window.getWidth(), window.getHeight());
		
		glMatrixMode(GL_PROJECTION);
		camera.getProjectionMatrix().get(matrixBuffer);
		glLoadMatrixf(matrixBuffer);
		
		glMatrixMode(GL_MODELVIEW);
		camera.getViewMatrix().get(matrixBuffer);
		glLoadMatrixf(matrixBuffer);
		
		glClearColor(0.25f,0.5f,1.0f,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glPolygonMode(GL_FRONT, GL_FILL);
		glPolygonMode(GL_BACK, GL_LINE);

		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		mGrassTexture.bind();

		float s = (1.0f + Constants.WORLD_SIZE_X)/(float)Constants.WORLD_SIZE_X;
		
		glPushMatrix();
		glScalef(s,1,s);
		drawArrays();
		glPopMatrix();
		
		glPushMatrix();	glScalef(s,1,s); glTranslatef( 1, 0, 0); drawArrays(); glPopMatrix();
		glPushMatrix();	glScalef(s,1,s); glTranslatef(-1, 0, 0); drawArrays(); glPopMatrix();
		//glPushMatrix();	glTranslatef( 0, 0, 1); glScalef(s,1,s); drawArrays(); glPopMatrix();
		//glPushMatrix();	glTranslatef( 0, 0,-1); glScalef(s,1,s); drawArrays(); glPopMatrix();
		
		glDisable(GL_TEXTURE_2D);
	}
}
