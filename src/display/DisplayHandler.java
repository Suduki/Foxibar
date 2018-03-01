package display;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.Constants;
import gpu.GpuUtils;
import gui.SplitRegion;
import gui.Text;
import gui.TextureRegion;
import gui.GridRegion;
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
		private boolean mSimulate = false;
				
		public RenderThread(DisplayHandler pDisplayHandler) {
			this.mDisplayHandler = pDisplayHandler;
		}
		
		Region createLegacyGui(LegacyRenderer legacyRenderer)
		{
			Region scene = new SceneRegion(legacyRenderer);

			ArrayRegion menu = new ArrayRegion(ArrayRegion.Vertical);
			menu.insertRegion( 0, new Button("Import Brains", ()->legacyRenderer.actionLoadBrains()));
			menu.insertRegion( 1, new Button("Export Brains", ()->legacyRenderer.actionSaveBrains()));
			menu.insertRegion( 2, new Button("Regenerate",    ()->legacyRenderer.actionRegenerateWorld()));
			menu.insertRegion( 3, new Button("Cycle Modes",   ()->legacyRenderer.actionToggleRenderAnimals()));
			menu.insertRegion( 4, new Button("Kill Animals",  ()->legacyRenderer.actionKillAllAnimals()));
			
			VerticalSplitRegion view = new VerticalSplitRegion(menu, scene);
			view.setDividerPosition(1.0f/6.0f);
			return view;
		}
		
		Region createModernGui(TerrainRenderer terrainRenderer) {
			Region scene = new SceneRegion(terrainRenderer);
		
			ArrayRegion menu = new ArrayRegion(ArrayRegion.Vertical);
			menu.insertRegion( 0, new Button("Simulation On/Off", ()->mSimulate = !mSimulate));
			menu.insertRegion( 1, new Button(" 1 iter/frame", ()->terrainRenderer.setIterationsPerFrame(1)));
			menu.insertRegion( 2, new Button(" 5 iter/frame", ()->terrainRenderer.setIterationsPerFrame(5)));
			menu.insertRegion( 3, new Button("10 iter/frame", ()->terrainRenderer.setIterationsPerFrame(10)));
			menu.insertRegion( 4, new Button("15 iter/frame", ()->terrainRenderer.setIterationsPerFrame(15)));
			menu.insertRegion( 5, new Button("20 iter/frame", ()->terrainRenderer.setIterationsPerFrame(20)));
			menu.insertRegion( 6, new Button("25 iter/frame", ()->terrainRenderer.setIterationsPerFrame(25)));
			menu.insertRegion( 7, new Button("30 iter/frame", ()->terrainRenderer.setIterationsPerFrame(30)));
			menu.insertRegion( 8, new Button("35 iter/frame", ()->terrainRenderer.setIterationsPerFrame(35)));
			menu.insertRegion( 9, new Button("40 iter/frame", ()->terrainRenderer.setIterationsPerFrame(40)));
			menu.insertRegion(10, new Button("45 iter/frame", ()->terrainRenderer.setIterationsPerFrame(45)));
			menu.insertRegion(11, new Button("50 iter/frame", ()->terrainRenderer.setIterationsPerFrame(50)));
			
			VerticalSplitRegion view = new VerticalSplitRegion(menu, scene);
			view.setDividerPosition(1.0f/6.0f);
			return view;
		}
		
		public void run() {
			System.out.println("Render thread started.");
			
			mWindow = new Window(1280, 720, "Foxibar");
			initOpenGL();
			
			Font.defaultFont();
						
			
			ArrayRegion mainMenu = new ArrayRegion(ArrayRegion.Horizontal);//new GridRegion(1,1);
			Button toggleButton  = new Button("Toggle View ( 2D <-> 3D )");
			mainMenu.insertRegion(0, toggleButton);
			
			TerrainRenderer terrainRenderer = new TerrainRenderer(mWindow);
			Region modernView = createModernGui(terrainRenderer);
			Region legacyView = createLegacyGui(new LegacyRenderer(mDisplayHandler, mSimulation));
			
			HorizontalSplitRegion rootRegion = new HorizontalSplitRegion(mainMenu,legacyView);
			
			toggleButton.setCallback(() -> {
				if (rootRegion.getBottomSubRegion() != modernView) {
					rootRegion.setBottomSubRegion(modernView);
				}
				else {
					rootRegion.setBottomSubRegion(legacyView);
				}
				
				rootRegion.updateGeometry();
			});
			
			GuiRoot guiRoot = new GuiRoot(mWindow);
			guiRoot.setRootRegion(rootRegion);			
			rootRegion.setDividerPosition(0.08);
			
			
			
			long time0 = System.currentTimeMillis();
			
			mWindow.makeCurrent();
			while(handleEvents()) {
				if (mSimulate) {
					terrainRenderer.simulate();
				}
				
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
