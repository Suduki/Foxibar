package display;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.Constants;
import gpu.GpuUtils;
import gui.SplitRegion;
import gui.Text;
import gui.TextureRegion;
import gui.ArrayRegion;
import gui.Button;
import gui.DummyRegion;
import gui.Font;
import gui.GuiRoot;
import gui.HorizontalSplitRegion;
import gui.Region;
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
			
			Font.defaultFont();
			
			Region terrainRenderer = new SceneRegion(new TerrainRenderer(mWindow));
			Region legacyRenderer = new SceneRegion(new LegacyRenderer(mWindow, mDisplayHandler, mSimulation));
			
			
			GuiRoot guiRoot = new GuiRoot(mWindow);
			
			ArrayRegion mainMenu = new ArrayRegion(6,1);
			Button toggleButton = new Button("Toggle");
			mainMenu.setRegion(1, 0, toggleButton);
			
			ArrayRegion legacyMenu = new ArrayRegion(1,5);
			VerticalSplitRegion legacyView = new VerticalSplitRegion(legacyMenu, legacyRenderer);
			legacyMenu.setRegion(0, 0, new Button("Import Brain"));
			legacyMenu.setRegion(0, 1, new Button("Export Brain"));
			legacyMenu.setRegion(0, 2, new Button("Regenerate World"));
			legacyMenu.setRegion(0, 3, new Button("Render Animals"));
			legacyMenu.setRegion(0, 4, new Button("Kill All"));
			
			HorizontalSplitRegion rootRegion = new HorizontalSplitRegion(mainMenu,legacyView);
			
			toggleButton.setCallback(() -> {
				if (rootRegion.getBottomSubRegion() != terrainRenderer) {
					rootRegion.setBottomSubRegion(terrainRenderer);
				}
				else {
					rootRegion.setBottomSubRegion(legacyView);
				}
				
				rootRegion.updateGeometry();
			});
			
			guiRoot.setRootRegion(rootRegion);			
			rootRegion.setDividerPosition(0.1);
			legacyView.setDividerPosition(0.3f);
			
			
			
			long time0 = System.currentTimeMillis();
			
			mWindow.makeCurrent();
			while(handleEvents()) {
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
