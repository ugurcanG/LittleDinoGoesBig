package ugmaybachengine;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
	
	private String vertexShaderSrc = "#version 410 core\n" +
			"layout (location = 0) in vec3 aPos;\n" +
			"layout (location = 1) in vec4 aColor;\n" +
			"\n" +
			"out vec4 fColor;\n" +
			"\n" +
			"void main(){\n" +
			"    fColor = aColor;\n" +
			"    gl_Position = vec4(aPos, 1.0);\n" +
			"}";
	
	private String fragmentShaderSrc = "#version 410 core\n" +
			"\n" +
			"in vec4 fColor;\n" +
			"\n" +
			"out vec4 color;\n" +
			"\n" +
			"void main(){\n" +
			"    color = fColor;\n" +
			"}";
	
	private int vertexID, fragmentID, shaderProgramm;
	
	private float[] vertexArray = {
			//position first 3 // color last 4 floats
			0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,  // Bottom right
			-0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,  // Top left
			0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,   // Top right
			-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, // Bottom left
	};
	
	// IMPORTANT: Must be in counter-clockwise order
	private int[] elementArray = {
			/*
					4x       3x
			
			
					1x       2x
			 */
			2, 1, 0, // Top right triangle
			0, 1, 3  // Bottom left triangle
	};
	
	private int vaoID, vboID, eboID;
	
	public LevelEditorScene() {
	}
	
	@Override
	public void init() {
		// Compile and link shaders
		
		
		// First load and compile the vertex shader
		vertexID = glCreateShader( GL_VERTEX_SHADER );
		// Pass the shader source to the GPU
		glShaderSource( vertexID, vertexShaderSrc );
		glCompileShader( vertexID );
		
		// Check for errors in the compilation
		int success = glGetShaderi( vertexID, GL_COMPILE_STATUS );
		if( success == GL_FALSE ) {
			int len = glGetShaderi( vertexID, GL_INFO_LOG_LENGTH );
			System.out.println( "ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed." );
			System.out.println( glGetShaderInfoLog( vertexID, len ) );
			assert false : "";
		}
		
		// First load and compile the vertex shader
		fragmentID = glCreateShader( GL_FRAGMENT_SHADER );
		// Pass the shader source to the GPU
		glShaderSource( fragmentID, fragmentShaderSrc );
		glCompileShader( fragmentID );
		
		// Check for errors in the compilation
		success = glGetShaderi( fragmentID, GL_COMPILE_STATUS );
		if( success == GL_FALSE ) {
			int len = glGetShaderi( fragmentID, GL_INFO_LOG_LENGTH );
			System.out.println( "ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed." );
			System.out.println( glGetShaderInfoLog( fragmentID, len ) );
			assert false : "";
		}
		
		// Link shaders and check for errors
		shaderProgramm = glCreateProgram();
		glAttachShader( shaderProgramm, vertexID );
		glAttachShader( shaderProgramm, fragmentID );
		glLinkProgram( shaderProgramm );
		
		// Check for linking errors
		success = glGetProgrami( shaderProgramm, GL_LINK_STATUS );
		if( success == GL_FALSE ) {
			int len = glGetProgrami( shaderProgramm, GL_INFO_LOG_LENGTH );
			System.out.println( "ERROR: 'defaultShader.glsl'\n\tLinking of shaders failed." );
			System.out.println( glGetProgramInfoLog( shaderProgramm, len ) );
			assert false : "";
		}
		
		//  ============================================================
		//  Generate VAO, VBO, and EBO buffer objects, and send to GPU
		//  ============================================================
		vaoID = glGenVertexArrays();
		glBindVertexArray( vaoID );
		
		// Create a float buffer of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer( vertexArray.length );
		vertexBuffer.put( vertexArray ).flip();
		
		// Create VBO upload the vertex buffer
		vboID = glGenBuffers();
		glBindBuffer( GL_ARRAY_BUFFER, vboID );
		glBufferData( GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW );
		
		// Create the indices and upload
		IntBuffer elementBuffer = BufferUtils.createIntBuffer( elementArray.length );
		elementBuffer.put( elementArray ).flip();
		
		eboID = glGenBuffers();
		glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, eboID );
		glBufferData( GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW );
		
		// Add the vertex attribute pointers
		int positionsSize = 3;
		int colorSize = 4;
		int floatSizeBytes = 4;
		int vertexSizeBytes = ( positionsSize + colorSize ) * floatSizeBytes;
		glVertexAttribPointer( 0, positionsSize, GL_FLAT, false, vertexSizeBytes, 0 );
		glEnableVertexAttribArray( 0 );
		
		glVertexAttribPointer( 1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes );
		glEnableVertexAttribArray( 1 );
	}
	
	@Override
	public void update( float dt ) {
		// Bind shader program
		glUseProgram( shaderProgramm );
		// Bind the VAO that we´re using
		glBindVertexArray( vaoID );
		
		// Enable the vertex attribute pointers
		glEnableVertexAttribArray( 0 );
		glEnableVertexAttribArray( 1 );
		
		glDrawElements( GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0 );
		
		// Unbind everything
		glDisableVertexAttribArray( 0 );
		glDisableVertexAttribArray( 1 );
		
		glBindVertexArray( 0 );
		
		glUseProgram( 0 );
	}
}