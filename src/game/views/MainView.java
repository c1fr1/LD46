package game.views;

import engine.*;
import engine.OpenGL.*;
import game.Shaders;
import game.UserControls;
import game.entities.Enemy;
import game.entities.Player;
import game.entities.Snake;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static game.Main.boundsCheck;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11C.*;

public class MainView extends EnigView {
	public static MainView main;

	private game.entities.Player player;

	private float hYPeMOdETimer = 0.1f;
	float hypeModDuration = 0.12f;

	float monsterHP = 1;

	VAO boxVAO;
	VAO tutVAO;

	int newCandyTimer;

	Matrix4f mainCam;

	ArrayList<Vector2i> candies = new ArrayList<>();

	ArrayList<Enemy> enemies = new ArrayList<>();
	int newEnemyTimer = 15;

	int hlSegmentIndex;
	Vector2f tpInitCursorPosition;

	int tutorialStage = -1;

	private Texture tutMove;
	private Texture tutTele;
	private Texture tutMastah;

	public MainView(EnigWindow window) {
		super(window);
		glDisable(GL_CULL_FACE);
		player = new Player();

		mainCam = window.getSquarePerspectiveMatrix(100);

		boxVAO = new VAO(-0.9f, -0.9f, 1.8f, 1.8f);
		tutVAO = new VAO(-0.5f, 0, 1, 1);

		tutMove = new Texture("res/textures/tutorial/move.png");
		tutTele = new Texture("res/textures/tutorial/teleport.png");
		tutMastah = new Texture("res/textures/tutorial/mastah.png");

		glDisable(GL_CULL_FACE);

		glClearColor(0f, 0f, 0f, 1.0f);
	}
	
	public void reset() {
		player.reset();
		hypeModDuration = 0.12f;
		hYPeMOdETimer = 0.1f;
		monsterHP = 1;
		newEnemyTimer = 15;
		tutorialStage = -1;
		candies.clear();
		enemies.clear();
	}
	
	public boolean loop() {

		FBO.prepareDefaultRender();

		hYPeMOdETimer -= deltaTime;

		updatePlayer();
		handleCandies();
		updateEnemies();

		if (monsterHP > 1) {
			monsterHP = 1;
		}
		if (monsterHP < 0) {
			if (tutorialStage == -1) {
				return true;
			} else {
				int temp = tutorialStage;
				reset();
				tutorialStage = temp;
			}
		}

		renderWall();
		renderCandies();
		renderPlayer();
		renderEnemies();
		renderHPBar();

		renderTutorialFrame();
		/*TODO
		 *
		 * tutorial
		 * sfx/polish
		 * improve enemy ai
		 * fix feeding hp bar
		 * score
		 * more kill enemies options
		 */

		if (hYPeMOdETimer < 0) {
			hYPeMOdETimer += hypeModDuration;
			monsterHP -= 0.001;
			hypeModDuration *= 0.9999;
		}

		if (UserControls.quit(window)) {
			return true;
		}
		return false;
	}

	public void renderHPBar() {
		boxVAO.prepareRender();

		Shaders.colorShader.setUniform(2, 0, 0.0f, 1.0f, 0.0f);
		Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0, 45, 0).scale(monsterHP * 41f, 1f, 1f));
		boxVAO.drawTriangles();
		Shaders.colorShader.setUniform(2, 0, 0.5f, 0.5f, 0f);
		float feedAmount = (monsterHP + player.feedBonus());
		if (feedAmount > 1) {
			feedAmount = 1;
		}
		Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0, 45, 0).scale((feedAmount) * 41f, 1f, 1f));
		boxVAO.drawTriangles();
		Shaders.colorShader.setUniform(2, 0, 0.0f, 0.2f, 0.0f);
		Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0, 45, 0).scale(42f, 1.5f, 1f));
		boxVAO.drawTriangles();

		boxVAO.unbind();
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

	public void renderEnemies() {
		Shaders.colorShader.enable();
		Shaders.colorShader.setUniform(2, 0, 1f, 0f, 1f);
		boxVAO.prepareRender();
		Snake location;
		for (int i = 0; i < enemies.size(); ++i) {
			location = enemies.get(i).snake;
			float clr = 0.3f;
			while (location != null) {
				if (boundsCheck(location.x, location.y)) {
					Shaders.colorShader.setUniform(2, 0, clr, 0f, clr);
					Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(2 * location.x, 2 * location.y, 0f));
					clr += 0.1f;
					boxVAO.drawTriangles();
				}
				location = location.tail;
			}
		}
		boxVAO.unbind();
	}

	public void renderCandies() {
		Shaders.colorShader.enable();
		Shaders.colorShader.setUniform(2, 0, 1f, 0f, 0f);
		boxVAO.prepareRender();
		for (Vector2i loc:candies) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(2 * loc.x, 2 * loc.y, 0f));
			boxVAO.drawTriangles();
		}
		boxVAO.unbind();
	}

	public void renderWall() {
		Shaders.colorShader.enable();
		Shaders.colorShader.setUniform(2, 0, 0.5f, 0.5f, 0.5f);
		boxVAO.prepareRender();

		for (int i = 1; i < 40; ++i) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(-40.0f + i * 2f, -40.0f, 0f));
			boxVAO.drawTriangles();
		}
		for (int i = 0; i < 18; ++i) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(-40.0f + i * 2f, 40.0f, 0f));
			boxVAO.drawTriangles();
		}
		for (int i = 23; i < 40; ++i) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(-40.0f + i * 2f, 40.0f, 0f));
			boxVAO.drawTriangles();
		}
		for (int i = 0; i < 40; ++i) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(-40.0f, -40.0f + i * 2f, 0f));
			boxVAO.drawTriangles();
		}
		for (int i = 0; i <= 40; ++i) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(40.0f, -40.0f + i * 2f, 0f));
			boxVAO.drawTriangles();
		}

		boxVAO.unbind();
	}

	public void renderTutorialFrame() {
		if (tutorialStage >= 0) {
			if (tutorialStage == 0) {
				tutVAO.prepareRender();
				Shaders.textureShader.enable();
				Shaders.textureShader.setUniform(2, 0, 23f);
				Shaders.textureShader.setUniform(2, 1, 23f);
				Shaders.textureShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0f, 0f, 0f).scale(23f, 23f, 1f));
				tutMove.bind();
			} else if (tutorialStage == 1) {
				tutVAO.prepareRender();
				Shaders.textureShader.enable();;
				Shaders.textureShader.setUniform(2, 0, 59f);
				Shaders.textureShader.setUniform(2, 1, 23f);
				Shaders.textureShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0f, 0f, 0f).scale(59f, 23f, 1f));
				tutTele.bind();
			} else if (tutorialStage == 2) {
				tutVAO.prepareRender();
				Shaders.textureShader.enable();;
				Shaders.textureShader.setUniform(2, 0, 47f);
				Shaders.textureShader.setUniform(2, 1, 31f);
				Shaders.textureShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0f, 0f, 0f).scale(47f, 31f, 1f));
				tutMastah.bind();
			}
			tutVAO.drawTriangles();
			tutVAO.unbind();
		}
	}

	public void updatePlayer() {
		if (player.snake.y <= 20) {
			if (UserControls.up(window)) {
				player.xv = 0;
				player.yv = 1;
			} else if (UserControls.down(window)) {
				player.xv = 0;
				player.yv = -1;
			} else if (UserControls.left(window)) {
				player.xv = -1;
				player.yv = 0;
			} else if (UserControls.right(window)) {
				player.xv = 1;
				player.yv = 0;
			}
		}

		if (hYPeMOdETimer < 0) {
			movePlayer();
		}
		if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 1) {
			float minDistance = Float.MAX_VALUE;
			float cursorX = window.getCursorXScaled(100) / 2;
			float cursorY = window.getCursorYScaled(100) / 2;
			hlSegmentIndex = 0;
			int i = 0;
			Snake segment = player.snake;
			while (segment != null) {
				float dx = segment.x - cursorX;
				float dy = segment.y - cursorY;
				float distance = dx * dx + dy * dy;
				if (distance < minDistance) {
					minDistance = distance;
					hlSegmentIndex = i;
				}
				segment = segment.tail;
				++i;
			}
			tpInitCursorPosition = new Vector2f(cursorX, cursorY);
		} else if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 2 && tpInitCursorPosition != null) {
			float minDistance = Float.MAX_VALUE;
			hlSegmentIndex = 0;
			int i = 0;
			Snake segment = player.snake;
			while (segment != null) {
				float dx = segment.x - tpInitCursorPosition.x;
				float dy = segment.y - tpInitCursorPosition.y;
				float distance = dx * dx + dy * dy;
				if (distance < minDistance) {
					minDistance = distance;
					hlSegmentIndex = i;
				}
				segment = segment.tail;
				++i;
			}
		}

		if (window.mouseButtons[GLFW_MOUSE_BUTTON_LEFT] == 3 && tpInitCursorPosition != null) {
			Snake segment = player.snake;
			while (hlSegmentIndex > 0) {
				segment = segment.tail;
				--hlSegmentIndex;
			}
			player.snake.x = segment.x;
			player.snake.y = segment.y;
			float dx = window.getCursorXScaled(100) / 2 - tpInitCursorPosition.x;
			float dy = window.getCursorYScaled(100) / 2 - tpInitCursorPosition.y;
			if (Math.abs(dx) > Math.abs(dy)) {
				if (dx > 0) {
					player.xv = 1;
					player.yv = 0;
				} else {
					player.xv = -1;
					player.yv = 0;
				}
			} else {
				if (dy > 0) {
					player.xv = 0;
					player.yv = 1;
				} else {
					player.xv = 0;
					player.yv = -1;
				}
			}
			tpInitCursorPosition = null;
			if (tutorialStage == 1) {
				++tutorialStage;
			}
		}
	}

	public void movePlayer() {
		player.addHead();
		if (player.snake.x >= 20 || player.snake.x <= -20 || player.snake.y <= -20) {
			player.reset();
			tpInitCursorPosition = null;
			return;
		}
		if (player.snake.y >= 20) {
			tpInitCursorPosition = null;
			if (player.snake.x >= -2 && player.snake.x <= 2) {
				if (player.snake.last().y > 25) {
					if (tutorialStage == 2) {
						reset();
						return;
					}
					player.reset();
					return;
				} else {
					if (player.snake.y > 29) {
						player.snake = player.snake.tail;
						monsterHP += 0.02 + Math.log(player.candiesGiven + 1) * 0.02;
						player.candiesGiven += 1;
					}
				}
			} else {
				player.reset();
				return;
			}
		}
		for (Enemy e:enemies) {
			Snake loc = e.snake;
			while (loc != null) {
				if (player.snake.x == loc.x && player.snake.y == loc.y) {
					player.reset();
					tpInitCursorPosition = null;
					return;
				}
				loc = loc.tail;
			}
		}
		int candyIdx = candyAt(player.snake.x, player.snake.y);
		if (candyIdx >= 0) {
			candies.remove(candyIdx);
			if (tutorialStage == 0) {
				tutorialStage = 1;
			}
		} else {
			player.dropTail();
		}
	}

	public void handleCandies() {
		if (hYPeMOdETimer < 0) {
			if (Math.random() < 0.5) {
				newCandyTimer -= 1;
				if (newCandyTimer < 0) {
					if (candies.size() < 10) {
						int x = ((int)(Math.random() * 39)) - 19;
						int y = ((int)(Math.random() * 39)) - 19;
						if (candyAt(x, y) == -1) {
							candies.add(new Vector2i(x, y));
						}
						newCandyTimer += 10;
					} else {
						newCandyTimer += 1;
					}
				}
			}
		}
	}

	public int candyAt(int x, int y) {
		for (int i = 0; i < candies.size(); ++i) {
			if (candies.get(i).x == x && candies.get(i).y == y) {
				return i;
			}
		}
		return -1;
	}

	public void updateEnemies() {
		if (hYPeMOdETimer < 0) {
			if (Math.random() < 0.4) {
				--newEnemyTimer;
				if (newEnemyTimer < 0) {
					enemies.add(new Enemy());
					newEnemyTimer = 20;
				}
			}
			for (int i = 0; i < enemies.size(); ++i) {
				if (enemies.get(i).updateMovement(player.snake, candies)) {
					enemies.remove(i);
					--i;
				}
			}
		}
	}
}
