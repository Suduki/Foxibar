package gui;

public interface SceneRegionRenderer {
	public void render(int pViewportWidth, int pViewportHeight);
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pState);
	public boolean handleKeyboardEvent(KeyboardState pEvent);
}
