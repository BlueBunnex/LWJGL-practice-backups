import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import shaders.Shader;

import java.nio.*;

public class Game {

	// returns the window handle
	public static long createWindow() {
		
		// Setup an error callback (into System.err)
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		if (!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

		// Create the window
		long thisWindow = GLFW.glfwCreateWindow(600, 400, "Hello World!", MemoryUtil.NULL, MemoryUtil.NULL);
		
		if (thisWindow == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback (called every time a key is pressed, repeated or released)
		GLFW.glfwSetKeyCallback(thisWindow, (window, key, scancode, action, mods) -> {
			
			// if escape is released, set window to close
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true);
			
		});

		// Get the thread stack and push a new frame
		// in order to get w/h to center the window
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			// Get the window size
			IntBuffer pWidth  = stack.mallocInt(1),
					  pHeight = stack.mallocInt(1);

			GLFW.glfwGetWindowSize(thisWindow, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

			// Center the window
			GLFW.glfwSetWindowPos(
				thisWindow,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
			
		}

		GLFW.glfwMakeContextCurrent(thisWindow);// Make the OpenGL context current
		GLFW.glfwSwapInterval(1); 				// Enable v-sync

		GLFW.glfwShowWindow(thisWindow);
		
		return thisWindow;
	}

	public static void loop(long window) {
		// allow all GL## to start working
		GL.createCapabilities();
		System.out.println(GL20.glGetString(GL20.GL_VERSION));

		// Set the clear color
		GL20.glClearColor(0.5f, 0.8f, 1.0f, 0.0f);
		
		new Shader("src/shaders/vert.txt", "src/shaders/frag.txt").start();
		
		float t = 0;

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!GLFW.glfwWindowShouldClose(window)) {
			// clear the framebuffer
			GL20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			
			// draw
			GL20.glPushMatrix();
			
			// ensure proper scaling regardless of window res
			try (MemoryStack stack = MemoryStack.stackPush()) {
			
				IntBuffer pWidth  = stack.mallocInt(1),
						  pHeight = stack.mallocInt(1);
	
				GLFW.glfwGetWindowSize(window, pWidth, pHeight);
				
				//GL11.glTranslatef(x, y, 0);
				GL20.glScalef(pHeight.get(0) / (float) pWidth.get(0), 1, 1f);
			}
			
			float z1 = 0.8f + (float) Math.sin(t) * 0.08f;
			float z2 = 0.8f - (float) Math.sin(t) * 0.08f;
			float x1 = (float) Math.cos(t) / 2;
			float x2 = -(float) Math.cos(t) / 2;
			
			t += 0.01;

			GL20.glBegin(GL20.GL_TRIANGLE_STRIP);
			
			GL20.glColor3f(0, 0, 1f);
			
			GL20.glVertex3f(x1, -0.5f, z1);
			GL20.glVertex3f(x1, 0.5f, z1);
			GL20.glVertex3f(x2, -0.5f, z2);
			
			GL20.glColor3f(1f, 1f, 0);
			
			GL20.glVertex3f(x2, 0.5f, z2);
			
			GL20.glEnd();

			GL20.glPopMatrix();

			// swap the color buffers
			GLFW.glfwSwapBuffers(window);

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			GLFW.glfwPollEvents();
		}
	}

}