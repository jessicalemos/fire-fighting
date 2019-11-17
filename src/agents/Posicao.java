package agents;
import java.io.Serializable;

public class Posicao implements Serializable {
	private int x;
	private int y;
	
	public Posicao(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
