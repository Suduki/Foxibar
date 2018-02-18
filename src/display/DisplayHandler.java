package display;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.Constants;
import gpu.GpuUtils;
import gui.SplitRegion;
import gui.TextureRegion;
import gui.ArrayRegion;
import gui.Button;
import gui.DummyRegion;
import gui.Font;
import gui.GuiRoot;
import gui.HorizontalSplitRegion;
import gui.VerticalSplitRegion;
import gui.SceneRegion;
import messages.Message;
import messages.MessageHandler;

public class DisplayHandler extends MessageHandler {
	public Thread renderThreadThread;
	private static RenderThread renderThread;
	private static simulation.Simulation mSimulation;
	
	public DisplayHandler(simulation.Simulation pSimulation) {
		mSimulation = pSimulation;
		renderThread = new RenderThread(this);
		renderThreadThread = new Thread(renderThread);
		renderThreadThread.start();
		

		this.message(new messages.DummyMessage());
	}

	protected void evaluateMessage(Message pMessage)
	{
		pMessage.evaluate(this);
	}
		
	private static class RenderThread implements Runnable {
		
		private DisplayHandler mDisplayHandler;
		private Window mWindow;
		private int numFrames = 0;
				
		public RenderThread(DisplayHandler pDisplayHandler) {
			this.mDisplayHandler = pDisplayHandler;
		}
		
		public void run() {
			System.out.println("Render thread started.");
			
			mWindow = new Window(1920, 1080, "Foxibar");
			initOpenGL();
			
			TerrainRenderer terrainRenderer = new TerrainRenderer(mWindow);
			LegacyRenderer legacyRenderer = new LegacyRenderer(mWindow, mDisplayHandler, mSimulation);			
			legacyRenderer.loadResources();
			
			GuiRoot guiRoot = new GuiRoot(mWindow);
			/*
			AbstractSplitRegion rightMenu = new HorizontalSplitRegion(
					new DummyRegion(),
					new HorizontalSplitRegion(
							new DummyRegion(),
							new VerticalSplitRegion(
									new DummyRegion(),
									new DummyRegion())));
									*/
			ArrayRegion ar = new ArrayRegion(16,16);
			
			SplitRegion mainView = new VerticalSplitRegion(
					ar, //new SceneRegion(legacyRenderer), 
					new SceneRegion(terrainRenderer));//rightMenu);
			
			ArrayRegion mainMenu = new ArrayRegion(4,1);
			SplitRegion rootRegion = new HorizontalSplitRegion(mainMenu,mainView);
			Texture tex = Texture.fromFile("pics/GuiDefault.png");
			mainMenu.setRegion(0, 0, new TextureRegion(tex, 0, 1, 1, 0));
			Button testButton = new Button("Test", () -> {System.out.println("I am printed when the button is clicked!"); });
			mainMenu.setRegion(1, 0, testButton);
			
			Font font = Font.defaultFont();
			
			int k = 0;
			for (int y = 0; y < 16; ++y) {
				for (int x = 0; x < 16; ++x) {
					Font.CharacterDefinition charDef = font.getCharacterDefinition(k);
					if (charDef != null) {
						ar.setRegion(x, y,
								new TextureRegion(font.getTexture(), charDef.u0, charDef.u1, charDef.v1, charDef.v0));
								//new TextureRegion(tex, 0,1,1,0));
					}
					else {
						ar.setRegion(x, y,
								new TextureRegion(font.getTexture(), 0,1,1,0));
					}
					++k;
				}
			}
			
			guiRoot.setRootRegion(rootRegion);			
			rootRegion.setDividerPosition(0.25);			
			
			long time0 = System.currentTimeMillis();
			
			mWindow.makeCurrent();
			while(handleEvents()) {
				//legacyRenderer.render(mWindow.getWidth(), mWindow.getHeight());
				guiRoot.render();
				mWindow.swapBuffers();
				
				++numFrames;
				
				if (System.currentTimeMillis() - time0 > 1000)
				{
				//	System.out.println("FPS: " + numFrames);
					numFrames = 0;
					time0 = System.currentTimeMillis();
				}
			}

			System.out.println("Render thread finished.");
		}

		private boolean handleEvents() {
			return mWindow.handleEvents() && mDisplayHandler.handleMessages();
		}

		private void initOpenGL() {
			// TODO: Make this the responsibility of Window.
			GL.createCapabilities();
			System.out.println("OpenGL version: " + GL11.glGetString(GL_VERSION));
		}
	}
}
