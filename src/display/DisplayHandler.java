package display;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.Constants;
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

	public void exit() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	private static class RenderThread implements Runnable {
		
		private DisplayHandler mDisplayHandler;
		private Window mWindow;
		private Window mWindow2;
		private int numFrames = 0;
				
		public RenderThread(DisplayHandler pDisplayHandler) {
			this.mDisplayHandler = pDisplayHandler;
		}
		
		public void run() {
			System.out.println("Render thread started.");
			
		//	mWindow = new Window(LegacyRenderer.PIXELS_X + Constants.PIXELS_SIDEBOARD, LegacyRenderer.PIXELS_Y, "FOXIBAR - DEAD OR ALIVE");
			
			//LegacyRenderer legacyRenderer = new LegacyRenderer(mWindow, mDisplayHandler, mSimulation);
			
			
			//legacyRenderer.loadResources();

			mWindow2 = new Window(1920, 1080, "FOXIBAR - New renderer");
			initOpenGL();
			TerrainRenderer terrainRenderer = new TerrainRenderer(mWindow2);
			
			long time0 = System.currentTimeMillis();
			
			mWindow2.makeCurrent();
			while(mDisplayHandler.handleMessages() && handleEvents()) {
				/*
				mWindow.makeCurrent();
				legacyRenderer.render();
				mWindow.swapBuffers();
				*/
				
				terrainRenderer.render();
				mWindow2.swapBuffers();
				
				++numFrames;
				
				if (System.currentTimeMillis() - time0 > 1000)
				{
					System.out.println("FPS: " + numFrames);
					numFrames = 0;
					time0 = System.currentTimeMillis();
				}
			}

			mDisplayHandler.exit();

			System.out.println("Render thread finished.");
		}

		private boolean handleEvents() {
			//boolean w1 = mWindow.handleEvents();
			boolean w2 = mWindow2.handleEvents();
			//return w1 && w2;
			return w2;
		}

		private void initOpenGL()
		{			
			GL.createCapabilities();

			System.out.println("OpenGL version: " + GL11.glGetString(GL_VERSION));
		}
	}
}
