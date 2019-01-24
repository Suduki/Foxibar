package display;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import constants.Constants;
import gui.*;
import messages.Message;
import messages.MessageHandler;
import talents.Talents;

public class DisplayHandler extends MessageHandler {
	public Thread renderThreadThread;
	private static RenderThread renderThread;
	private static simulation.Simulation mSimulation;
	
	public static float PIXELS_PER_NODE_X;
	public static float PIXELS_PER_NODE_Y;
	
	public DisplayHandler(simulation.Simulation pSimulation) {
		mSimulation = pSimulation;
		PIXELS_PER_NODE_X = ((float)constants.Constants.PIXELS_X)/mSimulation.WORLD_SIZE_X;
		PIXELS_PER_NODE_Y = ((float)constants.Constants.PIXELS_Y)/mSimulation.WORLD_SIZE_Y;
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
		private boolean mSimulate = false;
				
		public RenderThread(DisplayHandler pDisplayHandler) {
			this.mDisplayHandler = pDisplayHandler;
		}
		
		RegionI createDesignAnimalGui(DesignAnimalRenderer renderer) {
			RegionI scene = new SceneRegion(renderer);
			
			ArrayRegion menu = new ArrayRegion(ArrayRegion.Vertical);
			int buttonId = 0;
			int talentId = 0;
			menu.insertRegion( buttonId++, new Button(Talents.names[talentId],			()->renderer.actionIncrease(0)));
			menu.insertRegion( buttonId++, new Button("Speed",			()->renderer.actionIncrease(1)));
			menu.insertRegion( buttonId++, new Button("Toughness",		()->renderer.actionIncrease(2)));
			menu.insertRegion( buttonId++, new Button("Digest Blood",	()->renderer.actionIncrease(3)));
			menu.insertRegion( buttonId++, new Button("Digest Grass",	()->renderer.actionIncrease(4)));
			menu.insertRegion( buttonId++, new Button("Fertility",		()->renderer.actionIncrease(5)));
			menu.insertRegion( buttonId++, new Button("Save",		()->renderer.actionSave()));
			menu.insertRegion( buttonId++, new Button("Reset",		()->renderer.actionReset()));
			
			VerticalSplitRegion view = new VerticalSplitRegion(menu, scene);
			return view;
		}
		
		RegionI createLegacyGui(LegacyRenderer legacyRenderer)
		{
			RegionI scene = new SceneRegion(legacyRenderer);

			ArrayRegion menu = new ArrayRegion(ArrayRegion.Vertical);
			int buttonId = 0;
			menu.insertRegion( buttonId++, new Button("Import Brains",	()->legacyRenderer.actionLoadBrains()));
			menu.insertRegion( buttonId++, new Button("Export Brains",	()->legacyRenderer.actionSaveBrains()));
			menu.insertRegion( buttonId++, new Button("Regenerate",		()->legacyRenderer.actionRegenerateWorld()));
			menu.insertRegion( buttonId++, new Button("Cycle Modes",		()->legacyRenderer.actionToggleRenderAnimals()));
			menu.insertRegion( buttonId++, new Button("Kill Animals",	()->legacyRenderer.actionKillAllAnimals()));
			menu.insertRegion( buttonId++, new Button("Continuous Spawn",	()->legacyRenderer.actionSpawnAnimals()));
			menu.insertRegion( buttonId++, new Button("Spawn Randomlings",	()->legacyRenderer.actionSpawnWithMouse(main.Main.RANDOMLING)));
			menu.insertRegion( buttonId++, new Button("Spawn Grassler",	()->legacyRenderer.actionSpawnWithMouse(main.Main.GRASSLER)));
			menu.insertRegion( buttonId++, new Button("Spawn Bloodling",	()->legacyRenderer.actionSpawnWithMouse(main.Main.BLOODLING)));
			menu.insertRegion( buttonId++, new Button("Spawn Brainler",	()->legacyRenderer.actionSpawnWithMouse(main.Main.BRAINLER)));
			
			VerticalSplitRegion view = new VerticalSplitRegion(menu, scene);
			return view;
		}
		
		RegionI createModernGui(TerrainRenderer terrainRenderer) {
			RegionI scene = new SceneRegion(terrainRenderer);
		
			ArrayRegion menu = new ArrayRegion(ArrayRegion.Vertical);
			int buttonId = 0;
			menu.insertRegion(buttonId++, new Button("Simulation On/Off", ()->mSimulate = !mSimulate));
			menu.insertRegion(buttonId++, new Button(" 1 iter/frame", ()->terrainRenderer.setIterationsPerFrame(1)));
			menu.insertRegion(buttonId++, new Button(" 5 iter/frame", ()->terrainRenderer.setIterationsPerFrame(5)));
			menu.insertRegion(buttonId++, new Button("30 iter/frame", ()->terrainRenderer.setIterationsPerFrame(30)));
			menu.insertRegion(buttonId++, new Button("50 iter/frame", ()->terrainRenderer.setIterationsPerFrame(50)));
			
			menu.insertRegion(buttonId++, new Button("Flatness: 0.00", ()->terrainRenderer.mHexTerrainRenderer.setFlatness(0.0f)));
			menu.insertRegion(buttonId++, new Button("Flatness: 0.50", ()->terrainRenderer.mHexTerrainRenderer.setFlatness(0.5f)));
			menu.insertRegion(buttonId++, new Button("Flatness: 1.00", ()->terrainRenderer.mHexTerrainRenderer.setFlatness(1.0f)));
			
			menu.insertRegion(buttonId++, new Button("Render Grass", ()->terrainRenderer.setDrawGrass()));
			menu.insertRegion(buttonId++, new Button("Reset Grass", ()->terrainRenderer.resetGrass()));
			
			VerticalSplitRegion view = new VerticalSplitRegion(menu, scene);
			return view;
		}
		
		public void run() {
			System.out.println("Render thread started.");
			
			mWindow = new Window(Constants.PIXELS_X, Constants.PIXELS_Y, "Foxibar");
			initOpenGL();
			
			Font.defaultFont();

			ArrayRegion mainMenu = new ArrayRegion(ArrayRegion.Horizontal);
			Button legacyButton  = new Button("2D");
			Button modernButton  = new Button("3D");
			Button designAnimalButton  = new Button("DesignAnimal");
			
			int buttonId = 0;
			mainMenu.insertRegion(buttonId++, legacyButton);
			mainMenu.insertRegion(buttonId++, modernButton);
			mainMenu.insertRegion(buttonId++, designAnimalButton);
			
			TerrainRenderer terrainRenderer = new TerrainRenderer(mWindow);
			
			RegionI modernView = createModernGui(terrainRenderer);
			RegionI legacyView = createLegacyGui(new LegacyRenderer(mDisplayHandler, mSimulation));
			RegionI designAnimalView = createDesignAnimalGui(new DesignAnimalRenderer(mDisplayHandler, mSimulation));
			
			HorizontalSplitRegion rootRegion = new HorizontalSplitRegion(mainMenu, legacyView);
			
			legacyButton.setCallback(() -> {
				rootRegion.setBottomSubRegion(legacyView);
			});
			modernButton.setCallback(() -> {
				rootRegion.setBottomSubRegion(modernView);
			});
			designAnimalButton.setCallback(() -> {
				rootRegion.setBottomSubRegion(designAnimalView);
				System.out.println("setCallback designAnimalView");
			});
			
			GuiRoot guiRoot = new GuiRoot(mWindow, mSimulation);
			guiRoot.setRootRegion(rootRegion);

			mWindow.makeCurrent();
			while(handleEvents()) {
				if (mSimulate) {
					terrainRenderer.simulate();
				}
				guiRoot.render();
				mWindow.swapBuffers();
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
