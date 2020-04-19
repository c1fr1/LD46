package game.views;

import engine.*;
import engine.OpenGL.*;
import game.Shaders;
import game.UserControls;
import game.entities.Enemy;
import game.entities.Player;
import game.entities.Snake;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;

import static game.Main.boundsCheck;
import static org.lwjgl.opengl.GL11C.*;

public class MainView extends EnigView {
	public static MainView main;

	private game.entities.Player player;

	private float hYPeMOdETimer = 0.1f;

	float monsterHP = 1;

	VAO boxVAO;
	int newCandyTimer;

	Matrix4f mainCam;

	ArrayList<Vector2i> candies = new ArrayList<>();

	ArrayList<Enemy> enemies = new ArrayList<>();
	int newEnemyTimer = 20;

	public MainView(EnigWindow window) {
		super(window);
		glDisable(GL_CULL_FACE);
		player = new Player();

		mainCam = window.getSquarePerspectiveMatrix(100);

		boxVAO = new VAO(-0.9f, -0.9f, 1.8f, 1.8f);

		glDisable(GL_CULL_FACE);

		glClearColor(0f, 0f, 0f, 1.0f);
	}
	
	public void reset() {
		player.reset();
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
			monsterHP = 0;
		}

		renderWall();
		renderCandies();
		renderPlayer();
		renderEnemies();

		Shaders.colorShader.setUniform(2, 0, 0.0f, 1.0f, 0.0f);
		Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(0, 45, 0).scale(monsterHP * 41f, 1f, 1f));
		boxVAO.fullRender();

		if (hYPeMOdETimer < 0) {
			hYPeMOdETimer += 0.1;
			monsterHP -= 0.002;
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
		while (location != null) {
			Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(2 * location.x, 2 * location.y, 0f));
			boxVAO.drawTriangles();
			location = location.tail;
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
			while (location != null) {
				if (boundsCheck(location.x, location.y)) {
					Shaders.colorShader.setUniform(0, 0, new Matrix4f(mainCam).translate(2 * location.x, 2 * location.y, 0f));
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
	}

	public void movePlayer() {
		player.addHead();
		if (player.snake.x >= 20 || player.snake.x <= -20 || player.snake.y <= -20) {
			player.reset();
			return;
		}
		if (player.snake.y >= 20) {
			if (player.snake.x >= -2 && player.snake.x <= 2) {
				if (player.snake.last().y > 25) {
					player.reset();
				} else {
					if (player.snake.y > 29) {
						player.snake = player.snake.tail;
						monsterHP += 0.1;
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
					return;
				}
				loc = loc.tail;
			}
		}
		int candyIdx = candyAt(player.snake.x, player.snake.y);
		if (candyIdx >= 0) {
			candies.remove(candyIdx);
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
						newCandyTimer += 20;
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
			if (Math.random() < 0.25) {
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
