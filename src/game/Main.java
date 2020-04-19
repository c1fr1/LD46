package game;

import engine.OpenAL.SoundSource;
import engine.OpenGL.EnigWindow;
import engine.OpenGL.VAO;
import game.views.MainView;
import org.joml.Matrix4f;

import java.io.IOException;

public class Main {
	public static VAO screenObj;
	public static SoundSource source;
	public static Matrix4f squareCam;
	
	public static void main(String[] args) {
		if (args.length == 0) {
			String os = System.getProperty("os.name");
			System.out.println("Operating System: " + os);
			if (os.contains("mac") || os.contains("Mac")) {
				System.out.println("in order to get a stack trace, run with\njava -jar 'trashseeking.jar' noReRun -XstartOnFirstThread");
				try {
					Runtime.getRuntime().exec(new String[]{"java", "-XstartOnFirstThread", "-jar", "'trashseeking.jar'", "noReRun"});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				runGame();
			}
		}else if (args[0].equals("noReRun")) {
			runGame();
		}
	}
	
	public static void runGame() {
		EnigWindow.runOpeningSequence = false;
		EnigWindow window = new EnigWindow("LD45", "res/textures/icon.png");
		window.fps = 60;
		
		squareCam = window.getSquarePerspectiveMatrix(100);
		
		loadResources();
		
		MainView.main = new MainView(window);

		MainView.main.runLoop();
		
		window.terminate();
	}
	
	public static void loadResources() {
		source = new SoundSource();
		Shaders.createMainShaders();
		screenObj = new VAO(-1, -1, 2, 2);
	}

	public static boolean boundsCheck(int x, int y) {
		return x < 20 && x > -20 && y < 20 && y > -20;
	}
}
