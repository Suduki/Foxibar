package display;

import agents.Brainler;
import main.Main;
import gui.KeyboardState;
import gui.MouseEvent;
import gui.MouseState;
import gui.RegionI;
import simulation.Simulation;
import talents.TalentSpider;

public class DesignAnimalRenderer implements gui.SceneRegionRenderer {
	
	private RegionI mRegion;
	TalentSpider skillsRenderCircle;
	
	private int mViewportWidth, mViewportHeight;
	
	Brainler brainlerCreatedByUser;
	
	public DesignAnimalRenderer(DisplayHandler mDisplayHandler, Simulation mSimulation) {
		brainlerCreatedByUser = new Brainler(null);
		brainlerCreatedByUser.inherit(null);
		
		skillsRenderCircle = new TalentSpider(200, 100, brainlerCreatedByUser);
	}

	@Override
	public void render(int pViewportWidth, int pViewportHeight) {
		mViewportWidth = pViewportWidth;
		mViewportHeight = pViewportHeight;
		skillsRenderCircle.update(pViewportWidth / 2, pViewportHeight / 2);
		skillsRenderCircle.render();
	}
	
	@Override
	public void setRegion(RegionI region) {
		mRegion = region;
	}

	@Override
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse) {
		return true;
	}

	@Override
	public boolean handleKeyboardEvent(KeyboardState pEvent) {
		return false;
	}

	public void actionIncrease(int i) {
		brainlerCreatedByUser.talents.talentsRelative[i] += 0.2f;
		brainlerCreatedByUser.talents.normalizeAndCalculateActuals();
		brainlerCreatedByUser.updateColors();
	}
	
	public void actionSave() {
		Main.animalTypeToSpawn = Main.BRAINLER;
		Brainler.brainlerCreatedByUser = brainlerCreatedByUser;
		brainlerCreatedByUser.inheritAppearanceFactors(null);
		brainlerCreatedByUser.appearanceMutation = 0;
		brainlerCreatedByUser.talents.mutation = 0;
	}
	
	public void actionReset() {
		Brainler.brainlerCreatedByUser = null;
	}
}
