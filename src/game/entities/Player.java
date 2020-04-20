package game.entities;
import org.joml.Vector2f;

public class Player {

	public Snake snake;

	public int xv;
	public int yv;

	public int candiesGiven;

	public Player() {
		xv = 0;
		yv = -1;
		snake = new Snake(0, 20, 0, 1, 4);
	}

	public Player(int x0, int y0, int xv, int yv, int len) {
		this.xv = xv;
		this.yv = yv;
		snake = new Snake(x0, y0, -xv, -yv, len);
	}

	public void reset() {
		xv = 0;
		yv = -1;
		candiesGiven = 0;
		snake = new Snake(0, 20, 0, 1, 4);
	}

	public void dropTail() {
		snake.dropTail();
	}

	public void addHead() {
		Snake nSnake = new Snake(snake.x + xv, snake.y + yv, 0, 0, 1);
		nSnake.tail = snake;
		snake = nSnake;
	}

	public int length() {
		return snake.length();
	}

	public float feedBonus() {
		float ret = 0f;
		for (int i = length(); i > 4; --i) {
			ret += 0.02 + Math.log(i - 3) * 0.02;
		}
		return ret;
	}
}
