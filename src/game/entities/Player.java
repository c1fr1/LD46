package game.entities;
import org.joml.Vector2f;

public class Player extends Vector2f {
	int xv;
	int yv;
	int length;

	public Player() {
		xv = 0;
		yv = -1;
		length = 0;
	}

	public void reset() {
		xv = 0;
		yv = -1;
		length = 0;
	}
}
