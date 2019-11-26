package agents;

import java.io.Serializable;

public class Incendio implements Serializable{
	private boolean extinto;
	private int gravidade;
	private Posicao pos;

	public Incendio() {
		this.extinto = false;
		this.gravidade=0;
		this.pos = new Posicao(0,0);
	}

	public Incendio(int gravidade, int pos_x, int pos_y) {
		super();
		this.extinto = false;
		this.gravidade = gravidade;
		this.pos = new Posicao(pos_x,pos_y);
	}

	public int getGravidade() {
		return gravidade;
	}

	public boolean getExtinto() {
		return extinto;
	}
	
	public void setGravidade(int gravidade) {
		this.gravidade = gravidade;
	}
	
	public void setExtinto(boolean extinto) {
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
