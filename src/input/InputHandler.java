package input;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

//import buttons.Button;

public class InputHandler {

	private long mGlfwWindowHandle;
	
	public InputHandler(long glfwWindowHandle)
	{
		mGlfwWindowHandle = glfwWindowHandle;
	}
	
	public boolean handleEvents()
	{
		//	Button.updateAllButtons();

		if (glfwWindowShouldClose(mGlfwWindowHandle))
		{
			return false;
		}

		glfwPollEvents();

		return true;
	}
	
}
