import org.lwjgl.*;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("Running LWJGL (" + Version.getVersion() + ")");

		// have to run on main thread, otherwise GLFW doesn't work
		long window = Game.createWindow();
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!GLFW.glfwWindowShouldClose(window))
			Game.update(window);

		System.out.println("Window closed.");
		
		// Free the window callbacks and destroy the window
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
		
		System.out.println("Process terminated.");
	}

}
