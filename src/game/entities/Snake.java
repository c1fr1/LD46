package game.entities;

public class Snake {
	public int x = 0;
	public int y = 0;
	public Snake tail;

	private Snake(int x, int y) {
		this.x = x;
		this.y = y;
		tail = null;
	}

	public Snake(int x0, int y0, int xv, int yv, int len) {
		Snake currSnake = this;
		currSnake.x = x0;
		currSnake.y = y0;
		for (int i = 1; i < len; ++i) {
			currSnake.tail = new Snake(x0 + i * xv, y0 + i * yv);
			currSnake = currSnake.tail;
		}
	}

	public Snake last() {
		if (tail == null) {
			return this;
		} else {
			return tail.last();
		}
	}

	public int length() {
		if (tail == null) {
			return 1;
		}
		return 1 + tail.length();
	}

	public void dropTail() {
		Snake last = tail;
		if (last.tail == null) {
			tail = null;
			return;
		}
		while (last.tail.tail != null) {
			last = last.tail;
		}
		last.tail = null;
	}
}
