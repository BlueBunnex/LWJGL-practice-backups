package shaders;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

// we're using shadercode version 1.2, specified here
// https://registry.khronos.org/OpenGL/specs/gl/GLSLangSpec.1.20.pdf

public class Shader {
	
	private final int programID;
	
	public Shader(String vertexFile, String fragmentFile) {
		programID = GL20.glCreateProgram();
		
		// load in the shadercode
		int vertexShaderID   = loadShader(vertexFile,   GL20.GL_VERTEX_SHADER);
		int fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		// bind attributes
		//GL20.glBindAttribLocation(programID, 0, "position");

		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
	}
	
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append(System.getProperty("line.separator"));
			}
			
			reader.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		int shaderID = GL20.glCreateShader(type);
		
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		
		if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		
		return shaderID;
	}
	
	public int loadTexture(String path) {
		int texture = GL20.glGenTextures();
		GL20.glBindTexture(GL20.GL_TEXTURE_2D, texture);
		
		GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
		GL20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
		
		try {
			BufferedImage image = ImageIO.read(new File(path));
			
			ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 3);
			
	        for (int x=0; x<image.getWidth(); x++) {
	        	for (int y=0; y<image.getHeight(); y++) {
	        		
	        		int rgb = image.getRGB(x, y);
	        		
		            buffer.put((byte) (rgb       & 255));
		            buffer.put((byte) (rgb >>  8 & 255));
		            buffer.put((byte) (rgb >> 16 & 255));
	        	}
	        }
	        
	        buffer.flip();
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 16, 16, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
		
		return texture;
	}

}
