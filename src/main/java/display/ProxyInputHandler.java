package display;

import java.util.ArrayList;
import java.util.List;

public class ProxyInputHandler implements InputHandlerI {

	List<InputHandlerI> mInputHandlers;
	
	public ProxyInputHandler() {
		mInputHandlers = new ArrayList<InputHandlerI>();
	}
	
	public void add(InputHandlerI inputHandler) {
		mInputHandlers.add(inputHandler);
	}
	
	@Override
	public void handleKeyboardEvents(int action, int key) {
		for (InputHandlerI handler : mInputHandlers) {
			handler.handleKeyboardEvents(action, key);
		}
	}

	@Override
	public void handleMouseEvents(long window, int button, int action, int mods) {
		for (InputHandlerI handler : mInputHandlers) {
			handler.handleMouseEvents(window, button, action, mods);
		}		
	}

	@Override
	public void handleMouseMotion(long window, double xpos, double ypos) {
		for (InputHandlerI handler : mInputHandlers) {
			handler.handleMouseMotion(window, xpos, ypos);
		}
	}

	@Override
	public void handleScrollWheel(long window, double xoffset, double yoffset) {
		for (InputHandlerI handler : mInputHandlers) {
			handler.handleScrollWheel(window, xoffset, yoffset);
		}
	}

	@Override
	public void handleFramebufferSize(long window, int width, int height) {
		for (InputHandlerI handler : mInputHandlers) {
			handler.handleFramebufferSize(window, width, height);
		}
	}

}
