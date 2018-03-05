package gui;

public interface SceneRegionRenderer {
	public void render(int pViewportWidth, int pViewportHeight);
	public void setRegion(Region region);
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pState);
	public boolean handleKeyboardEvent(KeyboardState pEvent);
}
