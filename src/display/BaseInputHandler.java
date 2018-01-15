package display;

public class BaseInputHandler implements InputHandlerI {
	public void handleKeyboardEvents(int action, int key) {}
	public void handleMouseEvents(long window, int button, int action, int mods) {}
	public void handleMouseMotion(long window, double xpos, double ypos) {}
	public void handleScrollWheel(long window, double xoffset, double yoffset) {}

}
