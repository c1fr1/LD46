package game.entities;
import org.joml.Vector2f;

public class Player {

	public Snake snake;

	public int xv;
	public int yv;

	public Player() {
		xv = 0;
		yv = -1;
		snake = new Snake(0, 20, 0, 1, 5);
	}

	public void reset() {
		xv = 0;
		yv = -1;
		snake = new Snake(0, 20, 0, 1, 5);
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
}
