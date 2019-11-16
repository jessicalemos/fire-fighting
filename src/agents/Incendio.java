package agents;

import java.io.Serializable;

public class Incendio implements Serializable{

	private int gravidade;
	private int pos_x;
	private int pos_y;

	public Incendio() {
		this.gravidade=0;
		this.pos_x=0;
		this.pos_y=0;
	}

	public Incendio(int gravidade, int pos_x, int pos_y) {
		super();
		this.gravidade = gravidade;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
	}

	public int getGravidade() {
		return gravidade;
	}
	public void setGravidade(int gravidade) {
		this.gravidade = gravidade;
	}
	public int getpos_x() {
		return pos_x;
	}
	public void setpos_x(int pos_x) {
		this.pos_x = pos_x;
	}
	public int getpos_y() {
		return pos_y;
	}
	public void setpos_y(int pos_y) {
		this.pos_y = pos_y;
	}
}
