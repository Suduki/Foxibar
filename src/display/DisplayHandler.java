package display;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.Constants;
import gpu.GpuUtils;
import gui.SplitRegion;
import gui.DummyRegion;
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
			SplitRegion mainView = new VerticalSplitRegion(
					new SceneRegion(legacyRenderer), 
					new SceneRegion(terrainRenderer));//rightMenu);
			
			SplitRegion rootRegion = new HorizontalSplitRegion(
					new DummyRegion(), // Main menu
					mainView);
			
			guiRoot.setRootRegion(rootRegion);			
			rootRegion.setDividerPosition(0.05);
			//mainView.setDividerPosition(0.8f);
			
			
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
