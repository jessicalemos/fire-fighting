package agents;

import java.io.Serializable;

public class Agente implements Serializable {
	private double combustivel;
	private int agua;
	private Posicao pos;
	private boolean disponivel;

	public Agente() {
		int combustivel=0;
		int agua=0;
		Posicao pos;
		boolean disponivel=true;
	}
	public Agente(double combustivel,int agua,int pos_x,int pos_y,boolean disponivel) {
		super();
		this.combustivel=combustivel;
		this.agua=agua;
		this.pos = new Posicao(pos_x,pos_y);
		this.disponivel=disponivel;
	}

	public double getCombustivel() {
		return combustivel;
	}
	
	public void setCombustivel(double combustivel) {
		this.combustivel = combustivel;
	}
	
	public int getAgua() {
		return agua;
	}
	
	public void setAgua(int agua) {
		this.agua = agua;
	}
	
	public Posicao getPos() {
		return pos;
	}

	public void setPos(Posicao post) {
		this.pos.setX(pos.getX());
		this.pos.setY(post.getY());
	}
	
	public boolean isDisponivel() {
		return disponivel;
	}
	
	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
}
