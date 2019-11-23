package agents;

import java.io.Serializable;

public class Incendio implements Serializable{

	private int gravidade;
	private Posicao pos;

	public Incendio() {
		this.gravidade=0;
		this.pos = new Posicao(0,0);
	}

	public Incendio(int gravidade, int pos_x, int pos_y) {
		super();
		this.gravidade = gravidade;
		this.pos = new Posicao(pos_x,pos_y);
	}

	public int getGravidade() {
		return gravidade;
	}

	public void setGravidade(int gravidade) {
		this.gravidade = gravidade;
	}

	public Posicao getPos() {
		return pos;
	}

	public void setpos_x(Posicao pos) {
		this.pos.setX(pos.getX());
		this.pos.setY(pos.getY());
	}

}
