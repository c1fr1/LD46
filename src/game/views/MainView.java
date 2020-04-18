package game.views;

import engine.*;
import engine.OpenGL.*;
import game.UserControls;
import game.entities.Player;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.*;

public class MainView extends EnigView {
	public static MainView main;

	private game.entities.Player player;

	boolean haslost = true;

	public MainView(EnigWindow window) {
		super(window);
		glDisable(GL_CULL_FACE);
		player = new Player();

		glClearColor(0.529f, 0.808f, 0.922f, 1.0f);
	}
	
	public void reset() {
		player.reset();
	}
	
	public boolean loop() {

		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}
}
