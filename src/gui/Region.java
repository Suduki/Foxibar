package gui;

import display.Window;

public interface Region {
	public boolean render(GuiRenderer pGuiRenderer);
	
	public boolean handleMouseEvent(MouseEvent pEvent, MouseState pMouse);
	public boolean handleKeyboardEvent(KeyboardState pState);
	
	public void updateGeometry(int pPosX, int pPosY, int pWidth, int pHeight);
	public Point getPos();
	public Point getSize();
	
	public boolean isPointInside(Point pPoint);
	public boolean didMouseEnter(MouseState pState);
	public boolean didMouseLeave(MouseState pState);
	
	public boolean hasKeyboardFocus();
	public boolean keyboardFocusGranted();
	public void    keyboardFocusRevoked();
	
	public Point minSize();
	
	public void setParent(Region pParent);
	public Region getParent();
	public Window getWindow();
}
