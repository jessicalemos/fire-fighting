package agents;

import java.io.Serializable;

public class Incendio implements Serializable{
	private int extinto; //0 - ativo, 1 - em combate, 2 - extinto
	private int gravidade;
	private Posicao pos;

	public Incendio() {
		this.extinto = 0;
		this.gravidade = 0;
		this.pos = new Posicao(0,0);
	}

	public Incendio(int gravidade, int pos_x, int pos_y) {
		super();
		this.extinto = 0;
		this.gravidade = gravidade;
		this.pos = new Posicao(pos_x,pos_y);
	}

	public int getGravidade() {
		return gravidade;
	}

	public int getExtinto() {
		return extinto;
	}
	
	public void setGravidade(int gravidade) {
		this.gravidade = gravidade;
	}
	
	public void setExtinto(int extinto) {
		this.extinto = extinto;
	}

	public Posicao getPos() {
		return pos;
	}

	public void setpos_x(Posicao pos) {
		this.pos.setX(pos.getX());
		this.pos.setY(pos.getY());
	}
}
