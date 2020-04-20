package game.views;

import engine.EnigView;
import engine.OpenGL.*;
import game.Shaders;
import game.UserControls;
import game.entities.Player;
import game.entities.Snake;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11C.*;

public class MenuView extends EnigView {
	public static MenuView main;

	private Player player;

	private float hYPeMOdETimer = 0.1f;
	private float hypeModDuration = 0.1f;

	private VAO boxVAO;
	private VAO buttonVAO;

	private Matrix4f mainCam;

	private Texture playButtonTex;
	private Texture lernButtonTex;
	private Texture quitButtonTex;

	private int hlSegmentIndex;
	private Vector2f tpInitCursorPosition;

	private int selection = 0;

	public MenuView(EnigWindow window) {
		super(window);
		player = new Player(2, 6, 0, 1, 5);

		mainCam = window.getAlignedPerspectiveMatrix(100);

		playButtonTex = new Texture("res/textures/playButton.png");
		lernButtonTex = new Texture("res/textures/learnButton.png");
		quitButtonTex = new Texture("res/textures/quitButton.png");

		boxVAO = new VAO(-0.9f, -0.9f, 1.8f, 1.8f);
		buttonVAO = new VAO(0, -15f, 64f, 30f);

		glClearColor(0f, 0f, 0f, 1.0f);
	}
	
	public void reset() {
		player.reset();
	}
	
	public boolean loop() {

		FBO.prepareDefaultRender();

		hYPeMOdETimer -= deltaTime;

		updatePlayer();
		updateSelection();

		renderPlayer();
		renderStrawberry();
		renderButtons();

		/*TODO
		 *
		 * menus
		 * tutorial
		 * sfx/polish
		 * improve enemy ai
		 * fix feeding hp bar
		 * score
		 * more kill enemies options
		 */


		if (hYPeMOdETimer < 0) {
			hYPeMOdETimer += hypeModDuration;
		}

		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}

	public void renderPlayer() {
		Shaders.colorShader.enable();
		Snake location  = player.snake;
		Shaders.colorShader.setUniform(2, 0, 0f, 1f, 0f);
		boxVAO.prepareRender();
		int i = 0;
		while (location != null) {
			if (tpInitCursorPosition != null && i == hlSegmentIndex) {
				Shaders.colorShader.setUniform(2, 0, 0, 0, 1);
			} else {
				Shaders.colorShader.setUniform(2, 0, 0, 0.3f + 0.1f * i, 0);
			}
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(2 * location.x, 2 * location.y, 0f));
			boxVAO.drawTriangles();
			location = location.tail;
			++i;
		}
		boxVAO.unbind();
	}

	public void renderButtons() {
		Shaders.textureShader.enable();
		Shaders.textureShader.setUniform(2, 0, 32f);
		Shaders.textureShader.setUniform(2, 1, 15f);
		Shaders.textureShader.setUniform(0, 0, new Matrix4f(mainCam).translate(1f, 0, 0));
		lernButtonTex.bind();
		buttonVAO.prepareRender();
		buttonVAO.drawTriangles();
		Shaders.textureShader.setUniform(0, 0, new Matrix4f(mainCam).translate(1f, 30f, 0));
		playButtonTex.bind();
		buttonVAO.drawTriangles();
		Shaders.textureShader.setUniform(0, 0, new Matrix4f(mainCam).translate(1f, -30f, 0));
		quitButtonTex.bind();
		buttonVAO.drawTriangles();
		buttonVAO.unbind();
	}

	public void renderStrawberry() {
		Shaders.colorShader.enable();
		Shaders.colorShader.setUniform(2, 0, 1f, 0f, 0f);
		if (selection == 0) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(62f, 30f, 0f));
		} else if (selection == 1) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(62f, 0f, 0f));
		} else if (selection == 2) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(62f, -30f, 0f));
		}
		boxVAO.fullRender();
	}

	public void updateSelection() {
		float x = window.getCursorXAligned(100);
		float y = window.getCursorYScaled(100);
		if (x > 1f && x < 65f) {
			if (y < 45 && y > -45) {
				if (y > 15) {
					selection = 0;
					if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 3) {
						MainView.main.reset();
						MainView.main.tutorialStage = -1;
						MainView.main.runLoop();
						hYPeMOdETimer = hypeModDuration;
						frameStartTime = (float) System.nanoTime()/1e9f;
					}
				} else if (y < -15) {
					selection = 2;
					if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 3) {
						glfwSetWindowShouldClose(window.id, true);
						hYPeMOdETimer = hypeModDuration;
						frameStartTime = (float) System.nanoTime()/1e9f;
					}
				} else {
					selection = 1;
					if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 3) {
						MainView.main.reset();
						MainView.main.tutorialStage = 0;
						MainView.main.runLoop();
						hYPeMOdETimer = hypeModDuration;
						frameStartTime = (float) System.nanoTime()/1e9f;
					}
				}
			}
		}
	}

	public void updatePlayer() {
		if (player.xv > 0) {
			if (player.snake.x == 31) {
				player.yv = -1;
				player.xv = 0;
			}
		} else if (player.snake.x == 2) {
			if (selection == 0) {
				player.yv = 1;
				player.xv = 0;
				if (player.snake.y == 21) {
					player.xv = 1;
					player.yv = 0;
				}
			} else if (selection == 1) {
				if (player.snake.y > 6) {
					player.yv = -1;
					player.xv = 0;
				} else if (player.snake.y < 6) {
					player.yv = 1;
					player.xv = 0;
				} else {
					player.xv = 1;
					player.yv = 0;
				}
			} else if (selection == 2) {
				if (player.snake.y > -9) {
					player.yv = -1;
					player.xv = 0;
				} else if (player.snake.y < -9) {
					player.yv = 1;
					player.xv = 0;
				} else {
					player.xv = 1;
					player.yv = 0;
				}
			}
		} else if (player.yv > 0) {
			if (selection == 0) {
				if (player.snake.y == 21) {
					player.yv = 0;
					player.xv = 1;
				}
			} else if (selection == 1) {
				if (player.snake.y == 6) {
					player.yv = 0;
					player.xv = 1;
				}
			} else {
				if (player.snake.y == -9) {
					player.yv = 0;
					player.xv = 1;
				}
			}
		} else if (player.yv < 0) {
			if (player.snake.x == 31) {
				if (player.snake.y == -6 || player.snake.y == -21 || player.snake.y == 9) {
					player.yv = 0;
					player.xv = -1;
				}
			} else if (player.snake.x == 2) {
				if (selection == 0) {
					if (player.snake.y == 21) {
						player.yv = 0;
						player.xv = 1;
					}
				} else if (selection == 1) {
					if (player.snake.y == 6) {
						player.yv = 0;
						player.xv = 1;
					}
				} else {
					if (player.snake.y == -9) {
						player.yv = 0;
						player.xv = 1;
					}
				}
			}
		}

		if (hYPeMOdETimer < 0) {
			movePlayer();
		}
	}

	public void movePlayer() {
		player.addHead();
		if (!playerShouldStrawberry()) {
			player.dropTail();
		}
	}

	public boolean playerShouldStrawberry() {
		if (player.snake.x != 31) {
			return false;
		}
		if (player.snake.y == 15 && selection == 0) {
			return true;
		}
		if (player.snake.y == 0 && selection == 1) {
			return true;
		}
		if (player.snake.y == -15 && selection == 2) {
			return true;
		}
		return false;
	}
}
