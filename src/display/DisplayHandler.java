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
			
			/*
			GridRegion menu = new GridRegion(1,5);
			menu.setRegion(0, 0, new Button("Import Brains", ()->legacyRenderer.actionLoadBrains()));
			menu.setRegion(0, 1, new Button("Export Brains", ()->legacyRenderer.actionSaveBrains()));
			menu.setRegion(0, 2, new Button("Regenerate",    ()->legacyRenderer.actionRegenerateWorld()));
			menu.setRegion(0, 3, new Button("Cycle Modes",   ()->legacyRenderer.actionToggleRenderAnimals()));
			menu.setRegion(0, 4, new Button("Kill Animals",  ()->legacyRenderer.actionKillAllAnimals()));
			*/
			VerticalSplitRegion view = new VerticalSplitRegion(menu, scene);
			view.setDividerPosition(1.0f/6.0f);
			return view;
		}
		
		Region createModernGui(TerrainRenderer terrainRenderer) {
			Region scene = new SceneRegion(terrainRenderer);
		
			ArrayRegion menu = new ArrayRegion(ArrayRegion.Vertical);
			menu.insertRegion(0, new Button("Toggle sim", ()->terrainRenderer.toggleSimulateOnRender()));
			
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
				terrainRenderer.simulate();
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
