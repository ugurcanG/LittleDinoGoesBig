package ugmaybachengine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
	private static Window window = null;
	private static Scene currentScene;
	private final int width;
	private final int height;
	private final String title;
	private long glfwWindow;
	private float r, g, b, a;
	
	
	private Window() {
		this.width = 1920;
		this.height = 1080;
		this.title = "LittleDinoGoesBig";
		r = 1;
		g = 1;
		b = 1;
		a = 1;
	}
	
	public static void changeScene( int newScene ) {
		switch( newScene ) {
			case 0:
				currentScene = new LevelEditorScene();
				currentScene.init();
				break;
			case 1:
				currentScene = new LevelScene();
				currentScene.init();
				break;
			default:
				assert false : "Unkown scene '" + newScene + "'";
				break;
		}
	}
	
	public static Window get() {
		if( Window.window == null ) {
			Window.window = new Window();
		}
		return Window.window;
	}
	
	public float getR() {
		return r;
	}
	
	public void setR( float r ) {
		this.r = r;
	}
	
	public float getG() {
		return g;
	}
	
	public void setG( float g ) {
		this.g = g;
	}
	
	public float getB() {
		return b;
	}
	
	public void setB( float b ) {
		this.b = b;
	}
	
	public float getA() {
		return a;
	}
	
	public void setA( float a ) {
		this.a = a;
	}
	
	public void run() {
		System.out.println( "Hello LWJGL " + Version.getVersion() + "!" );
		
		init();
		loop();
		
		// Free the memory
		glfwFreeCallbacks( glfwWindow );
		glfwDestroyWindow( glfwWindow );
		
		// Terminate GLFW and the free the error callback
		glfwTerminate();
		glfwSetErrorCallback( null ).free();
	}
	
	public void init() {
		//Setup an error callback
		GLFWErrorCallback.createPrint( System.err ).set();
		
		// Initialize GLFW
		if( !glfwInit() ) {
			throw new IllegalStateException( "Unable to initialize GLFW." );
		}
		
		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint( GLFW_VISIBLE, GLFW_FALSE );
		glfwWindowHint( GLFW_RESIZABLE, GLFW_TRUE );
		glfwWindowHint( GLFW_MAXIMIZED, GLFW_FALSE );
		glfwWindowHint( GLFW_CONTEXT_VERSION_MAJOR, 3 );
		glfwWindowHint( GLFW_CONTEXT_VERSION_MINOR, 2 );
		glfwWindowHint( GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE );
		glfwWindowHint( GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE );
		
		// Create the window
		glfwWindow = glfwCreateWindow( this.width, this.height, this.title, NULL, NULL );
		if( glfwWindow == NULL ) {
			throw new IllegalStateException( "Failed to create the GLFW window." );
		}
		
		glfwSetCursorPosCallback( glfwWindow, MouseListener::mousePosCallback );
		glfwSetMouseButtonCallback( glfwWindow, MouseListener::mouseButtonCallback );
		glfwSetScrollCallback( glfwWindow, MouseListener::mouseScrollCallback );
		glfwSetKeyCallback( glfwWindow, KeyListener::keyCallback );
		
		// Make the OpenGl context current
		glfwMakeContextCurrent( glfwWindow );
		// Enable v-sync
		glfwSwapInterval( 1 );
		
		// Make the window visible
		glfwShowWindow( glfwWindow );
		
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		Window.changeScene( 0 );
	}
	
	public void loop() {
		float beginTime = Time.getTime();
		float endTime;
		float dt = -1.0f;
		
		while( !glfwWindowShouldClose( glfwWindow ) ) {
			// Poll events
			glfwPollEvents();
			
			glClearColor( r, g, b, a );
			glClear( GL_COLOR_BUFFER_BIT );
			
			if( dt >= 0 ) {
				currentScene.update( dt );
			}
			
			glfwSwapBuffers( glfwWindow );
			
			endTime = Time.getTime();
			dt = endTime - beginTime;
			beginTime = endTime;
		}
	}
}
