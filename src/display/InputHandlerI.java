package display;

public interface InputHandlerI {
	
	void handleKeyboardEvents(int action, int key);
	
	void handleMouseEvents(long window, int button, int action, int mods);
	
	void handleMouseMotion(long window, double xpos, double ypos);
	
	void handleScrollWheel(long window, double xoffset, double yoffset);
	
	void handleFramebufferSize(long window, int width, int height);
}
