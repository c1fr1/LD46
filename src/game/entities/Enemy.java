package game.entities;

import org.joml.Vector2i;

import java.util.ArrayList;

import static game.Main.boundsCheck;

public class Enemy {
	int xv = 0;
	int yv = 0;
	public Snake snake;
	public Enemy() {
		double side = Math.random();
		if (side < 0.3) {
			int x;
			if (side < 0.125) {
				x = (int) (side * 8f * 17) - 19;
			} else {
				x = (int) ((side - 0.125) * 8f * 17) + 3;
			}
			snake = new Snake(x, 20, 0, 1, 5);
			yv = -1;
		} else if (side < 0.5f) {
			int x = (int) ((side - 0.25) * 4f * 40f) - 20;
			snake = new Snake(x, -20, 0, -1, 5);
			yv = 1;
		} else if (side < 0.75f) {
			int y = (int) ((side - 0.5) * 4f * 40f) - 20;
			snake = new Snake(20, y, 0, 1, 5);
			xv = -1;
		} else {
			int y = (int) ((side - 0.75) * 4f * 40f) - 20;
			snake = new Snake(-20, y, 0, 1, 5);
			xv = 1;
		}
	}

	public void addHead() {
		Snake nSnake = new Snake(snake.x + xv, snake.y + yv, 0, 0, 1);
		nSnake.tail = snake;
		snake = nSnake;
	}

	public void dropTail() {
		snake.dropTail();
	}

	private boolean runAI(ArrayList<Vector2i> strawberries) {
		int closest = -1;
		int closestDist = Integer.MAX_VALUE;
		for (int i = 0; i < strawberries.size(); ++i) {
			int distance = Math.abs(strawberries.get(i).x - snake.x) + Math.abs(strawberries.get(i).y - snake.y);
			if (distance < closestDist) {
				closest = i;
				closestDist = distance;
			}
		}
		if (closest < 0) {
			if (snake.x > 18) {
				if (xv == 1) {
					xv = 0;
					if (Math.random() < 0.5) {
						yv = 1;
					} else {
						yv = -1;
					}
				} else if (xv == 0 && Math.random() < 0.5) {
					yv = 0;
					xv = -1;
				}
			} else if (snake.x < -18) {
				if (yv == -1) {
					xv = 0;
					if (Math.random() < 0.5) {
						yv = 1;
					} else {
						yv = -1;
					}
				} else if (yv == 0 && Math.random() < 0.5) {
					xv = 0;
					yv = 1;
				}
			}
			if (snake.y > 18) {
				if (yv == 1) {
					yv = 0;
					if (Math.random() < 0.5) {
						xv = 1;
					} else {
						xv = -1;
					}
				} else if (yv == 0 && Math.random() < 0.5) {
					xv = 0;
					yv = -1;
				}
			} else if (snake.y < -18) {
				if (yv == 1) {
					yv = 0;
					if (Math.random() < 0.5) {
						xv = 1;
					} else {
						xv = -1;
					}
				} else if (yv == 0 && Math.random() < 0.5) {
					xv = 0;
					yv = 1;
				}
			}
			return false;
		}
		if (closestDist == 0) {
			strawberries.remove(closest);
			return true;
		}
		if (Math.random() < 0.3) {
			return false;
		}
		if (xv != 0) {
			if ((strawberries.get(closest).x - snake.x) / xv < 0) {
				if (strawberries.get(closest).y - snake.y != 0 && Math.random() < 0.5) {
					yv = (strawberries.get(closest).y - snake.y) / Math.abs(strawberries.get(closest).y - snake.y);
					xv = 0;
				} else {
					xv *= -1;
				}
			} else if (strawberries.get(closest).x - snake.x == 0) {
				yv = (strawberries.get(closest).y - snake.y) / Math.abs(strawberries.get(closest).y - snake.y);
				xv = 0;
			}
		} else {
			if ((strawberries.get(closest).y - snake.y) / yv < 0) {
				if (strawberries.get(closest).x - snake.x != 0 && Math.random() < 0.5) {
					xv = (strawberries.get(closest).x - snake.x) / Math.abs(strawberries.get(closest).x - snake.x);
					yv = 0;
				} else {
					yv *= -1;
				}
			} else if (strawberries.get(closest).y - snake.y == 0) {
				xv = (strawberries.get(closest).x - snake.x) / Math.abs(strawberries.get(closest).x - snake.x);
				yv = 0;
			}
		}
		return false;
	}

	public boolean updateMovement(Snake player, ArrayList<Vector2i> strawberries) {
		if (boundsCheck(snake.x, snake.y)) {
			if (!runAI(strawberries)) {
				dropTail();
			}
		} else {
			dropTail();
		}
		addHead();
		if (!boundsCheck(snake.x, snake.y)) {
			Snake tail = snake.last();
			if (!boundsCheck(tail.x, tail.y)) {
				return true;
			}
		}
		while (player != null) {
			if (snake.x == player.x && snake.y == player.y) {
				return true;
			}
			player = player.tail;
		}
		return false;
	}

}
