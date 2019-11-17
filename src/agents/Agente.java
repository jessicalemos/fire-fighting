package agents;

import java.io.Serializable;

public class Agente implements Serializable {
private double combustivel;
private int agua;
private int pos_x;
private int pos_y;
private boolean disponivel;
	public Agente() {
		int combustivel=0;
		int agua=0;
		int pos_x=0;
		int pos_y=0;
		boolean disponivel=true;
	}
	public Agente(double combustivel,int agua,int pos_x,int pos_y,boolean disponivel) {
		super();
		this.combustivel=combustivel;
		this.agua=agua;
		this.pos_x=pos_x;
		this.pos_y=pos_y;
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
	public int getPos_x() {
		return pos_x;
	}
	public void setPos_x(int pos_x) {
		this.pos_x = pos_x;
	}
	public int getPos_y() {
		return pos_y;
	}
	public void setPos_y(int pos_y) {
		this.pos_y = pos_y;
	}
	public boolean isDisponivel() {
		return disponivel;
	}
	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
}
